package live.senya.supertranslate.data

import java.util.*

/**
 * Immutable class that represents Translation.
 */
data class Translation(val sourceLang: Lang,
                       val targetLang: Lang,
                       val originalText: String,
                       val translatedText: String,
                       val isFavorite: Boolean = false,
                       val id: String = UUID.randomUUID().toString()) {

    /**
     * This constructor is used to create Translation from TextToTranslate
     * and translated String
     */
    constructor(textToTranslate: TextToTranslate, translatedText: String) : this(
            textToTranslate.sourceLang,
            textToTranslate.targetLang,
            textToTranslate.originalText,
            translatedText
    )

    /**
     * This constructor is used to mark/unmark translation as favorite
     */
    constructor(translation: Translation, isFavorite: Boolean) : this(
            translation.sourceLang,
            translation.targetLang,
            translation.originalText,
            translation.translatedText,
            isFavorite,
            translation.id
    )

}

