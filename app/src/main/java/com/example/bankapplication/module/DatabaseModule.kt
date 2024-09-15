package com.example.bankapplication.module

import android.content.Context
import androidx.room.Room
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
        ).build()
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
