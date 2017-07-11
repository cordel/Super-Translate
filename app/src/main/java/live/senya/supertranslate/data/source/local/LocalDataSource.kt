package live.senya.supertranslate.data.source.local

import io.reactivex.Observable
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.TextToTranslate
import live.senya.supertranslate.data.Translation

interface LocalDataSource {

    fun saveLang(lang: Lang)

    fun getLangs(): Observable<List<Lang>>

    fun saveTranslation(translation: Translation)

    fun getTranslation(textToTranslate: TextToTranslate): Observable<Translation>

    fun putTranslationOnTopOfHistory(translation: Translation)

    fun getHistory(): Observable<MutableList<Translation>>

    fun getHistoryUpdates(): Observable<Translation>

}