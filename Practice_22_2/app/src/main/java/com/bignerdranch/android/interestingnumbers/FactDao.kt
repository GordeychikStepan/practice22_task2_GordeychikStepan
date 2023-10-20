package com.bignerdranch.android.interestingnumbers

import androidx.room.*

// интерфейс для доступа к базе данных и методы
@Dao
interface FactDao {
    @Query("SELECT * FROM facts ORDER BY id DESC LIMIT 10")
    suspend fun getRecentFacts(): List<Fact>

    @Insert
    suspend fun insertFact(fact: Fact)

    @Delete
    suspend fun deleteFact(fact: Fact)

    @Query("DELETE FROM facts")
    suspend fun deleteAllFacts()

    @Update
    suspend fun updateFact(fact: Fact)
}