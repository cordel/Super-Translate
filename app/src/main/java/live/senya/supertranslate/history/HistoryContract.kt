package live.senya.supertranslate.history

import live.senya.supertranslate.BasePresenter
import live.senya.supertranslate.BaseView

interface HistoryContract {

  interface View : BaseView<Presenter> {

  }

  interface Presenter : BasePresenter {

  }
}