package live.senya.supertranslate

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.Repository
import live.senya.supertranslate.data.source.local.LocalDataSource
import live.senya.supertranslate.data.source.remote.RemoteDataSource
import org.junit.Test
import org.mockito.Mockito.*

class RepositoryTest {

    val lang = Lang("code", "name", "en")
    val textToTranslate = TextToTranslate(lang, lang, "original text")
    val localTranslationResponse = Translation(textToTranslate, "translated text")
    val remoteTranslationResponse = Translation(textToTranslate, "translated text1")

    @Test
    fun repoQueriesRemoteDataSourceTest() {
        val remoteDataSource = mock(RemoteDataSource::class.java)
        `when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.just(remoteTranslationResponse))

        val localDataSource = mock(LocalDataSource::class.java)
        `when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.empty())

        val repository = Repository(localDataSource, remoteDataSource)

        val test = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(test)

        test.assertValue(remoteTranslationResponse)
        test.assertValueCount(1)
        test.assertComplete()

        verify(localDataSource, times(1)).saveTranslation(remoteTranslationResponse)

    }

    @Test
    fun repoQueriesOnlyLocalDataSourceTest() {
        val remoteDataSource = mock(RemoteDataSource::class.java)
        `when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(
                        Observable.create<Translation> {
                            kotlin.run {
                                println(123)
                                it.onNext(kotlin.run {
                                    throw Exception("remoteSourceWasCalled!")
                                })
                                it.onComplete()
                            }
                        }
                )

        val localDataSource = mock(LocalDataSource::class.java)
        `when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.just(localTranslationResponse))

        val repository = Repository(localDataSource, remoteDataSource)

        val test = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(test)

        test.assertValue(localTranslationResponse)
        test.assertValueCount(1)
        test.assertComplete()

        verify(localDataSource, never()).saveTranslation(remoteTranslationResponse)
    }

    @Test
    fun repoSavesTranslationsFromRemoteSource() {
        val remoteDataSource = mock(RemoteDataSource::class.java)
        `when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.just(remoteTranslationResponse))

        val localDataSource = mock(LocalDataSource::class.java)
        `when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Observable.empty())

        val repository = Repository(localDataSource, remoteDataSource)

        val test = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(test)

        test.assertValue(remoteTranslationResponse)
        test.assertValueCount(1)
        test.assertComplete()

        verify(localDataSource, times(1)).saveTranslation(remoteTranslationResponse)
    }
}