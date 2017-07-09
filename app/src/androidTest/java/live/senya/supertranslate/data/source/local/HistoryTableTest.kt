package live.senya.supertranslate.data.source.local

import io.reactivex.observers.TestObserver
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.Translation
import org.junit.Test

class HistoryTableTest : BaseLocalDataSourceTest() {

    val lang = Lang("code", "name", testLocale)
    val translationsList = arrayListOf(
            Translation(lang, lang, "original text1", "translated text1"),
            Translation(lang, lang, "original text2", "translated text2"),
            Translation(lang, lang, "original text3", "translated text3")
    )

    override fun setUp() {
        super.setUp()
        localDataSource.saveLang(lang)
        translationsList.forEach { localDataSource.saveTranslation(it) }
    }

    @Test fun addTranslationToHistory_retrievesHistory(){
        val testObserver = TestObserver<List<Translation>>()

        translationsList.forEach { localDataSource.putTranslationOnTopOfHistory(it) }

        localDataSource.getHistory().subscribe(testObserver)

        testObserver.assertValue(translationsList)
        testObserver.assertComplete()
    }

    @Test fun moveTranslationToTop_retrievesHistory() {
        val testObserver = TestObserver<List<Translation>>()

        translationsList.forEach { localDataSource.putTranslationOnTopOfHistory(it) }
        localDataSource.putTranslationOnTopOfHistory(translationsList[0])

        localDataSource.getHistory().subscribe(testObserver)

        testObserver.assertValue(arrayListOf(
                translationsList[1],
                translationsList[2],
                translationsList[0]
        ))
        testObserver.assertComplete()
    }

    @Test fun moveTranslationToTop_receivesUpdate() {
        val historyObserver = TestObserver<Translation>()

        localDataSource.putTranslationOnTopOfHistory(translationsList[0])

        localDataSource.getHistoryUpdates().subscribe(historyObserver)

        localDataSource.putTranslationOnTopOfHistory(translationsList[1])

        historyObserver.assertValue(translationsList[1])
        historyObserver.assertValueCount(1)
        historyObserver.assertNotComplete()
    }

}