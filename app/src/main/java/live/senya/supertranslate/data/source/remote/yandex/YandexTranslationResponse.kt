package live.senya.supertranslate.data.source.remote.yandex

data class YandexTranslationResponse(val code: Int,
                                     val lang: String,
                                     val text: List<String>)


