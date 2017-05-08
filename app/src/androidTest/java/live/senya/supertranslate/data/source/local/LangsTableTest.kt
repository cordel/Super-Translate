package live.senya.supertranslate.data.source.local

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import live.senya.supertranslate.data.Lang
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class LangsTableTest {

    val localDataSource = TranslationsLocalDataSource(
            InstrumentationRegistry.getTargetContext()
    )

    @Before fun setup() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TranslationsPersistenceContract.DB_NAME)
    }

    @Test fun saveLang_retrievesLangs() {
        val lang0 = Lang("asd", "dsa", "zxc")
        val lang1 = Lang("asd", "dsa", "zxc")

        localDataSource.saveLang(lang0)
        localDataSource.saveLang(lang1)

        val langs = localDataSource.getLangs()
        Assert.assertTrue(langs.size == 2)
        Assert.assertTrue(langs.contains(lang0))
        Assert.assertTrue(langs.contains(lang1))
    }

}