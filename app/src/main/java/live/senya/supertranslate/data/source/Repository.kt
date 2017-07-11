package live.senya.supertranslate.data.source

import io.reactivex.Observable
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.local.BaseLocalDataSource
import live.senya.supertranslate.data.source.remote.RemoteDataSource

class Repository(val localDataSource: BaseLocalDataSource,
                 val remoteDataSource: RemoteDataSource) {

    fun getLangs(): Observable<List<Lang>> = localDataSource.getLangs()

//    fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation> {
//        return localDataSource.getTranslation(textToTranslate)
//                .switchIfEmpty(remoteDataSource.getTranslation(textToTranslate))
//    }

    fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation> {
        return localDataSource.getTranslation(textToTranslate)
                .switchIfEmpty(remoteDataSource.getTranslation(textToTranslate))
    }


    fun putTranslationOnTopOfHistory(translation: Translation) = localDataSource.putTranslationOnTopOfHistory(translation)

    fun getHistory(): Observable<MutableList<Translation>> = localDataSource.getHistory()

    fun getHistoryUpdates(): Observable<Translation> = localDataSource.getHistoryUpdates()

    private fun saveTranslation(translation: Translation) {

    }

}