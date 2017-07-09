package live.senya.supertranslate.data.source.local

import android.content.Context
import android.database.Cursor
import com.squareup.sqlbrite2.SqlBrite
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.schedulers.BaseSchedulerProvider
import java.util.*

abstract class BaseLocalDataSource(context: Context,
                                   schedulerProvider: BaseSchedulerProvider) {

    val currentLocale: String = Locale.getDefault().language

    val selectSourceLangSql = """
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

    val selectTargetLangSql = """
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

    val dbHelper = SqlBrite.Builder()
            .build()
            .wrapDatabaseHelper(
                    DbHelper(context),
                    schedulerProvider.io()
            )

    fun mapLang(c: Cursor): Lang {
        val code = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_CODE))
        val name = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_NAME))
        val locale = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_LOCALE))

        return Lang(
                code = code,
                name = name,
                locale = locale
        )
    }

    fun mapTranslation(c: Cursor): Translation {
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

}
