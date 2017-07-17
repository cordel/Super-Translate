package live.senya.supertranslate.translate

import dagger.Module
import dagger.Provides

@Module
class TranslatePresenterModule(private val view: TranslateContract.View) {

  @Provides
  fun provideView(): TranslateContract.View = view

}