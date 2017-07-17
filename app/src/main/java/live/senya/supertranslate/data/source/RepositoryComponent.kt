package live.senya.supertranslate.data.source

import dagger.Component
import live.senya.supertranslate.AppModule
import live.senya.supertranslate.schedulers.SchedulerProviderModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        RepositoryModule::class,
        SchedulerProviderModule::class,
        AppModule::class
))
interface RepositoryComponent {

    fun getRepository(): Repository

}