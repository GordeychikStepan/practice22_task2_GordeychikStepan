package com.bignerdranch.android.interestingnumbers

import androidx.room.Entity
import androidx.room.PrimaryKey

// сущность (Entity) для сохранения фактов в базе данных
@Entity(tableName = "facts")
data class Fact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var text: String
)
