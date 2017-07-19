package live.senya.supertranslate.history

import dagger.Module
import dagger.Provides

@Module
class HistoryPresenterModule(private val view: HistoryContract.View) {

  @Provides
  fun provideView(): HistoryContract.View = view

}