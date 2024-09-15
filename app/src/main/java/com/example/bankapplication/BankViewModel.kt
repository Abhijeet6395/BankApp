package com.example.bankapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapplication.repositery.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel

class BankViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _bankManager = MutableStateFlow<BankManager?>(null)
    val bankManager: StateFlow<BankManager?> = _bankManager.asStateFlow()

    private val _currentCustomerEmail = MutableStateFlow<String>("")
    val currentCustomerEmail: StateFlow<String> = _currentCustomerEmail.asStateFlow()

    private val _logout = MutableStateFlow<Boolean>(false)
    val logout: StateFlow<Boolean> = _logout.asStateFlow()
    var triggerRecomposition by mutableStateOf(false)

    init {
        viewModelScope.launch {
            _customers.value = repository.getAllCustomers()

        }
    }


    fun onLogoutComplete() {
        _logout.value = false
    }

    suspend fun signIn(email: String, pin: String, userType: String): Boolean {
        return when (userType) {
            "customer" -> {
                val customer = repository.getCustomerByEmail(email)
                customer != null && customer.pin == pin
            }

            "banker" -> {
                val bankManager = repository.getBankManagerByEmail(email)
                bankManager != null && bankManager.pin == pin
            }

            else -> false
        }
    }

    fun setCurrentCustomerEmail(email: String) {
        _currentCustomerEmail.value = email
    }

    fun addCustomer(
        name: String,
        email: String,
        pin: String,
        accountType: String,
        initialBalance: Double
    ) {
        viewModelScope.launch {
            val accountNumber = UUID.randomUUID().toString()
            val newAccount = Account(
                accountNumber = accountNumber,
                accountType = accountType,
                balance = initialBalance
            )
            val newCustomer = Customer(
                email = email,
                name = name,
                pin = pin,
                accountNumber = accountNumber,
                accountType = accountType
            )
            repository.insertAccount(newAccount)
            repository.insertCustomer(newCustomer)
            _customers.value = repository.getAllCustomers()
            triggerRecomposition = !triggerRecomposition
        }
    }
   suspend fun getAccountByAccountNumber(accountNumber: String): Account? {
        return repository.getAccountByAccountNumber(accountNumber)
    }
    suspend fun removeCustomer(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                repository.deleteCustomerByEmail(email) // Delete customer first
                _customers.value = repository.getAllCustomers() // Update the customer list
                true // Return true if successful
            } catch (e: Exception) {
                false
            }
        }
    }


    fun addMoney(email: String, amount: Double) {
        viewModelScope.launch {
            val customer = repository.getCustomerByEmail(email)
            customer?.let {
                val account = repository.getAccountByAccountNumber(it.accountNumber)
                account?.let { acc ->
                    val updatedAccount = Account(
                        accountNumber = acc.accountNumber,
                        accountType = acc.accountType,
                        balance = acc.balance + amount
                    )
                    repository.updateAccount(updatedAccount)
                    _customers.value = repository.getAllCustomers()
                    triggerRecomposition = !triggerRecomposition
                }
            }
        }
    }

    fun withdrawMoney(email: String, amount: Double) {
        viewModelScope.launch {
            val customer = repository.getCustomerByEmail(email)
            customer?.let {
                val account = repository.getAccountByAccountNumber(it.accountNumber)
                account?.let { acc ->
                    if (acc.balance >= amount) {
                        val updatedAccount = Account(
                            accountNumber = acc.accountNumber,
                            accountType = acc.accountType,
                            balance = acc.balance - amount
                        )
                        repository.updateAccount(updatedAccount)
                        _customers.value = repository.getAllCustomers()
                        triggerRecomposition = !triggerRecomposition
                    }
                }
            }
        }
    }

    fun changePin(email: String, currentPin: String, newPin: String): Boolean {
        var pinChanged = false
        viewModelScope.launch {
            val customer = repository.getCustomerByEmail(email)
            customer?.let {
                if (it.pin == currentPin) {
                    val updatedCustomer = Customer(
                        email = it.email,
                        name = it.name,
                        pin = newPin,
                        accountNumber = it.accountNumber,
                        accountType = it.accountType

                    )
                    repository.updateCustomer(updatedCustomer)
                    pinChanged = true
                }
            }
        }
        return pinChanged
    }
}
