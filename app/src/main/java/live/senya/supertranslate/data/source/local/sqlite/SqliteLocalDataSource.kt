package live.senya.supertranslate.data.source.local.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.squareup.sqlbrite2.BriteDatabase
import com.squareup.sqlbrite2.SqlBrite
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.local.LocalDataSource
import live.senya.supertranslate.schedulers.BaseSchedulerProvider

class SqliteLocalDataSource(
    context: Context,
    schedulerProvider: BaseSchedulerProvider,
    private val currentLocale: String = context.resources.configuration.locale.language
) : LocalDataSource {

  private val selectSourceLangSql = """
            |(
            |SELECT
            |${LangTable.COLUMN_NAME_NAME}
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_CODE} = ${TranslationTable.COLUMN_NAME_SOURCE_LANG}
            |AND
            |${LangTable.COLUMN_NAME_LOCALE} = '$currentLocale'
            |) AS
            |${UtilNames.SOURCE}
        """.trimMargin()

  private val selectTargetLangSql = """
            |(
            |SELECT
            |${LangTable.COLUMN_NAME_NAME}
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_CODE} = ${TranslationTable.COLUMN_NAME_TARGET_LANG}
            |AND
            |${LangTable.COLUMN_NAME_LOCALE} = '$currentLocale'
            |)
            |AS ${UtilNames.TARGET}
        """.trimMargin()

  private val dbHelper = SqlBrite.Builder()
      .build()
      .wrapDatabaseHelper(
          DbHelper(context),
          schedulerProvider.io()
      )

  override fun saveLang(lang: Lang) {
    val values = ContentValues()

    with(values) {
      put(LangTable.COLUMN_NAME_CODE, lang.code)
      put(LangTable.COLUMN_NAME_NAME, lang.name)
      put(LangTable.COLUMN_NAME_LOCALE, lang.locale)
    }

    dbHelper.writableDatabase.insertWithOnConflict(
        LangTable.TABLE_NAME,
        null,
        values,
        SQLiteDatabase.CONFLICT_REPLACE
    )
  }

  override fun getLangs(): Single<List<Lang>> {
    val projection = arrayOf(
        LangTable.COLUMN_NAME_CODE,
        LangTable.COLUMN_NAME_NAME,
        LangTable.COLUMN_NAME_LOCALE
    ).joinToString(", ")

    val sql = """
            |SELECT
            |$projection
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_LOCALE} = ?
        """.trimMargin()

    return dbHelper.queryDb({ mapLang(it) }, sql, currentLocale)
  }

  override fun saveTranslation(translation: Translation) {
    val values = ContentValues()

    with(values) {
      put(TranslationTable.COLUMN_NAME_SOURCE_LANG, translation.sourceLang.code)
      put(TranslationTable.COLUMN_NAME_TARGET_LANG, translation.targetLang.code)
      put(TranslationTable.COLUMN_NAME_ORIGINAL_TEXT, translation.originalText)
      put(TranslationTable.COLUMN_NAME_TRANSLATED_TEXT, translation.translatedText)
      put(TranslationTable.COLUMN_NAME_IS_FAVORITE, translation.isFavorite)
      put(TranslationTable.COLUMN_NAME_ID, translation.id)
    }

    dbHelper.writableDatabase.insertWithOnConflict(
        TranslationTable.TABLE_NAME,
        null,
        values,
        SQLiteDatabase.CONFLICT_REPLACE
    )
  }

  override fun getTranslation(textToTranslate: TextToTranslate): Maybe<Translation> {

    val projection = arrayOf(
        selectSourceLangSql,
        selectTargetLangSql,
        TranslationTable.COLUMN_NAME_SOURCE_LANG,
        TranslationTable.COLUMN_NAME_TARGET_LANG,
        TranslationTable.COLUMN_NAME_ORIGINAL_TEXT,
        TranslationTable.COLUMN_NAME_TRANSLATED_TEXT,
        TranslationTable.COLUMN_NAME_IS_FAVORITE,
        TranslationTable.COLUMN_NAME_ID
    ).joinToString(", ")

    val sql = """
            |SELECT
            |$projection
            |FROM
            |${TranslationTable.TABLE_NAME}
            |WHERE
            |${TranslationTable.COLUMN_NAME_SOURCE_LANG} LIKE ?
            |AND
            |${TranslationTable.COLUMN_NAME_TARGET_LANG} LIKE ?
            |AND
            |${TranslationTable.COLUMN_NAME_ORIGINAL_TEXT} LIKE ?
        """.trimMargin()

    val selectionArgs = arrayOf(
        textToTranslate.sourceLang.code,
        textToTranslate.targetLang.code,
        textToTranslate.originalText
    )

    return Maybe.create<Translation> { e ->
      try {
        dbHelper.readableDatabase.rawQuery(sql, selectionArgs).use {
          if (it.count > 0) {
            it.moveToFirst()
            e.onSuccess(mapTranslation(it))
          } else {
            e.onComplete()
          }
        }
      } catch (exc: RuntimeException) {
        e.onError(exc)
      }
    }

  }

  override fun putTranslationOnTopOfHistory(translation: Translation) {
    val sql = """
            |UPDATE
            |${TranslationTable.TABLE_NAME}
            |SET
            |${TranslationTable.COLUMN_NAME_HISTORY_POSITION}
            |=
                |ifnull
                |(
                    |(
                    |SELECT
                    |max(${TranslationTable.COLUMN_NAME_HISTORY_POSITION}) + 1
                    |FROM
                    |${TranslationTable.TABLE_NAME}
                    |)
                |,
                    |0
                |)
            |WHERE
            |${TranslationTable.COLUMN_NAME_ID}
            |=
            |'${translation.id}'
            """.trimMargin()

    dbHelper.executeAndTrigger(TranslationTable.TABLE_NAME, sql)
  }

  override fun getHistory(): Single<List<Translation>> {
    val projection = arrayOf(
        selectSourceLangSql,
        selectTargetLangSql,
        TranslationTable.COLUMN_NAME_SOURCE_LANG,
        TranslationTable.COLUMN_NAME_TARGET_LANG,
        TranslationTable.COLUMN_NAME_ORIGINAL_TEXT,
        TranslationTable.COLUMN_NAME_TRANSLATED_TEXT,
        TranslationTable.COLUMN_NAME_IS_FAVORITE,
        TranslationTable.COLUMN_NAME_ID
    ).joinToString(", ")

    val sql = """
            |SELECT
            |$projection
            |FROM
            |${TranslationTable.TABLE_NAME}
            |WHERE
            |${TranslationTable.COLUMN_NAME_HISTORY_POSITION} IS NOT NULL
            |ORDER BY
            |${TranslationTable.COLUMN_NAME_HISTORY_POSITION}
        """.trimMargin()

    return dbHelper.queryDb({ mapTranslation(it) }, sql)
  }

  override fun getHistoryUpdates(): Observable<Translation> {
    val projection = arrayOf(
        selectSourceLangSql,
        selectTargetLangSql,
        TranslationTable.COLUMN_NAME_SOURCE_LANG,
        TranslationTable.COLUMN_NAME_TARGET_LANG,
        TranslationTable.COLUMN_NAME_ORIGINAL_TEXT,
        TranslationTable.COLUMN_NAME_TRANSLATED_TEXT,
        TranslationTable.COLUMN_NAME_IS_FAVORITE,
        TranslationTable.COLUMN_NAME_ID
    ).joinToString(", ")

    val sql = """
            |SELECT
            |$projection
            |FROM
            |${TranslationTable.TABLE_NAME}
            |WHERE
            |${TranslationTable.COLUMN_NAME_HISTORY_POSITION} IS NOT NULL
            |ORDER BY
            |${TranslationTable.COLUMN_NAME_HISTORY_POSITION} DESC
            |LIMIT 1
        """.trimMargin()

    return dbHelper.createQuery(TranslationTable.TABLE_NAME, sql)
        .mapToOne { mapTranslation(it) }
        .skip(1)
  }

  private fun mapLang(c: Cursor): Lang {
    val code = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_CODE))
    val name = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_NAME))
    val locale = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_LOCALE))

    return Lang(
        code = code,
        name = name,
        locale = locale
    )
  }

  private fun mapTranslation(c: Cursor): Translation {
    val langCodeSource = c.getString(c.getColumnIndexOrThrow(TranslationTable.COLUMN_NAME_SOURCE_LANG))
    val langNameSource = c.getString(c.getColumnIndexOrThrow(UtilNames.SOURCE))
    val langCodeTarget = c.getString(c.getColumnIndexOrThrow(TranslationTable.COLUMN_NAME_TARGET_LANG))
    val langNameTarget = c.getString(c.getColumnIndexOrThrow(UtilNames.TARGET))
    val originalText = c.getString(c.getColumnIndexOrThrow(TranslationTable.COLUMN_NAME_ORIGINAL_TEXT))
    val translatedText = c.getString(c.getColumnIndexOrThrow(TranslationTable.COLUMN_NAME_TRANSLATED_TEXT))
    val isFavorite = c.getInt(c.getColumnIndexOrThrow(TranslationTable.COLUMN_NAME_IS_FAVORITE)) == 1
    val id = c.getString(c.getColumnIndexOrThrow(TranslationTable.COLUMN_NAME_ID))

    return Translation(
        sourceLang = Lang(langCodeSource, langNameSource, currentLocale),
        originalText = originalText,
        targetLang = Lang(langCodeTarget, langNameTarget, currentLocale),
        translatedText = translatedText,
        isFavorite = isFavorite,
        id = id
    )
  }

  /**
   * Executes a received sql query and returns a list of items wrapped into Single.
   */
  private fun <T> BriteDatabase.queryDb(
      mapper: (Cursor) -> T,
      sql: String,
      vararg args: String): Single<List<T>> {
    return Single.create<List<T>> { e ->
      try {
        readableDatabase.rawQuery(sql, args).use { cursor ->
          val list = (1..cursor.count).map {
            cursor.moveToNext()
            mapper(cursor)
          }
          e.onSuccess(list)
        }
      } catch (exc: SQLException) {
        e.onError(exc)
      }
    }
  }
}
