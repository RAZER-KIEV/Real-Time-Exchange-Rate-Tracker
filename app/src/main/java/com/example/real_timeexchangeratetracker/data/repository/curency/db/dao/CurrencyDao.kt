package com.example.real_timeexchangeratetracker.data.repository.curency.db.dao

import androidx.room.*
import com.example.real_timeexchangeratetracker.data.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<Currency>)

    @Query("SELECT * FROM currencies ORDER BY shortName ASC")
    suspend fun getAllCurrencies(): List<Currency>

    @Query("SELECT * FROM currencies WHERE isMonitored = 1")
    fun getMonitoredCurrencies(): Flow<List<Currency>>


    @Query("SELECT * FROM currencies WHERE id = :currencyId")
    fun getCurrencyById(currencyId: Long): Currency?

    @Query("SELECT * FROM currencies WHERE isCrypto = :isCrypto ORDER BY shortName ASC")
    fun getCurrenciesByType(isCrypto: Boolean): Flow<List<Currency>>

    @Query("SELECT * FROM currencies WHERE longName LIKE '%' || :query || '%' OR shortName LIKE '%' || :query || '%' ORDER BY shortName ASC")
    fun searchCurrenciesByName(query: String): Flow<List<Currency>>

    @Update
    suspend fun update(currency: Currency)

    @Delete
    suspend fun delete(currency: Currency)

    @Query("DELETE FROM currencies WHERE id = :currencyId")
    suspend fun deleteById(currencyId: Long)

    @Query("DELETE FROM currencies")
    suspend fun deleteAll()
}