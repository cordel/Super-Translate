package live.senya.supertranslate.data.source.local

// Visible for testing
abstract class TranslationsPersistenceContract private constructor() {
    companion object DbInfo {
        const val DB_NAME = "Translations.db"
        const val DB_VERSION = 1
    }
}

object LangTable {
    const val TABLE_NAME = "lang"
    const val COLUMN_NAME_CODE = "lang_code"
    const val COLUMN_NAME_NAME = "lang_name"
    const val COLUMN_NAME_LOCALE = "lang_name_locale"
}

object TranslationTable {
    const val TABLE_NAME = "translation"
    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_SOURCE_LANG = "source_lang"
    const val COLUMN_NAME_TARGET_LANG = "target_lang"
    const val COLUMN_NAME_ORIGINAL_TEXT = "original_text"
    const val COLUMN_NAME_TRANSLATED_TEXT = "translated_text"
    const val COLUMN_NAME_IS_FAVORITE = "is_favorite"
    const val COLUMN_NAME_HISTORY_POSITION = "history_position"
    const val COLUMN_NAME_FAVORITE_POSITION = "favorite_position"
}

object UtilNames {
    const val TARGET = "target"
    const val SOURCE = "source"
}


