package live.senya.supertranslate.translate

import android.app.Activity
import android.content.Intent
import android.util.Log
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.source.Repository
import live.senya.supertranslate.langselector.LangSelectorActivity
import live.senya.supertranslate.langselector.LangSelectorType
import javax.inject.Inject

class TranslatePresenter @Inject constructor(
    private val repository: Repository,
    private val view: TranslateContract.View
) : TranslateContract.Presenter {

  companion object {
    private val TAG = TranslatePresenter::class.java.simpleName!!
  }

  init {
    Log.i(TAG, "constructor()")
    view.setPresenter(this)
  }

  lateinit var sourceLang: Lang
  lateinit var targetLang: Lang

  override fun translate(text: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun openLangSelector(langSelectorType: LangSelectorType) = view.showLangSelectorUi(langSelectorType)

  override fun onLangSelectorResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      val lang = data.getParcelableExtra<Lang>(TranslateFragment.RESULT_KEY)

      when (requestCode) {
        LangSelectorActivity.REQUEST_CODE_SOURCE_LANG -> {
          sourceLang = lang
          view.setSourceLangLabel(lang.name)
        }
        LangSelectorActivity.REQUEST_CODE_TARGET_LANG -> {
          targetLang = lang
          view.setTargetLangLabel(lang.name)
        }
      }
    }
  }

}
