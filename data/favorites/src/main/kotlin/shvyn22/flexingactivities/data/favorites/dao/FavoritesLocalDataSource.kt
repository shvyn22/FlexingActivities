package shvyn22.flexingactivities.data.favorites.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import shvyn22.flexingactivities.data.favorites.model.FavoriteLocationEntity
import shvyn22.flexingactivities.data.favorites.model.FavoriteLocationScheme

@Dao
interface FavoritesLocalDataSource {

    @Query("SELECT * FROM ${FavoriteLocationScheme.TABLE_NAME} ORDER BY ${FavoriteLocationScheme.COLUMN_NAME} ASC")
    fun getFavoritesObservable(): Flow<List<FavoriteLocationEntity>>

    @Query("SELECT * FROM ${FavoriteLocationScheme.TABLE_NAME} WHERE ${FavoriteLocationScheme.COLUMN_ID} = :id LIMIT 1")
    suspend fun getById(id: Long): FavoriteLocationEntity?

    @Query("SELECT * FROM ${FavoriteLocationScheme.TABLE_NAME} WHERE ${FavoriteLocationScheme.COLUMN_LATITUDE} = :latitude AND ${FavoriteLocationScheme.COLUMN_LONGITUDE} = :longitude LIMIT 1")
    fun getByCoordinatesObservable(latitude: Double, longitude: Double): Flow<FavoriteLocationEntity?>

    @Query("SELECT * FROM ${FavoriteLocationScheme.TABLE_NAME} WHERE ${FavoriteLocationScheme.COLUMN_LATITUDE} = :latitude AND ${FavoriteLocationScheme.COLUMN_LONGITUDE} = :longitude LIMIT 1")
    suspend fun getByCoordinates(latitude: Double, longitude: Double): FavoriteLocationEntity?

    @Upsert
    suspend fun upsert(entity: FavoriteLocationEntity)

    @Query("DELETE FROM ${FavoriteLocationScheme.TABLE_NAME} WHERE ${FavoriteLocationScheme.COLUMN_ID} = :id")
    suspend fun deleteById(id: Long)
}