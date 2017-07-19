package live.senya.supertranslate.history

import live.senya.supertranslate.BasePresenter
import live.senya.supertranslate.BaseView
import live.senya.supertranslate.data.Translation

interface HistoryContract {

  interface View : BaseView<Presenter> {
    fun showHistory(history: MutableList<Translation>)
    fun addTranslationToTheTop(translation: Translation)
    fun switchToTranslationView(translation: Translation)
  }

  interface Presenter : BasePresenter {
    fun loadHistory()
    fun onTranslationSelected(translation: Translation)
    fun getHistoryUpdates()
  }
}