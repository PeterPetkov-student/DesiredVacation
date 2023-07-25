package com.example.desiredvacationsapp.daoObject

import androidx.room.*
import com.example.desiredvacationsapp.models.Vacation
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationDao {

    @Query("SELECT * from Vacation ORDER BY name ASC")
    fun allVacations(): Flow<List<Vacation>>

    @Query("SELECT * from Vacation WHERE id = :id")
    fun getItem(id: Int): Flow<Vacation>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vacation: Vacation)

    @Update
    suspend fun update(vacation: Vacation)

    @Delete
    suspend fun delete(vacation: Vacation)
}