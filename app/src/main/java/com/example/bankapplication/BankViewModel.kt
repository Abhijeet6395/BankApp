package com.example.bankapplication

import androidx.lifecycle.ViewModel
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

    fun editBankManager(
        email: String,
        newName: String? = null,
        newPin: Int? = null,
        newAccountNumber: String? = null,
        newBranch: String? = null,
        newRole: String? = null
    ) {
        _bankManagers.update { bankerMap ->
            val mutableMap = bankerMap.toMutableMap()
            mutableMap[email]?.let { manager ->
                mutableMap[email] = BankManager(
                    name = newName ?: manager.name,
                    email = manager.email,
                    pin = newPin?.toString() ?: manager.pin,
                    accountNumber = newAccountNumber ?: manager.accountNumber,
                    branch =    newBranch ?: manager.branch,
                    role = newRole ?: manager.role,
                    accounts = manager.accounts
                )
            }
            mutableMap
        }
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
                mutableMap[email] = Customer(customer.name, customer.email, customer.pin, updatedAccount)
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
                    mutableMap[email] = Customer(customer.name, customer.email, customer.pin, updatedAccount)
                }
            }
            mutableMap
        }
    }

    fun editCustomerPin(email: String, newPin: Int) {
        _customers.update { customerMap ->
            val mutableMap = customerMap.toMutableMap()
            mutableMap[email]?.let { customer ->
                mutableMap[email] = Customer(customer.name, customer.email, newPin.toString(), customer.account)
            }
            mutableMap
        }
    }

    fun signIn(email: String, pin: String, userType: String): Boolean {
        return when (userType) {
            "customer" -> {
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
        val newCustomer = Customer(name, email, pin, Account(UUID.randomUUID().toString(), accountType, initialBalance))
        _customers.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[email] = newCustomer
            }
        }
    }

    fun removeCustomer(name: String,email: String) {
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