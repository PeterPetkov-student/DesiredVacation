package com.example.desiredvacationsapp.appDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.desiredvacationsapp.daoObject.VacationDao
import com.example.desiredvacationsapp.models.Vacation

@Database(entities = [Vacation::class], version = 2, exportSchema = false)
abstract class VacationDatabase  : RoomDatabase() {

    abstract fun vacationDao(): VacationDao

    companion object {
        @Volatile
        private var INSTANCE: VacationDatabase? = null

        fun getDatabase(context: Context): VacationDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VacationDatabase::class.java,
                    "vacation_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                 //return instance
                instance
            }
        }
    }
}