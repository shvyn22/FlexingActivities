package shvyn22.flexingactivities

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import shvyn22.flexingactivities.data.core.di.HttpClientModule
import shvyn22.flexingactivities.data.favorites.di.FavoritesDataModule
import shvyn22.flexingactivities.di.DatabaseModule
import shvyn22.flexingactivities.data.geocoding.di.GeocodingDataModule
import shvyn22.flexingactivities.data.weather.di.WeatherDataModule
import shvyn22.flexingactivities.domain.core.di.DispatcherModule
import shvyn22.flexingactivities.domain.favorites.di.FavoritesDomainModule
import shvyn22.flexingactivities.domain.geocoding.di.GeocodingDomainModule
import shvyn22.flexingactivities.domain.weather.di.WeatherDomainModule
import shvyn22.flexingactivities.feature.details.di.DetailsModule
import shvyn22.flexingactivities.feature.favorites.di.FavoritesModule
import shvyn22.flexingactivities.feature.search.di.SearchModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                DatabaseModule,
                DispatcherModule,
                HttpClientModule,
                GeocodingDataModule,
                WeatherDataModule,
                FavoritesDataModule,
                GeocodingDomainModule,
                WeatherDomainModule,
                FavoritesDomainModule,
                SearchModule,
                FavoritesModule,
                DetailsModule,
            )
        }
    }
}
