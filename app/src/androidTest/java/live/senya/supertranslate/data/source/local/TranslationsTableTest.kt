package live.senya.supertranslate.data.source.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
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

    //todo have some fun with locale, make it work. Override equals
    @Test fun saveTranslations_retrievesTranslations(){
        val testSubscriber = TestSubscriber<List<Translation>>()
        val lang = Lang("qw","qw","en")
        localDataSource.saveLang(lang)

        val translation1 = Translation(lang, lang, "123", "123")
        val translation2 = Translation(lang, lang, "123", "123")
        val translation3 = Translation(lang, lang, "123", "123")
        val translation4 = Translation(lang, lang, "123", "123")
        val translation5 = Translation(lang, lang, "123", "123")
        val translation6 = Translation(lang, lang, "123", "123")

        localDataSource.saveTranslation(translation1)
        localDataSource.saveTranslation(translation2)
        localDataSource.saveTranslation(translation3)
        localDataSource.saveTranslation(translation4)
        localDataSource.saveTranslation(translation5)
        localDataSource.saveTranslation(translation6)

        val list: ArrayList<Translation>?
        list = arrayListOf(translation3, translation2, translation5, translation6)
        list.forEach { localDataSource.putTranslationOnTopOfHistory(it) }
        localDataSource.putTranslationOnTopOfHistory(translation5)
        list[2] = list[3]
        list[3] = translation5

        localDataSource.getHistory()?.subscribe(testSubscriber)
        testSubscriber.assertCompleted()
        testSubscriber.assertValue(list)
    }

}