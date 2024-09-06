package com.example.bankapplication

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bankapplication.Customer.Companion.customerMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class BankViewModel : ViewModel() {

    private val _customers = MutableStateFlow(customerMap)
    val customers: StateFlow<Map<String, Customer>> = _customers

    private val _bankManagers = MutableStateFlow(BankManager.bankerMap)
    val bankManagers: StateFlow<Map<String, BankManager>> = _bankManagers

    var email = mutableStateOf("")
        private set
    private val _currentCustomerEmail = MutableStateFlow("")

    val currentCustomerEmail: StateFlow<String> = _currentCustomerEmail

    private val _currentUser = MutableStateFlow<Any?>(null)
    var triggerRecomposition by mutableStateOf(false)

    fun setCurrentCustomerEmail(email: String) {
        _currentCustomerEmail.value = email
    }

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


    fun onLogoutComplete() {
        _logout.value = false
        email.value = ""
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


    fun signIn(inputEmail: String, pin: String, userType: String): Boolean {

        val isValidUser = when (userType) {

            "customer" -> _customers.value[inputEmail]?.let { it.pin == pin } ?: false

            "banker" -> _bankManagers.value[inputEmail]?.let { it.pin == pin } ?: false
            else -> false
        }


        if (isValidUser) {
            email.value = inputEmail
            _currentUser.value = when (userType) {
                "customer" -> _customers.value[inputEmail]
                "banker" -> _bankManagers.value[inputEmail]
                else -> null
            }
        }

        return isValidUser
    }

    fun addCustomer(
        name: String,
        email: String,
        pin: String,
        accountType: String,
        initialBalance: Double
    ) {
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


        //new pendingCustomers Map,
        //populate the new map from the old map using for loop
        // pendingCustomer[email]=newCustomer
        //_customer.value.


//        val pendingCustomers = mutableMapOf<String, Customer>()
//
//        // Populate the new map from the old map using a for loop
//        for ((key, value) in _customers.value) {
//            pendingCustomers[key] = value
//        }
//
//
//        pendingCustomers[email] = newCustomer
//
//        // Update the _customers with the new pendingCustomers map
//        _customers.value = pendingCustomers

        _customers.update { currentMap ->
            val updatedMap = currentMap.toMutableMap()
            updatedMap[email] = newCustomer

            updatedMap // Return the updatedMap for StateFlow
        }

        customerMap[email] = newCustomer
        _customers.update { customerMap }
        triggerRecomposition=!triggerRecomposition

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

                    if (_currentUser.value is BankManager && (_currentUser.value as BankManager).email == email) {
                        _currentUser.value = mutableMap[email]
                    }
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
                    if (_currentUser.value is Customer && (_currentUser.value as Customer).email == email) {
                        _currentUser.value = mutableMap[email]
                    }

                }
            }
            mutableMap
        }

        return pinChanged
    }

}