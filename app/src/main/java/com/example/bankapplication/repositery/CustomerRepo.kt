package com.example.bankapplication.repositery

import com.example.bankapplication.Account
import com.example.bankapplication.Customer
import com.example.bankapplication.dao.AccountDao
import com.example.bankapplication.dao.BankManagerDao

import com.example.bankapplication.dao.CustomerDao
import javax.inject.Inject

class CustomerRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private val accountDao: AccountDao,
    private val bankManagerDao: BankManagerDao
) {
    suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)

    suspend fun getBankManagerByEmail(email: String) = bankManagerDao.getBankManagerByEmail(email)

    suspend fun deleteCustomerByEmail(email: String) = customerDao.deleteCustomerByEmail(email)

    suspend fun getCustomerByEmail(email: String) = customerDao.getCustomerByEmail(email)

    suspend fun getAllCustomers() = customerDao.getAllCustomers()

    suspend fun getAccountByAccountNumber(accountNumber: String) = accountDao.getAccountByAccountNumber(accountNumber)

    suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)

    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
}