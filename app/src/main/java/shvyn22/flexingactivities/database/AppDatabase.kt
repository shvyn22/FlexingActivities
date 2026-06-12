package shvyn22.flexingactivities.database

import androidx.room.Database
import androidx.room.RoomDatabase
import shvyn22.flexingactivities.data.favorites.dao.FavoritesLocalDataSource
import shvyn22.flexingactivities.data.favorites.model.FavoriteLocationEntity

@Database(
    entities = [FavoriteLocationEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesLocalDataSource
}
