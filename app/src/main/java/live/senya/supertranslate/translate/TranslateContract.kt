package live.senya.supertranslate.translate

import android.content.Intent
import live.senya.supertranslate.BasePresenter
import live.senya.supertranslate.BaseView
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.langselector.LangSelectorType

interface TranslateContract {

  interface View : BaseView<Presenter> {
    fun showTranslation(translation: Translation)
    fun showLangSelectorUi(langSelectorType: LangSelectorType)
    fun setSourceLangLabel(label: String)
    fun setTargetLangLabel(label: String)
  }

  interface Presenter : BasePresenter {
    fun translate(text: String)
    fun openLangSelector(langSelectorType: LangSelectorType)
    fun onLangSelectorResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun swapLangs()
    fun moveTranslationOnTopOfHistory(translation: Translation)
  }

}