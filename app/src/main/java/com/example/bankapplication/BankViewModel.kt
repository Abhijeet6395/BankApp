package com.example.bankapplication

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bankapplication.Customer.Companion.customerMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class BankViewModel : ViewModel() {

    private val _customers = MutableStateFlow(Customer.customerMap)
    val customers: StateFlow<Map<String, Customer>> = _customers

    private val _bankManagers = MutableStateFlow(BankManager.bankerMap)
    val bankManagers: StateFlow<Map<String, BankManager>> = _bankManagers

    init {
        addBankManager(
            "Admin",
            "admin@example.com",
            1234.toString(),
            "000111",
            "Main Branch",
            "Admin"
        )
    }

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> = _logout

    fun logout() {
        _logout.value = true
    }

    fun onLogoutComplete() {
        _logout.value = false
    }

    fun addBankManager(
        name: String,
        email: String,
        pin: String,
        accountNumber: String,
        branch: String,
        role: String
    ) {
        val newManager = BankManager(name, email, pin, accountNumber, branch, role, emptyList())
        _bankManagers.update { bankerMap ->
            (bankerMap + (email to newManager)) as MutableMap<String, BankManager>
        }
    }

    fun addMoney(email: String, amount: Double) {
        _customers.update { customerMap ->
            val mutableMap = customerMap.toMutableMap()
            mutableMap[email]?.let { customer ->
                val updatedAccount = Account(
                    customer.account.accountNumber,
                    customer.account.accountType,
                    customer.account.balance + amount
                )
                mutableMap[email] =
                    Customer(customer.name, customer.email, customer.pin, updatedAccount)
            }
            mutableMap
        }
    }

    fun withdrawMoney(email: String, amount: Double) {
        _customers.update { customerMap ->
            val mutableMap = customerMap.toMutableMap()
            mutableMap[email]?.let { customer ->
                if (customer.account.balance >= amount) {
                    val updatedAccount = Account(
                        customer.account.accountNumber,
                        customer.account.accountType,
                        customer.account.balance - amount
                    )
                    mutableMap[email] =
                        Customer(customer.name, customer.email, customer.pin, updatedAccount)
                }
            }
            mutableMap
        }
    }


    fun signIn(email: String, pin: String, userType: String): Boolean {
        return when (userType) {
            "customer" -> {
                customerMap.forEach { (email, customer) ->
                    Log.d(
                        TAG,
                        "Email: $email, Name: ${customer.name}, Account Number: ${customer.account.accountNumber}, Balance: ${customer.account.balance}"
                    )
                }
                _customers.value[email]?.let {
                    it.pin == pin
                } ?: false
            }

            "banker" -> {
                _bankManagers.value[email]?.let {
                    it.pin == pin
                } ?: false
            }

            else -> false
        }
    }

    fun addCustomer(
        name: String,
        email: String,
        pin: String,
        accountType: String,
        initialBalance: Double
    ) {
        // Create a new Customer object
        val newCustomer = Customer(
            name = name,
            email = email,
            pin = pin,
            account = Account(
                accountNumber = UUID.randomUUID().toString(),
                accountType = accountType,
                balance = initialBalance
            )
        )
        customerMap[email] = newCustomer

        _customers.value = customerMap
    }


    fun removeCustomer(name: String, email: String) {
        _customers.update { currentMap ->
            currentMap.toMutableMap().apply {
                remove(email)
            }
        }
    }


    fun changePin(email: String, currentPin: String, newPin: Int): Boolean {
        var pinChanged = false

        _bankManagers.update { bankerMap ->
            val mutableMap = bankerMap.toMutableMap()
            mutableMap[email]?.let {
                if (it.pin == currentPin) {
                    mutableMap[email] = BankManager(
                        name = it.name,
                        email = it.email,
                        pin = newPin.toString(),
                        accountNumber = it.accountNumber,
                        branch = it.branch,
                        role = it.role,
                        accounts = it.accounts
                    )
                    pinChanged = true
                }
            }
            mutableMap
        }

        _customers.update { customerMap ->
            val mutableMap = customerMap.toMutableMap()
            mutableMap[email]?.let {
                if (it.pin == currentPin) {
                    mutableMap[email] = Customer(
                        name = it.name,
                        email = it.email,
                        pin = newPin.toString(),
                        account = it.account
                    )
                    pinChanged = true
                }
            }
            mutableMap
        }

        return pinChanged
    }
}