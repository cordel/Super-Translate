package live.senya.supertranslate.schedulers

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SchedulerProviderModule {

  @Singleton
  @Provides
  fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider()

}