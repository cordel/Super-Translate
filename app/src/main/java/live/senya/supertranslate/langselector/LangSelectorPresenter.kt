package live.senya.supertranslate.langselector

import android.util.Log
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.source.Repository
import javax.inject.Inject

class LangSelectorPresenter @Inject constructor(
    private val repository: Repository,
    private val view: LangSelectorContract.View
) : LangSelectorContract.Presenter {

  companion object {
    private val TAG = LangSelectorPresenter::class.java.simpleName!!
  }

  init {
    Log.i(TAG, "constructor()")
    view.setPresenter(this)
  }

  override fun unsubscribe() {

  }

  override fun onLangSelected(lang: Lang) = view.returnSelectedLang(lang)

  override fun loadLangs() {
    repository.getLangs().subscribe { t: List<Lang> -> view.showLangs(t) }
  }

}