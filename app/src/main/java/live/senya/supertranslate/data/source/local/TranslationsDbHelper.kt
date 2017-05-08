package live.senya.supertranslate.data.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import live.senya.supertranslate.data.source.local.TranslationsPersistenceContract.DbInfo.DB_NAME
import live.senya.supertranslate.data.source.local.TranslationsPersistenceContract.DbInfo.DB_VERSION

class TranslationsDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        with(db!!) {
            execSQL(SQL_CREATE_LANG_TABLE)
            execSQL(SQL_CREATE_TRANSLATION_TABLE)
            execSQL(SQL_CREATE_HISTORY_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //noop
    }

}

private val SQL_CREATE_LANG_TABLE = """
    |CREATE TABLE
    |${LangTable.TABLE_NAME}
    |(
    |${LangTable.COLUMN_NAME_CODE} TEXT,
    |${LangTable.COLUMN_NAME_NAME} TEXT,
    |${LangTable.COLUMN_NAME_LOCALE} TEXT
    |)
""".trimMargin()

private val SQL_CREATE_HISTORY_TABLE = """
    |CREATE TABLE
    |${HistoryTable.TABLE_NAME}
    |(
    |${HistoryTable.COLUMN_NAME_POSITION} INTEGER DEFAULT 0,
    |${HistoryTable.COLUMN_NAME_TRANSLATION} TEXT
    |)
""".trimMargin()

private val SQL_CREATE_TRANSLATION_TABLE = """
    |CREATE TABLE
    |${TranslationTable.TABLE_NAME}
    |(
    |${TranslationTable.COLUMN_NAME_ID} TEXT,
    |${TranslationTable.COLUMN_NAME_SOURCE_LANG} TEXT,
    |${TranslationTable.COLUMN_NAME_TARGET_LANG} TEXT,
    |${TranslationTable.COLUMN_NAME_ORIGINAL_TEXT} TEXT,
    |${TranslationTable.COLUMN_NAME_TRANSLATED_TEXT} TEXT,
    |${TranslationTable.COLUMN_NAME_IS_FAVORITE} INTEGER
    |)
""".trimMargin()

