package com.example.bankapplication.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.bankapplication.Customer

import com.example.bankapplication.Account
import com.example.bankapplication.BankManager

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE email = :email")
    suspend fun getCustomerByEmail(email: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Query("DELETE FROM customers WHERE email = :email")
    suspend fun deleteCustomerByEmail(email: String)


    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("SELECT * FROM customers")
    suspend fun getAllCustomers(): List<Customer>
}

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Query("SELECT * FROM accounts WHERE account_number = :accountNumber")
    suspend fun getAccountByAccountNumber(accountNumber: String): Account?
}

@Dao
interface BankManagerDao {
    @Query("SELECT * FROM bank_managers WHERE email = :email")
    suspend fun getBankManagerByEmail(email: String): BankManager?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankManager(bankManager: BankManager)
}