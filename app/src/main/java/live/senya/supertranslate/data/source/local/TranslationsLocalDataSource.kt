package live.senya.supertranslate.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import live.senya.supertranslate.data.Lang
import javax.inject.Inject

class TranslationsLocalDataSource @Inject constructor(context: Context) {

    private val dbHelper = TranslationsDbHelper(context)

    fun saveLang(lang: Lang) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(LangTable.COLUMN_NAME_CODE, lang.code)
            put(LangTable.COLUMN_NAME_NAME, lang.name)
            put(LangTable.COLUMN_NAME_LOCALE, lang.locale)
        }

        db.insert(LangTable.TABLE_NAME, null, values)

        db.close()
    }

    fun getLangs(): List<Lang> {
        val langs = ArrayList<Lang>()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
                LangTable.COLUMN_NAME_CODE,
                LangTable.COLUMN_NAME_NAME,
                LangTable.COLUMN_NAME_LOCALE
        )

        val c: Cursor? =
                db.query(LangTable.TABLE_NAME, projection, null, null, null, null, null, null)

        c?.let {
            if (c.count > 0) {
                while (c.moveToNext()) {
                    val code = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_CODE))
                    val name = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_NAME))
                    val locale = c.getString(c.getColumnIndexOrThrow(LangTable.COLUMN_NAME_LOCALE))
                    langs.add(Lang(code, name, locale))
                }

            }
            c.close()
        }

        db.close()

        return langs
    }

}