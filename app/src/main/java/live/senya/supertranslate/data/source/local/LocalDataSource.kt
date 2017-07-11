package live.senya.supertranslate.data.source.local

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation

interface LocalDataSource {

    fun saveLang(lang: Lang)

    fun getLangs(): Single<List<Lang>>

    fun saveTranslation(translation: Translation)

    fun getTranslation(textToTranslate: TextToTranslate): Maybe<Translation>

    fun putTranslationOnTopOfHistory(translation: Translation)

    fun getHistory(): Single<List<Translation>>

    fun getHistoryUpdates(): Observable<Translation>

}