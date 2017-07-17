package live.senya.supertranslate.data.source.remote.yandex

import android.content.Context
import io.reactivex.Maybe
import live.senya.supertranslate.R
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.remote.RemoteDataSource
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class YandexTranslateDataSource(context: Context) : RemoteDataSource {

    companion object {
        private const val SERVICE_URL = "https://translate.yandex.net/api/v1.5/"
    }

    private val apiKey = context.getString(R.string.yandex_translate_api_key)

    private val yandexTranslateApi: YandexTranslateApi =
            Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(SERVICE_URL)
                    .build()
                    .create(YandexTranslateApi::class.java)

    override fun getTranslation(textToTranslate: TextToTranslate): Maybe<Translation> =
            yandexTranslateApi.translate(
                    key = apiKey,
                    lang = getTranslationDirection(textToTranslate),
                    text = textToTranslate.originalText
            )
                    .map { Translation(textToTranslate, it.text[0]) }

    private fun getTranslationDirection(textToTranslate: TextToTranslate): String =
            "${textToTranslate.sourceLang.code}-${textToTranslate.targetLang.code}"

}