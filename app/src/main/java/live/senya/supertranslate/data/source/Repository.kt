package live.senya.supertranslate.data.source

import io.reactivex.Observable
import io.reactivex.Single
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.local.LocalDataSource
import live.senya.supertranslate.data.source.remote.RemoteDataSource

class Repository(private val localDataSource: LocalDataSource,
                 private val remoteDataSource: RemoteDataSource) {

    fun getLangs(): Single<List<Lang>> = localDataSource.getLangs()

    /**
     * This method tries to receive a valid translation from localDataSource.
     * In case it receives nothing it queries remoteDataSource and then saves the translation
     * with localDataSource
     */
    fun getTranslation(textToTranslate: TextToTranslate): Single<Translation> {
        return localDataSource.getTranslation(textToTranslate)
                .switchIfEmpty(
                        remoteDataSource.getTranslation(textToTranslate)
                                .doAfterSuccess { localDataSource.saveTranslation(it) }
                )
                .toSingle()
    }

    fun putTranslationOnTopOfHistory(translation: Translation) = localDataSource.putTranslationOnTopOfHistory(translation)

    fun getHistory(): Single<List<Translation>> = localDataSource.getHistory()

    fun getHistoryUpdates(): Observable<Translation> = localDataSource.getHistoryUpdates()

}