package live.senya.supertranslate.translate


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
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

    fun getResultIntent(lang: Lang) = Intent().putExtra(RESULT_KEY, lang)!!
  }

  private lateinit var translationPresenter: TranslateContract.Presenter
  private lateinit var textViewSourceLang: TextView
  private lateinit var textViewTargetLang: TextView
  private lateinit var textViewTranslationResultLabel: TextView
  private lateinit var textViewTranslationResultText: TextView
  private lateinit var editTextTranslationSourceText: EditText
  private lateinit var cardViewTranslationResultCard: CardView
  private lateinit var imageViewCancelInput: ImageView
  private lateinit var imageViewSwapLangs: ImageView
  private lateinit var progressBarIsLoading: ProgressBar
  private lateinit var toolBar: Toolbar

  override fun onCreateView(inflater: LayoutInflater?,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    Log.i(TAG, "onCreateView()")
    val root = inflater!!.inflate(R.layout.fragment_translate, container, false)

    findViews(root)

    setupToolBar()
    setupEditText()
    setupOnClickListeners()

    return root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    translationPresenter.onLangSelectorResult(requestCode, resultCode, data)
  }

  override fun setPresenter(presenter: TranslateContract.Presenter) {
    translationPresenter = presenter
  }

  override fun showTranslation(translation: Translation) {
    cardViewTranslationResultCard.visibility = View.VISIBLE
    textViewTranslationResultText.text = translation.translatedText
    textViewTranslationResultLabel.text = translation.targetLang.name
    translationPresenter.moveTranslationOnTopOfHistory(translation)
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

  private fun findViews(parentView: View) {
    with(parentView) {
      textViewSourceLang = findViewById(R.id.tv_translate_sourcelang) as TextView
      textViewTargetLang = findViewById(R.id.tv_translate_targetlang) as TextView
      textViewTranslationResultLabel = findViewById(R.id.tv_translate_resulttexttitle) as TextView
      textViewTranslationResultText = findViewById(R.id.tv_translate_resulttext) as TextView
      editTextTranslationSourceText = findViewById(R.id.te_translate_sourcetext) as EditText
      cardViewTranslationResultCard = findViewById(R.id.cv_translate_resultcard) as CardView
      imageViewCancelInput = findViewById(R.id.iv_translate_cancelinput) as ImageView
      imageViewSwapLangs = findViewById(R.id.iv_translate_swaplangs) as ImageView
      progressBarIsLoading = findViewById(R.id.pb_translate_isloading) as ProgressBar
      toolBar = findViewById(R.id.tb_translate) as Toolbar
    }
  }

  private fun setupToolBar() {
    with(activity as AppCompatActivity) {
      setSupportActionBar(toolBar)
      supportActionBar?.title = resources.getString(R.string.app_name)
    }
  }

  private fun setupEditText() {
    with(editTextTranslationSourceText) {
      setHorizontallyScrolling(false)
      maxLines = Int.MAX_VALUE

      setOnEditorActionListener { v, _, _ ->
        hideKeyboard(v)
        val sourceText = text.toString()
        translationPresenter.translate(sourceText)
        return@setOnEditorActionListener true
      }

      setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
          imageViewCancelInput.visibility = View.VISIBLE
          hint = ""
        }
        if (!hasFocus) {
          imageViewCancelInput.visibility = View.INVISIBLE
          setHint(R.string.hint_edit_text)
          hideKeyboard(v)
        }
      }
    }
  }

  private fun setupOnClickListeners() {
    textViewSourceLang.setOnClickListener { translationPresenter.openLangSelector(LangSelectorType.SOURCE) }
    textViewTargetLang.setOnClickListener { translationPresenter.openLangSelector(LangSelectorType.TARGET) }
    imageViewSwapLangs.setOnClickListener { translationPresenter.swapLangs() }

    imageViewCancelInput.setOnClickListener {
      translationPresenter.unsubscribe()
      cardViewTranslationResultCard.visibility = View.INVISIBLE
      progressBarIsLoading.visibility = View.INVISIBLE

      with(editTextTranslationSourceText) {
        if (text.isEmpty()) {
          clearFocus()
        } else {
          text.clear()
        }
      }
    }
  }

  private fun hideKeyboard(view: View) {
    val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
  }
}
