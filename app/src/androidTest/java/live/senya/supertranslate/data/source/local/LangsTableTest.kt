package live.senya.supertranslate.data.source.local

import io.reactivex.observers.TestObserver
import live.senya.supertranslate.data.Lang
import org.junit.Test

class LangsTableTest : BaseLocalDataSourceTest() {

    val langList = arrayListOf(
            Lang("code1", "name1", testLocale),
            Lang("code2", "name2", testLocale),
            Lang("code3", "name3", testLocale)
    )

    override fun setUp() {
        super.setUp()
    }

    @Test fun saveLang_retrievesLangs() {
        val testObserver = TestObserver<List<Lang>>()

        langList.forEach { localDataSource.saveLang(it) }

        localDataSource.getLangs().subscribe(testObserver)

        testObserver.assertValues(langList)
        testObserver.assertComplete()
    }

}