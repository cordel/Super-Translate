package live.senya.supertranslate.translate


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import live.senya.supertranslate.R
import live.senya.supertranslate.data.Lang
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.langselector.LangSelectorActivity
import live.senya.supertranslate.langselector.LangSelectorType

class TranslateFragment : Fragment(), TranslateContract.View {

  companion object {
    private val TAG = TranslatePresenter::class.java.simpleName!!
    const val RESULT_KEY = "result"

    fun newInstance(): TranslateFragment {
      Log.i(TAG, "newInstance()")
      return TranslateFragment()
    }

    fun getResultIntent(lang: Lang): Intent? = Intent().putExtra(RESULT_KEY, lang)
  }

  lateinit var translationPresenter: TranslateContract.Presenter
  lateinit var textViewSourceLang: TextView
  lateinit var textViewTargetLang: TextView

  override fun onCreateView(inflater: LayoutInflater?,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    Log.i(TAG, "onCreateView()")
    val root = inflater!!.inflate(R.layout.fragment_translate, container, false)

    with(root){
      textViewSourceLang = findViewById(R.id.tv_translate_sourcelang) as TextView
      textViewTargetLang = findViewById(R.id.tv_translate_targetlang) as TextView
    }

    textViewSourceLang.setOnClickListener { translationPresenter.openLangSelector(LangSelectorType.SOURCE) }
    textViewTargetLang.setOnClickListener { translationPresenter.openLangSelector(LangSelectorType.TARGET) }


    return root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    translationPresenter.onLangSelectorResult(requestCode, resultCode, data)
  }

  override fun setPresenter(presenter: TranslateContract.Presenter) {
    translationPresenter = presenter
  }

  override fun showTranslation(translation: Translation) {

  }

  override fun showLangSelectorUi(langSelectorType: LangSelectorType) {
    val intent = LangSelectorActivity.getStartIntent(context, langSelectorType)

    when (langSelectorType) {
      LangSelectorType.SOURCE -> startActivityForResult(intent, LangSelectorActivity.REQUEST_CODE_SOURCE_LANG)
      LangSelectorType.TARGET -> startActivityForResult(intent, LangSelectorActivity.REQUEST_CODE_TARGET_LANG)
    }
  }

  override fun setSourceLangLabel(label: String) {
    textViewSourceLang.text = label
  }

  override fun setTargetLangLabel(label: String) {
    textViewTargetLang.text = label
  }

}
