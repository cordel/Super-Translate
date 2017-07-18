package live.senya.supertranslate.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import live.senya.supertranslate.App
import live.senya.supertranslate.R
import live.senya.supertranslate.history.HistoryFragment
import live.senya.supertranslate.translate.TranslateFragment
import live.senya.supertranslate.translate.TranslatePresenter
import live.senya.supertranslate.translate.TranslatePresenterModule
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  companion object {
    private val TAG = MainActivity::class.java.simpleName!!
    const val TRANSLATE_FRAGMENT_TAG = "translate"
    const val HISTORY_FRAGMENT_TAG = "history"
  }

  @Inject
  lateinit var translatePresenter: TranslatePresenter
  lateinit var translateFragment: TranslateFragment
  lateinit var historyFragment: HistoryFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.i(TAG, "onCreate()")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val bottomNavigationView = findViewById(R.id.navigation) as BottomNavigationView
    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectListener())

    if (savedInstanceState == null) {
      translateFragment = TranslateFragment.newInstance()
      historyFragment = HistoryFragment.newInstance()

      supportFragmentManager
          .beginTransaction()
          .add(R.id.content, translateFragment, TRANSLATE_FRAGMENT_TAG)
          .add(R.id.content, historyFragment, HISTORY_FRAGMENT_TAG)
          .show(translateFragment)
          .hide(historyFragment)
          .commit()
    } else {
      translateFragment = supportFragmentManager
          .findFragmentByTag(TRANSLATE_FRAGMENT_TAG) as TranslateFragment
      historyFragment = supportFragmentManager
          .findFragmentByTag(HISTORY_FRAGMENT_TAG) as HistoryFragment
    }

    DaggerMainActivityComponent.builder()
        .repositoryComponent((application as App).repositoryComponent)
        .translatePresenterModule(TranslatePresenterModule(translateFragment))
        .build()
        .inject(this)

  }

  private fun navigationItemSelectListener(): (MenuItem) -> Boolean {
    return {
      when (it.itemId) {
        R.id.navigation_translate -> {
          supportFragmentManager.beginTransaction()
              .show(translateFragment)
              .hide(historyFragment)
              .commit()
          true
        }
        R.id.navigation_history -> {
          supportFragmentManager.beginTransaction()
              .show(historyFragment)
              .hide(translateFragment)
              .commit()
          true
        }
        else -> false
      }
    }
  }
}
