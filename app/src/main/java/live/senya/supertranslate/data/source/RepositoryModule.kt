package live.senya.supertranslate.data.source

import android.content.Context
import dagger.Module
import dagger.Provides
import live.senya.supertranslate.data.source.local.LocalDataSource
import live.senya.supertranslate.data.source.local.sqlite.SqliteLocalDataSource
import live.senya.supertranslate.data.source.remote.RemoteDataSource
import live.senya.supertranslate.data.source.remote.yandex.YandexTranslateDataSource
import live.senya.supertranslate.schedulers.BaseSchedulerProvider
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideLocalDataSource(
            context: Context,
            schedulerProvider: BaseSchedulerProvider
    ): LocalDataSource = SqliteLocalDataSource(context, schedulerProvider)

    @Singleton
    @Provides
    fun provideRemoteDataSource(
            context: Context
    ): RemoteDataSource = YandexTranslateDataSource(context)

}