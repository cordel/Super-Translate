package live.senya.supertranslate.data.source.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.reactivex.observers.TestObserver
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class LangsTableTest {

    val localDataSource = LocalDataSource(
            InstrumentationRegistry.getTargetContext(),
            ImmediateSchedulerProvider()
    )

    @Before fun setup() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TranslationsPersistenceContract.DB_NAME)
    }

    @Test fun saveLang_retrievesLangs() {
        val testObserver = TestObserver<List<Lang>>()
        val langList = arrayListOf(Lang("asd", "dsa", "en"), Lang("asd", "dsa", "en"))
        langList.forEach { localDataSource.saveLang(it) }

        localDataSource.getLangs().subscribe(testObserver)
        testObserver.assertValues(langList)
        testObserver.assertComplete()
    }

}