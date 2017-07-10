package live.senya.supertranslate.data.source

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.source.local.LocalDataSource
import live.senya.supertranslate.data.source.remote.yandex.YandexTranslateDataSource
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class RepositoryTest {

    val schedulerProvider = ImmediateSchedulerProvider()
    val context = InstrumentationRegistry.getTargetContext()!!
    val testLocale = "en"

    val repository = Repository(
            LocalDataSource(context, schedulerProvider, testLocale),
            YandexTranslateDataSource(context, schedulerProvider)
    )


    val textToTranslate = TextToTranslate(
            sourceLang = Lang("en", "ENGLISH", "en"),
            targetLang = Lang("ru", "RUSSIAN", "en"),
            originalText = "Hello, how are you? Whanna hang out?"
    )
    val expectedTranslation = "Привет, как ты? Whanna потусоваться?"

    @Test
    fun getTranslationTest() {
        var translatedText: String = ""

        repository.getTranslation(textToTranslate)
                .subscribe { t1 -> translatedText = t1.translatedText }

        Assert.assertEquals(expectedTranslation, translatedText)
    }
}