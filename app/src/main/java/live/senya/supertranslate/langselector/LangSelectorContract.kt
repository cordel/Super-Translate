package live.senya.supertranslate.langselector

import live.senya.supertranslate.BasePresenter
import live.senya.supertranslate.BaseView
import live.senya.supertranslate.data.Lang

interface LangSelectorContract {

  interface View : BaseView<Presenter> {
    fun showLangs(langs: List<Lang>)
    fun returnSelectedLang(lang: Lang)
  }

  interface Presenter : BasePresenter {
    fun onLangSelected(lang: Lang)
    fun loadLangs()
  }

}