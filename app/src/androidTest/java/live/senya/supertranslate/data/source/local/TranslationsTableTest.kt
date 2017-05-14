package live.senya.supertranslate.data.source.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rx.observers.TestSubscriber

@RunWith(AndroidJUnit4::class) class TranslationsTableTest {
    val localDataSource = TranslationsRxLocalDataSource(
            InstrumentationRegistry.getTargetContext(),
            ImmediateSchedulerProvider()
    )

    @Before fun setup() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TranslationsPersistenceContract.DB_NAME)
    }

    @Test fun saveTranslations_retrievesTranslations(){
        val lang1 = Lang("asd","as","locale")
        val lang2 = Lang("asasd","dsas","locale")
        val textToTranslate = TextToTranslate(
                sourceLang = lang1,
                targetLang = lang2,
                originalText = "text1"
        )
        val translation = Translation(
                textToTranslate,
                translatedText = "text2"
        )
        val testSubscriber = TestSubscriber<Translation?>()

        localDataSource.saveLang(lang1)
        localDataSource.saveLang(lang2)
        Assert.assertTrue(localDataSource.saveTranslation(translation) > 0)
        localDataSource.getTranslation(textToTranslate).subscribe(testSubscriber)

        testSubscriber.assertValue(translation)
        testSubscriber.assertCompleted()
        testSubscriber.assertNoErrors()

    }
}