package live.senya.supertranslate.data.source.local.sqlite

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import live.senya.supertranslate.data.source.local.sqlite.TranslationsPersistenceContract.DbInfo.DB_NAME
import live.senya.supertranslate.data.source.local.sqlite.TranslationsPersistenceContract.DbInfo.DB_VERSION

class DbHelper(context: Context) : SQLiteAssetHelper(context, DB_NAME, null, DB_VERSION) {

//  override fun onCreate(db: SQLiteDatabase?) {
//    with(db!!) {
//      execSQL(SQL_CREATE_LANG_TABLE)
//      execSQL(SQL_CREATE_TRANSLATION_TABLE)
//    }
//  }
//
//  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//    //noop
//  }

}

private val SQL_CREATE_LANG_TABLE = """
    |CREATE TABLE
    |${LangTable.TABLE_NAME}
    |(
    |${LangTable.COLUMN_NAME_CODE} TEXT NOT NULL,
    |${LangTable.COLUMN_NAME_NAME} TEXT NOT NULL,
    |${LangTable.COLUMN_NAME_LOCALE} TEXT NOT NULL
    |)
""".trimMargin()

private val SQL_CREATE_TRANSLATION_TABLE = """
    |CREATE TABLE
    |${TranslationTable.TABLE_NAME}
    |(
    |${TranslationTable.COLUMN_NAME_ID} TEXT NOT NULL,
    |${TranslationTable.COLUMN_NAME_SOURCE_LANG} TEXT NOT NULL,
    |${TranslationTable.COLUMN_NAME_TARGET_LANG} TEXT NOT NULL,
    |${TranslationTable.COLUMN_NAME_ORIGINAL_TEXT} TEXT NOT NULL,
    |${TranslationTable.COLUMN_NAME_TRANSLATED_TEXT} TEXT NOT NULL,
    |${TranslationTable.COLUMN_NAME_IS_FAVORITE} INTEGER NOT NULL,
    |${TranslationTable.COLUMN_NAME_HISTORY_POSITION} INTEGER,
    |${TranslationTable.COLUMN_NAME_FAVORITE_POSITION} INTEGER
    |)
""".trimMargin()
