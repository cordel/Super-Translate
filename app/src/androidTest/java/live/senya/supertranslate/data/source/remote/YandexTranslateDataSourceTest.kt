package live.senya.supertranslate.data.source.remote

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.source.remote.yandex.YandexTranslateDataSource
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class YandexTranslateDataSourceTest {

    val yandexRemoteDataSource = YandexTranslateDataSource(
            InstrumentationRegistry.getTargetContext()
    )
    val textToTranslate = TextToTranslate(
            sourceLang = Lang("en", "ENGLISH", "en"),
            targetLang = Lang("ru", "RUSSIAN", "en"),
            originalText = "Hello, how are you? Whanna hang out?"
    )
    val expectedTranslation = "Привет, как ты? Whanna потусоваться?"

    @Test
    fun getTranslation_multipleWordsGiven_correctTranslationTextReturned() {
        var translatedText: String = ""

        yandexRemoteDataSource.getTranslation(textToTranslate)
                .subscribe { t1 -> translatedText = t1.translatedText }

        Assert.assertEquals(expectedTranslation, translatedText)
    }
}