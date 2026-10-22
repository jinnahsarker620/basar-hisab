package com.example.data

import androidx.room.*
import com.example.model.KharchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KharchDao {

  @Query("SELECT * FROM kharch_table ORDER BY dateMillis DESC, id DESC")
  fun getAllExpenses(): Flow<List<KharchEntity>>

  @Query("SELECT * FROM kharch_table WHERE category = :category ORDER BY dateMillis DESC, id DESC")
  fun getExpensesByCategory(category: String): Flow<List<KharchEntity>>

  @Query("SELECT * FROM kharch_table WHERE dateMillis BETWEEN :startTime AND :endTime ORDER BY dateMillis DESC")
  fun getExpensesBetweenDates(startTime: Long, endTime: Long): Flow<List<KharchEntity>>

  @Query("SELECT SUM(taka) FROM kharch_table")
  fun getTotalExpense(): Flow<Double?>

  @Query("SELECT COUNT(*) FROM kharch_table")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: KharchEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(expenses: List<KharchEntity>)

  @Update
  suspend fun updateExpense(expense: KharchEntity)

  @Delete
  suspend fun deleteExpense(expense: KharchEntity)

  @Query("DELETE FROM kharch_table WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM kharch_table")
  suspend fun deleteAll()
}
