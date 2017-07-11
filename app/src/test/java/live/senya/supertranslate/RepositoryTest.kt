package live.senya.supertranslate

import io.reactivex.Maybe
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
    val localTranslationResponse = Translation(textToTranslate, "local_ds_translation")
    val remoteTranslationResponse = Translation(textToTranslate, "remote_ds_translation")

    @Test
    fun repoQueriesRemoteDsIfLocalDsReturnsNoResult() {
        val remoteDataSource = mock(RemoteDataSource::class.java)
        `when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(Maybe.just(remoteTranslationResponse))

        val localDataSource = mock(LocalDataSource::class.java)
        `when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Maybe.empty())

        val repository = Repository(localDataSource, remoteDataSource)

        val testObserver = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(testObserver)

        testObserver.assertValue(remoteTranslationResponse)
        testObserver.assertValueCount(1)
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        verify(localDataSource, times(1)).saveTranslation(remoteTranslationResponse)

    }

    @Test
    fun repoQueriesOnlyLocalDataSourceTest() {
        val remoteDataSource = mock(RemoteDataSource::class.java)
        `when`(remoteDataSource.getTranslation(textToTranslate))
                .thenReturn(
                        Maybe.create<Translation> {
                            it.onError(
                                throw AssertionError("remoteSourceWasCalled!")
                            )
                        }
                )

        val localDataSource = mock(LocalDataSource::class.java)
        `when`(localDataSource.getTranslation(textToTranslate))
                .thenReturn(Maybe.just(localTranslationResponse))

        val repository = Repository(localDataSource, remoteDataSource)

        val testObserver = TestObserver<Translation>()

        repository.getTranslation(textToTranslate).subscribe(testObserver)

        testObserver.assertValue(localTranslationResponse)
        testObserver.assertValueCount(1)
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        verify(localDataSource, never()).saveTranslation(remoteTranslationResponse)
    }

}