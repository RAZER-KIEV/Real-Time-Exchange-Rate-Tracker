package com.example.real_timeexchangeratetracker.data.repository.curency.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.real_timeexchangeratetracker.data.model.CurrencyRateRecord

@Dao
interface CurrencyRateRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencyRateRecord: CurrencyRateRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<CurrencyRateRecord>)

    @Update
    suspend fun update(currencyRateRecord: CurrencyRateRecord)

    @Delete
    suspend fun delete(currencyRateRecord: CurrencyRateRecord)

    @Query("DELETE FROM statuses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM statuses WHERE id = :id")
    suspend fun getById(id: Long): CurrencyRateRecord?

    @Query("SELECT * FROM statuses")
    suspend fun getAll(): List<CurrencyRateRecord>

    @Query("SELECT * FROM statuses WHERE currencyId = :currencyId")
    suspend fun getByCurrencyId(currencyId: Long): List<CurrencyRateRecord>
}