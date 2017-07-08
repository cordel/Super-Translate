package live.senya.supertranslate.data.source.remote.yandex

import android.content.Context
import live.senya.supertranslate.R
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.schedulers.BaseSchedulerProvider
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Observable
import javax.inject.Inject

class YandexTranslateDataSource @Inject constructor(val schedulerProvider: BaseSchedulerProvider,
                                                    context: Context) {

    companion object {
        private const val SERVICE_URL = "https://translate.yandex.net/api/v1.5/"
    }

    private val apiKey = context.getString(R.string.yandex_translate_api_key)

    private val yandexTranslateService: YandexTranslateService =
            Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(SERVICE_URL)
                    .build()
                    .create(YandexTranslateService::class.java)

    fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation> =
            yandexTranslateService.translate(
                    key = apiKey,
                    lang = getTranslationDirection(textToTranslate),
                    text = textToTranslate.originalText
            )
                    .subscribeOn(schedulerProvider.io())
                    .map { Translation(textToTranslate, it.text[0]) }
                    .observeOn(schedulerProvider.ui())

    private fun getTranslationDirection(textToTranslate: TextToTranslate): String =
            "${textToTranslate.sourceLang.code}-${textToTranslate.targetLang.code}"

}