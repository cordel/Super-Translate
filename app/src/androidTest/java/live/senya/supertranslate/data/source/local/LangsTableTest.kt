package live.senya.supertranslate.data.source.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rx.observers.TestSubscriber

@RunWith(AndroidJUnit4::class) class LangsTableTest {

    val localDataSource = TranslationsRxLocalDataSource(
            InstrumentationRegistry.getTargetContext(),
            ImmediateSchedulerProvider()
    )

    @Before fun setup() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TranslationsPersistenceContract.DB_NAME)
    }

    @Test fun saveLang_retrievesLangs() {
        val langList = arrayListOf(Lang("asd", "dsa", "zxc"), Lang("asd", "dsa", "zxc"))
        langList.forEach { localDataSource.saveLang(it) }
        val testSubscriber = TestSubscriber<List<Lang>>()

        localDataSource.getLangs().take(1).subscribe(testSubscriber)
        testSubscriber.assertValues(langList)
        testSubscriber.assertCompleted()

        val newLang = Lang("qq","ww", "ww")
        val newList = ArrayList<Lang>(langList)
        newList.add(newLang)

        localDataSource.saveLang(newLang)
        testSubscriber.assertValues(langList)

    }

}