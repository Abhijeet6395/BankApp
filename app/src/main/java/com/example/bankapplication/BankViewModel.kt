package com.example.bankapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class User(
    val id: String,
    val name: String,
    val accountType: String = "Savings",
    val balance: Double
)

data class Account(
    val id: String,
    val name: String,
    val balance: Double,
    val type:String
)

class BankViewModel : ViewModel() {
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> get() = _accounts

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private val _customer = MutableStateFlow<User?>(null)
    val customer: StateFlow<User?> get() = _customer

    fun addAccount(account: Account) {
        _accounts.value += account
        updateUsersList()
    }

    fun addUser(user: User) {
        _users.value += user
    }

    fun removeUser(user: User) {
        _users.value = _users.value.filterNot { it.id == user.id }
        _accounts.value = _accounts.value.filterNot { it.id == user.id }
    }

    fun updateCustomer(customer: User) {
        _customer.value = customer
    }

    fun addMoney(amount: Double) {
        _customer.value?.let { customer ->
            _customer.value = customer.copy(balance = customer.balance + amount)
        }
    }

    fun withdrawMoney(amount: Double) {
        _customer.value?.let { customer ->
            if (customer.balance >= amount) {
                _customer.value = customer.copy(balance = customer.balance - amount)
            }
        }
    }
    private fun updateUsersList() {
        val updatedUser = _customer.value
        if (updatedUser != null) {
            _users.value = _users.value.map {
                if (it.id == updatedUser.id) updatedUser else it
            }
        }
    }
    fun updateCustomerName(newName: String) {
        _customer.value = _customer.value?.copy(name = newName)
        updateUsersList()
    }
    fun getUserByAccount(account: Account): User? {
        return _users.value.find { user ->
            user.id == account.id
        }
    }
}
