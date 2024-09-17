package com.example.bankapplication.module

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bankapplication.BankManager
import com.example.bankapplication.dao.AccountDao
import com.example.bankapplication.dao.BankManagerDao
import com.example.bankapplication.dao.CustomerDao
import com.example.bankapplication.database.BankDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BankDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            BankDatabase::class.java,
            "bank_database"
     ).fallbackToDestructiveMigration()
            .build()
    }


    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 1. Create a temporary table to store existing customer data
            database.execSQL(
                "CREATE TABLE customers_temp (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "email TEXT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "pin TEXT NOT NULL, " +
                        "account_number TEXT NOT NULL, " +
                        "account_type TEXT NOT NULL)"
            )
            // 2. Copy data from the original customers table to the temporary table
            database.execSQL("INSERT INTO customers_temp SELECT * FROM customers")

            // 3. Drop the original customers table
            database.execSQL("DROP TABLE customers")

            // 4. Create the new customers table with the foreign key
            database.execSQL(
                "CREATE TABLE customers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "email TEXT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "pin TEXT NOT NULL, " +
                        "account_number TEXT NOT NULL, " +
                        "account_type TEXT NOT NULL, " +
                        "FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE)"
            )

            // 5. Insert data back into the customers table from the temporary table
            database.execSQL("INSERT INTO customers SELECT * FROM customers_temp")

            //  6.Drop the temporary table
            database.execSQL("DROP TABLE customers_temp")
        }
    }

    @Provides
    @Singleton
    fun provideCustomerDao(bankDatabase: BankDatabase): CustomerDao {
        return bankDatabase.customerDao()
    }

    @Provides
    @Singleton
    fun provideAccountDao(bankDatabase: BankDatabase): AccountDao {
        return bankDatabase.accountDao()
    }

    @Provides
    @Singleton
    fun provideBankManagerDao(bankDatabase: BankDatabase): BankManagerDao {
        val bankManagerDao = bankDatabase.bankManagerDao()
        CoroutineScope(Dispatchers.IO).launch {
            if (bankManagerDao.getBankManagerByEmail("banker@example.com") == null) {
                val sampleBanker = BankManager(
                    email = "banker@example.com",
                    name = "Banker",
                    pin = "1234",
                    branch = "Main Branch",
                    role = "Manager"
                )
                bankManagerDao.insertBankManager(sampleBanker)
            }
        }
        return bankDatabase.bankManagerDao()
    }
}
