package live.senya.supertranslate

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.Repository
import live.senya.supertranslate.data.source.local.BaseLocalDataSource
import live.senya.supertranslate.data.source.remote.RemoteDataSource
import org.junit.Test
import org.mockito.Mockito

class RepositoryTest {

    val lang = Lang("code", "name", "en")
    val textToTranslate = TextToTranslate(lang, lang, "original text")
    val translation = Translation(textToTranslate, "translated text")
    val translation1 = Translation(textToTranslate, "translated text1")

    @Test
    fun repoQueriesRemoteDataSourceTest() {
        val remoteDataSource = Mockito.mock(RemoteDataSource::class.java)
        Mockito.`when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.just(translation1))

        val localDataSource = Mockito.mock(BaseLocalDataSource::class.java)
        Mockito.`when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.empty())

        val repository = Repository(localDataSource, remoteDataSource)

        val test = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(test)

        test.assertValue(translation1)
        test.assertValueCount(1)
        test.assertComplete()
    }

    @Test
    fun repoQueriesOnlyLocalDataSourceTest() {
        val remoteDataSource = Mockito.mock(RemoteDataSource::class.java)
        Mockito.`when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.just(translation1))

        val localDataSource = Mockito.mock(BaseLocalDataSource::class.java)
        Mockito.`when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.just(translation))

        val repository = Repository(localDataSource, remoteDataSource)

        val test = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(test)

        test.assertValue(translation)
        test.assertValueCount(1)
        test.assertComplete()

        Mockito.verifyZeroInteractions(remoteDataSource)
    }
}