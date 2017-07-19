package live.senya.supertranslate.data.source

import io.reactivex.Observable
import io.reactivex.Single
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.local.LocalDataSource
import live.senya.supertranslate.data.source.remote.RemoteDataSource
import live.senya.supertranslate.schedulers.BaseSchedulerProvider
import javax.inject.Inject

class Repository @Inject constructor(val localDataSource: LocalDataSource,
                                     val remoteDataSource: RemoteDataSource,
                                     val schedulerProvider: BaseSchedulerProvider) {

  fun getLangs(): Single<List<Lang>> = localDataSource
      .getLangs()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.ui())

  /**
   * This method tries to receive a valid translation from localDataSource.
   * In case it receives nothing it queries remoteDataSource and then saves the translation
   * with localDataSource.
   */
  fun getTranslation(textToTranslate: TextToTranslate): Single<Translation> {
    return localDataSource.getTranslation(textToTranslate)
        .switchIfEmpty(
            remoteDataSource.getTranslation(textToTranslate)
                .doAfterSuccess { localDataSource.saveTranslation(it) }
        )
        .toSingle()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.ui())
  }

  fun putTranslationOnTopOfHistory(translation: Translation) = localDataSource.putTranslationOnTopOfHistory(translation)

  fun getHistory(): Single<List<Translation>> = localDataSource
      .getHistory()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.ui())

  fun getHistoryUpdates(): Observable<Translation> = localDataSource.getHistoryUpdates().observeOn(schedulerProvider.ui())

}