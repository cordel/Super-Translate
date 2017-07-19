package live.senya.supertranslate.main

import dagger.Component
import live.senya.supertranslate.ActivityScope
import live.senya.supertranslate.data.source.RepositoryComponent
import live.senya.supertranslate.history.HistoryPresenterModule
import live.senya.supertranslate.translate.TranslatePresenterModule

@ActivityScope
@Component(
    dependencies = arrayOf(RepositoryComponent::class),
    modules = arrayOf(TranslatePresenterModule::class, HistoryPresenterModule::class)
)
interface MainActivityComponent {

  fun inject(mainActivity: MainActivity)

}