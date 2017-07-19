package live.senya.supertranslate.history

import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import live.senya.supertranslate.data.Translation
import live.senya.supertranslate.data.source.Repository
import javax.inject.Inject

class HistoryPresenter @Inject constructor(
    private val repository: Repository,
    private val view: HistoryContract.View
) : HistoryContract.Presenter {

  companion object {
    private val TAG = HistoryPresenter::class.java.simpleName!!
  }

  init {
    Log.i(HistoryPresenter.TAG, "constructor()")
    view.setPresenter(this)
  }

  private val subscriptions = CompositeDisposable()


  override fun unsubscribe() {
    subscriptions.clear()
  }

  override fun loadHistory() {
    subscriptions.add(repository.getHistory().subscribe { t1, _ -> view.showHistory(t1.toMutableList()) })
  }

  override fun onTranslationSelected(translation: Translation) {
    view.switchToTranslationView(translation)
  }

  override fun getHistoryUpdates() {
    subscriptions.add(repository.getHistoryUpdates().subscribe(view::addTranslationToTheTop))
  }

}