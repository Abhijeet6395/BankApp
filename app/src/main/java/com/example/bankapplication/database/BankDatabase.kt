package com.example.bankapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bankapplication.Account
import com.example.bankapplication.AccountTypeConverter
import com.example.bankapplication.Customer
import com.example.bankapplication.BankManager
import com.example.bankapplication.dao.AccountDao
import com.example.bankapplication.dao.CustomerDao
import com.example.bankapplication.dao.BankManagerDao

@Database(entities = [Customer::class, Account::class, BankManager::class], version = 2)
@TypeConverters(AccountTypeConverter::class)
abstract class BankDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun accountDao(): AccountDao
    abstract fun bankManagerDao(): BankManagerDao
}
