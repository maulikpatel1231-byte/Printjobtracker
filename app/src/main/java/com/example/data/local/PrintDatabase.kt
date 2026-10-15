package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PrintJobEntity::class], version = 1, exportSchema = false)
abstract class PrintDatabase : RoomDatabase() {
  abstract fun printJobDao(): PrintJobDao

  companion object {
    @Volatile
    private var INSTANCE: PrintDatabase? = null

    fun getDatabase(context: Context): PrintDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PrintDatabase::class.java,
          "print_jobs_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
