package live.senya.supertranslate.data.source.local

import io.reactivex.observers.TestObserver
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import org.junit.Test

class TranslationsTableTest : BaseLocalDataSourceTest() {

    val lang = Lang("code", "name", testLocale)
    val textToTranslate = TextToTranslate(lang, lang, "original text")
    val translation = Translation(textToTranslate, "translated text")

    override fun setUp() {
        super.setUp()
        localDataSource.saveLang(lang)
    }

    @Test fun saveTranslations_retrievesTranslation() {
        val testObserver = TestObserver<Translation>()

        localDataSource.saveTranslation(translation)

        localDataSource.getTranslation(textToTranslate).subscribe(testObserver)

        testObserver.assertValue(translation)
        testObserver.assertComplete()
    }

    @Test fun emptyRequestCompletesAnyway() {
        val testObserver = TestObserver<Translation>()

        localDataSource.getTranslation(textToTranslate).subscribe(testObserver)

        testObserver.assertNoValues()
        testObserver.assertComplete()
    }

}