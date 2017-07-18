package live.senya.supertranslate.langselector

import dagger.Module
import dagger.Provides

@Module
class LangSelectorPresenterModule(private val view: LangSelectorContract.View) {

  @Provides
  fun provideView(): LangSelectorContract.View = view

}