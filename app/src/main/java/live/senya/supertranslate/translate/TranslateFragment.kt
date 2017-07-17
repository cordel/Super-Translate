package live.senya.supertranslate.translate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import live.senya.supertranslate.R
import live.senya.supertranslate.data.Translation

class TranslateFragment : Fragment(), TranslateContract.View {

  companion object {
    val TAG = TranslatePresenter::class.java.simpleName!!
    fun newInstance(): TranslateFragment {
      Log.w(TAG, "newInstance()")
      return TranslateFragment()
    }
  }

  lateinit var presenter1: TranslateContract.Presenter

  override fun onCreateView(inflater: LayoutInflater?,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    Log.w(TAG, "onCreateView()")
    return inflater!!.inflate(R.layout.fragment_translate, container, false)
  }

  override fun showTranslation(translation: Translation) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun setPresenter(presenter: TranslateContract.Presenter) {
    presenter1 = presenter
  }

}
