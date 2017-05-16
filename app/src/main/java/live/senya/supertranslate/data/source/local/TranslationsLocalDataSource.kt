package live.senya.supertranslate.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.squareup.sqlbrite.SqlBrite
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.schedulers.BaseSchedulerProvider
import rx.Observable
import javax.inject.Inject

class TranslationsRxLocalDataSource @Inject constructor(context: Context,
                                                        schedulerProvider: BaseSchedulerProvider) {
//    private val currentLocale = Locale.getDefault().language
    private val currentLocale = "en"

    private val dbHelper = SqlBrite.Builder()
            .build()
            .wrapDatabaseHelper(TranslationsDbHelper(context),
                    schedulerProvider.io())

    fun saveLang(lang: Lang) {
        val values = ContentValues()

        with(values) {
            put(LangTable.COLUMN_NAME_CODE, lang.code)
            put(LangTable.COLUMN_NAME_NAME, lang.name)
            put(LangTable.COLUMN_NAME_LOCALE, lang.locale)
        }

        dbHelper.insert(LangTable.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getLangs(): Observable<MutableList<Lang>> {
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

    fun saveTranslation(translation: Translation) {
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

    fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation?> {
        val sourceLangSql = """
            |(
            |SELECT
            |${LangTable.COLUMN_NAME_NAME}
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_CODE} = ${TranslationTable.COLUMN_NAME_SOURCE_LANG}
            |AND
            |${LangTable.COLUMN_NAME_LOCALE} = $currentLocale
            |) AS
            |${UtilNames.SOURCE}
        """.trimMargin()
        val targetLangSql = """
            |(
            |SELECT
            |${LangTable.COLUMN_NAME_NAME}
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_CODE} = ${TranslationTable.COLUMN_NAME_TARGET_LANG}
            |AND
            |${LangTable.COLUMN_NAME_LOCALE} = $currentLocale
            |)
            |AS ${UtilNames.TARGET}
        """.trimMargin()

        val projection = arrayOf(
                sourceLangSql,
                targetLangSql,
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
                .mapToOneOrDefault({ mapTranslation(it) }, null)
                .take(1)

    }

    fun putTranslationOnTopOfHistory(translation: Translation){
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

    fun getHistory(): Observable<MutableList<Translation>>? {
        val sourceLangSql = """
            |(
            |SELECT
            |${LangTable.COLUMN_NAME_NAME}
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_CODE} = ${TranslationTable.COLUMN_NAME_SOURCE_LANG}
            |AND
            |${LangTable.COLUMN_NAME_LOCALE} = $currentLocale
            |) AS
            |${UtilNames.SOURCE}
        """.trimMargin()
        val targetLangSql = """
            |(
            |SELECT
            |${LangTable.COLUMN_NAME_NAME}
            |FROM
            |${LangTable.TABLE_NAME}
            |WHERE
            |${LangTable.COLUMN_NAME_CODE} = ${TranslationTable.COLUMN_NAME_TARGET_LANG}
            |AND
            |${LangTable.COLUMN_NAME_LOCALE} = $currentLocale
            |)
            |AS ${UtilNames.TARGET}
        """.trimMargin()

        val projection = arrayOf(
                sourceLangSql,
                targetLangSql,
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
                sourceLang = Lang(langCodeSource, langNameSource, "currentLocale"),
                originalText = originalText,
                targetLang = Lang(langCodeTarget, langNameTarget, "currentLocale"),
                translatedText = translatedText,
                isFavorite = isFavorite,
                id = id
        )
    }

}
