package live.senya.supertranslate.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.reactivex.Observable
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.schedulers.BaseSchedulerProvider
import java.util.*
import javax.inject.Inject

class LocalDataSource @Inject constructor(context: Context,
                                          schedulerProvider: BaseSchedulerProvider,
                                          currentLocale: String = Locale.getDefault().language) :
        BaseLocalDataSource(context, schedulerProvider, currentLocale) {

    override fun saveLang(lang: Lang) {
        val values = ContentValues()

        with(values) {
            put(LangTable.COLUMN_NAME_CODE, lang.code)
            put(LangTable.COLUMN_NAME_NAME, lang.name)
            put(LangTable.COLUMN_NAME_LOCALE, lang.locale)
        }

        dbHelper.insert(LangTable.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun getLangs(): Observable<List<Lang>> {
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

        return dbHelper.createQuery(LangTable.TABLE_NAME, sql, currentLocale)
                .mapToList { mapLang(it) }
                .take(1)
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

        dbHelper.insert(TranslationTable.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation> {

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

        return dbHelper.createQuery(TranslationTable.TABLE_NAME, sql, *selectionArgs)
                .mapToOne({ mapTranslation(it) })
                .take(1)

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

        dbHelper.execute(sql)
    }

    override fun getHistory(): Observable<MutableList<Translation>> {
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

        return dbHelper.createQuery(TranslationTable.TABLE_NAME, sql)
                .mapToList { mapTranslation(it) }
                .take(1)
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
}