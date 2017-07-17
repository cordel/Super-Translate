package live.senya.supertranslate.data.source.remote

import io.reactivex.Maybe
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation

interface RemoteDataSource {

  fun getTranslation(textToTranslate: TextToTranslate): Maybe<Translation>

}