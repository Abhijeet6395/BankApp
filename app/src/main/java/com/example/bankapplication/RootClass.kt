package com.example.bankapplication

import android.content.ContentValues.TAG
import android.util.Log
import kotlin.math.log

open class Person(var name: String, var email: String, var pin: String) {


}

class Customer(
    name: String,
    email: String,
    pin: String,
    var account: Account
) : Person(name, email, pin) {
    companion object {
        val customerMap: MutableMap<String, Customer> = mutableMapOf()


        init {


            customerMap["hi@example.com"] = Customer(
                "hi",
                "hi@example.com",
                "1234",
                Account("123456", "savings", 1000.0)
            )
            customerMap["Hello@example.com"] = Customer(
                "Hello",
                "jane.smith@example.com",
                "5678",
                Account("654321", "current", 2000.0)
            )
            customerMap["bhak@example.com"] = Customer(
                "bhak",
                "bhak@example.com",
                "9101",
                Account("112233", "savings", 1500.0)
            )
            logCustomerMap()
        }


        fun logCustomerMap() {
            Log.d(TAG, "Current Customer Map:")
            customerMap.forEach { (email, customer) ->
                Log.d(TAG, "Email: $email, Name: ${customer.name}, PIN: ${customer.pin}, Account Number: ${customer.account.accountNumber}, Balance: ${customer.account.balance}")
            }
        }
    }
}

class Account(
    var accountNumber: String,
    var accountType: String,
    var balance: Double
) {

}

class BankManager(
    name: String,
    email: String,
    pin: String,
    var accountNumber: String,
    var branch: String,
    var role: String,
    var accounts: List<Account> = emptyList()
) : Person(name, email, pin) {
    companion object {

        val bankerMap: MutableMap<String, BankManager> = mutableMapOf()


        init {

            bankerMap["banker@example.com"] = BankManager(
                "Banker",
                "banker@example.com",
                "1234",
                "000111",
                "Main Branch",
                "Admin"
            )
        }




    }
}
