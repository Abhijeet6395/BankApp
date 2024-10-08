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

    fun getBankManagerDetails(email: String) {
        viewModelScope.launch {
            val manager = repository.getBankManagerByEmail(email)
            _bankManager.value = manager
        }
    }
    suspend fun addCustomer(
        name: String,
        email: String,
        pin: String,
        accountType: String,
        initialBalance: Double
    ): Boolean {
        val existingCustomer = repository.getCustomerByEmail(email)
        if (existingCustomer == null) {
            val accountNumber = UUID.randomUUID().toString()
            val newAccount = Account(
                accountNumber= accountNumber,
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
            return true
        } else {
            return false
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

    suspend fun changePin(email: String, currentPin: String, newPin: String): Boolean {
        return withContext(Dispatchers.IO) {
            val customer = repository.getCustomerByEmail(email)
            if (customer != null && customer.pin == currentPin) {
                val updatedCustomer = customer.copy(pin = newPin)
                repository.updateCustomer(updatedCustomer) // Persist the update in the repository

                // Update the in-memory list of customers, iterate over it
                _customers.value = _customers.value.map { existingCustomer ->
                    if (existingCustomer.email == email) updatedCustomer else existingCustomer
                }

                true // PIN changed successfully
            } else {
                false // Current PIN incorrect
            }
        }
    }

}
