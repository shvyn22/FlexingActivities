package shvyn22.flexingactivities.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import shvyn22.flexingactivities.data.favorites.dao.FavoritesLocalDataSource
import shvyn22.flexingactivities.database.AppDatabase

val DatabaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "flexing_db",
            )
            .build()
    }
    single<FavoritesLocalDataSource> { get<AppDatabase>().favoritesDao() }
}
