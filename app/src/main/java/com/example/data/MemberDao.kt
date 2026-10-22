package com.example.data

import androidx.room.*
import com.example.model.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
  @Query("SELECT * FROM member_table ORDER BY id ASC")
  fun getAllMembers(): Flow<List<MemberEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: MemberEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(members: List<MemberEntity>)

  @Delete
  suspend fun deleteMember(member: MemberEntity)

  @Query("DELETE FROM member_table WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT COUNT(*) FROM member_table")
  suspend fun getCount(): Int
}
