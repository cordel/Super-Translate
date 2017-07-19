package live.senya.supertranslate

import live.senya.supertranslate.data.Translation

interface BaseView<T> {

  fun setPresenter(presenter: T)

}