package live.senya.supertranslate.langselector

import dagger.Component
import live.senya.supertranslate.ActivityScope
import live.senya.supertranslate.data.source.RepositoryComponent

@ActivityScope
@Component(
    dependencies = arrayOf(RepositoryComponent::class),
    modules = arrayOf(LangSelectorPresenterModule::class)
)
interface LangSelectorComponent {

  fun inject(langSelectorActivity: LangSelectorActivity)

}