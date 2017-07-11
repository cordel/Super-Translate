package live.senya.supertranslate.data

/**
 * Immutable class that represents not completed Translation.
 */
data class TextToTranslate(val sourceLang: Lang,
                           val targetLang: Lang,
                           val originalText: String)