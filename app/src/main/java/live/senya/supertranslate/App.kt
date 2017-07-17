package live.senya.supertranslate

import android.app.Application
import live.senya.supertranslate.data.source.DaggerRepositoryComponent
import live.senya.supertranslate.data.source.RepositoryComponent

class App : Application() {

  lateinit var repositoryComponent: RepositoryComponent

  override fun onCreate() {
    super.onCreate()

    repositoryComponent = DaggerRepositoryComponent.builder()
        .appModule(AppModule(applicationContext))
        .build()
  }

}