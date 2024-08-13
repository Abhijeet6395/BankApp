package com.example.bankapplication


import androidx.compose.runtime.mutableIntStateOf

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.file.WatchEvent


class BankViewModel : ViewModel() {
    // Example account balance
    var accountBalance = mutableIntStateOf(0) // Default balance

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    fun addUser(user: User) {
        _users.value += user
    }

    fun removeUser(user: User) {
        _users.value -= user
    }

    fun addMoney(amount: Int) {
        accountBalance.intValue += amount
    }

    fun withdrawMoney(amount: Int) {
        if (amount <= accountBalance.intValue) {
            accountBalance.intValue -= amount
        }
    }
}