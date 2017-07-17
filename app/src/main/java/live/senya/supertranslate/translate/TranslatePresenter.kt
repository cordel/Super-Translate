package live.senya.supertranslate.translate

import live.senya.supertranslate.data.source.Repository
import javax.inject.Inject

class TranslatePresenter @Inject constructor(
    private val repository: Repository,
    view: TranslateContract.View
) : TranslateContract.Presenter {

  init {
    view.setPresenter(this)
  }

  override fun translate(text: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}