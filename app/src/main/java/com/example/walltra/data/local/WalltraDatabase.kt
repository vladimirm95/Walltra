package com.example.walltra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.walltra.data.local.dao.CategoryDao
import com.example.walltra.data.local.dao.ExpenseDao
import com.example.walltra.data.local.dao.PeriodDao
import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import com.example.walltra.data.model.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Category::class, Expense::class, Period::class],
    version = 1,
    exportSchema = false
)
abstract class WalltraDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun periodDao(): PeriodDao

    companion object {
        fun create(context: Context): WalltraDatabase {
            return Room.databaseBuilder(
                context,
                WalltraDatabase::class.java,
                "walltra_database"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            
                        }
                    }
                })
                .build()
        }
    }
}