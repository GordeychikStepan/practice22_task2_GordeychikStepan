package com.bignerdranch.android.interestingnumbers

import androidx.room.Database
import androidx.room.RoomDatabase

// база данных, которая использует сущность и интерфейс доступа
@Database(entities = [Fact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun factDao(): FactDao
}