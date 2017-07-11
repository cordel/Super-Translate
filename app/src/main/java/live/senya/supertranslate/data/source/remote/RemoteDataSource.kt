package live.senya.supertranslate.data.source.remote

import io.reactivex.Observable
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation

interface RemoteDataSource {

    fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation>

}