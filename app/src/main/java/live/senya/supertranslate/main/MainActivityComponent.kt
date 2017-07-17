package live.senya.supertranslate.main

import dagger.Component
import live.senya.supertranslate.FragmentScoped
import live.senya.supertranslate.data.source.RepositoryComponent
import live.senya.supertranslate.translate.TranslatePresenterModule

@FragmentScoped
@Component(
        dependencies = arrayOf(RepositoryComponent::class),
        modules = arrayOf(TranslatePresenterModule::class)
)
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

}