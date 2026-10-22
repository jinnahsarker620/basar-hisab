package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.KharchEntity
import com.example.model.MemberEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
  entities = [KharchEntity::class, MemberEntity::class],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun kharchDao(): KharchDao
  abstract fun memberDao(): MemberDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "ghorer_hisab_db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.kharchDao(), database.memberDao())
          }
        }
      }

      suspend fun populateInitialData(kharchDao: KharchDao, memberDao: MemberDao) {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val curMonth = cal.get(Calendar.MONTH)
        val curYear = cal.get(Calendar.YEAR)

        // Initial Members
        val initialMembers = listOf(
          MemberEntity(id = 1, name = "আবদুল্লাহ আল মামুন"),
          MemberEntity(id = 2, name = "বাবা"),
          MemberEntity(id = 3, name = "মা")
        )
        memberDao.insertAll(initialMembers)

        // Initial Expenses
        val initialExpenses = listOf(
          KharchEntity(
            id = 1,
            category = "বাজার",
            taka = 2500.0,
            dateMillis = now - (oneDay * 2),
            tarikhDisplay = "১২ নভেম্বর",
            note = "সাপ্তাহিক কাঁচা বাজার ও মাছ",
            paymentMethod = "নগদ",
            member = "আবদুল্লাহ আল মামুন",
            bazarKoreche = "আবদুল্লাহ আল মামুন",
            month = curMonth,
            year = curYear
          ),
          KharchEntity(
            id = 2,
            category = "ওয়াইফাই বিল",
            taka = 800.0,
            dateMillis = now - (oneDay * 4),
            tarikhDisplay = "১০ নভেম্বর",
            note = "নভেম্বর মাসের ইন্টারনেট বিল",
            paymentMethod = "বিকাশ",
            member = "পরিবার",
            bazarKoreche = "আবদুল্লাহ আল মামুন",
            month = curMonth,
            year = curYear
          ),
          KharchEntity(
            id = 3,
            category = "কারেন্ট বিল",
            taka = 1450.0,
            dateMillis = now - (oneDay * 6),
            tarikhDisplay = "০৮ নভেম্বর",
            note = "ডেসকো বিদ্যুৎ বিল",
            paymentMethod = "বিকাশ",
            member = "পরিবার",
            bazarKoreche = "আবদুল্লাহ আল মামুন",
            month = curMonth,
            year = curYear
          ),
          KharchEntity(
            id = 4,
            category = "গ্যাস",
            taka = 1080.0,
            dateMillis = now - (oneDay * 9),
            tarikhDisplay = "০৫ নভেম্বর",
            note = "তিতাস গ্যাস প্রিপেইড কার্ড রিচার্জ",
            paymentMethod = "নগদ",
            member = "পরিবার",
            bazarKoreche = "বাবা",
            month = curMonth,
            year = curYear
          ),
          KharchEntity(
            id = 5,
            category = "ওষুধ ও চিকিৎসা",
            taka = 620.0,
            dateMillis = now - (oneDay * 10),
            tarikhDisplay = "০৪ নভেম্বর",
            note = "নিয়মিত প্রেসার ও গ্যাসট্রিকের ওষুধ",
            paymentMethod = "নগদ",
            member = "বাবা",
            bazarKoreche = "আবদুল্লাহ আল মামুন",
            month = curMonth,
            year = curYear
          )
        )
        kharchDao.insertAll(initialExpenses)
      }
    }
  }
}
