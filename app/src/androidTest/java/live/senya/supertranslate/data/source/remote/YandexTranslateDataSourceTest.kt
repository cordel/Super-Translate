package live.senya.supertranslate.data.source.remote

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.source.remote.yandex.YandexTranslateDataSource
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class YandexTranslateDataSourceTest {

    val context: Context = InstrumentationRegistry.getTargetContext()

    @Test
    fun getTranslation_multipleWordsGiven_correctTranslationTextReturned() {

        val yandex = YandexTranslateDataSource(ImmediateSchedulerProvider(), context)
        val translationObs = yandex.getTranslation(
                TextToTranslate(
                        sourceLang = Lang("en", "ENGLISH", "en"),
                        targetLang = Lang("ru", "RUSSIAN", "en"),
                        originalText = "Hello, how are you? Whanna hang out?"
                )
        )
        val expectedTranslation = "Привет, как ты? Whanna потусоваться?"

        var translatedText: String = ""
        translationObs.subscribe { t1, t2 -> translatedText = t1.translatedText }

        Assert.assertEquals(expectedTranslation, translatedText)

    }
}