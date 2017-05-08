package live.senya.supertranslate.data

data class TextToTranslate(val sourceLang: Lang,
                      val targetLang: Lang,
                      val originalText: String)

