package live.senya.supertranslate.langselector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import live.senya.supertranslate.App
import live.senya.supertranslate.R
import javax.inject.Inject

class LangSelectorActivity : AppCompatActivity() {

  companion object {
    const val REQUEST_CODE_SOURCE_LANG = 0
    const val REQUEST_CODE_TARGET_LANG = 1
    const val LANG_SELECTOR_TYPE_KEY = "key"

    fun getStartIntent(context: Context, langSelectorType: LangSelectorType): Intent? {
      return Intent(context, LangSelectorActivity::class.java)
          .putExtra(LANG_SELECTOR_TYPE_KEY, langSelectorType)
    }
  }

  @Inject
  lateinit var langSelectorPresenter: LangSelectorPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lang_selector)
    setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

    with(supportActionBar!!) {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setTitle(
          when (intent.getSerializableExtra(LANG_SELECTOR_TYPE_KEY)) {
            LangSelectorType.SOURCE -> R.string.title_langselector_translate_from
            LangSelectorType.TARGET -> R.string.title_langselector_translate_to
            else -> throw IllegalArgumentException("Empty intent received!")
          }
      )
    }

    var langSelectorFragment = supportFragmentManager
        .findFragmentById(R.id.content_frame) as LangSelectorFragment?

    if (langSelectorFragment == null) {
      langSelectorFragment = LangSelectorFragment.newInstance()
      supportFragmentManager.beginTransaction()
          .add(R.id.content_frame, langSelectorFragment)
          .commit()
    }

    DaggerLangSelectorComponent.builder()
        .repositoryComponent((application as App).repositoryComponent)
        .langSelectorPresenterModule(LangSelectorPresenterModule(langSelectorFragment))
        .build()
        .inject(this)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

}
