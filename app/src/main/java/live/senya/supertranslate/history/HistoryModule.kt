package live.senya.supertranslate.history

import live.senya.supertranslate.translate.TranslateContract

class HistoryModule(private val view: TranslateContract) {

  fun provideView(): TranslateContract = view

}