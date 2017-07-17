package live.senya.supertranslate.translate

import live.senya.supertranslate.BasePresenter
import live.senya.supertranslate.BaseView
import live.senya.supertranslate.data.Translation

interface TranslateContract {

  interface View : BaseView<Presenter> {

    fun showTranslation(translation: Translation)

  }

  interface Presenter : BasePresenter {

    fun translate(text: String)

  }
}