package live.senya.supertranslate.data.source.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.schedulers.ImmediateSchedulerProvider
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) abstract class BaseLocalDataSourceTest {

    val testLocale = "en"

    val localDataSource = LocalDataSource(
            InstrumentationRegistry.getTargetContext(),
            ImmediateSchedulerProvider(),
            testLocale
    )

    @Before open fun setUp() {
        recreateDb()
    }

    private fun recreateDb() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TranslationsPersistenceContract.DB_NAME)
    }

}