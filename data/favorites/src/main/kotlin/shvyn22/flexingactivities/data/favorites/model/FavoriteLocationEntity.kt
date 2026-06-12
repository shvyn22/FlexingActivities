package shvyn22.flexingactivities.data.favorites.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = FavoriteLocationScheme.TABLE_NAME)
data class FavoriteLocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_ID) val id: Long = 0,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_NAME) val name: String,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_COUNTRY) val country: String,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_LATITUDE) val latitude: Double,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_LONGITUDE) val longitude: Double,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_SKIING_SCORE) val skiingScore: Int,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_SURFING_SCORE) val surfingScore: Int,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_OUTDOOR_SCORE) val outdoorScore: Int,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_INDOOR_SCORE) val indoorScore: Int,
    @ColumnInfo(name = FavoriteLocationScheme.COLUMN_UPDATED_AT) val updatedAt: Long,
)