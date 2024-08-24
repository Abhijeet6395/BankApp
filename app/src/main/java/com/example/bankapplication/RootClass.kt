package com.example.bankapplication

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

        fun addBankManager(
            name: String,
            email: String,
            pin: String,
            accountNumber: String,
            branch: String,
            role: String
        ) {
            val newManager = BankManager(name, email, pin, accountNumber, branch, role)
            bankerMap[email] = newManager
        }

        // Function to remove a BankManager from the map
        fun removeBankManager(email: String) {
            bankerMap.remove(email)
        }

    }
    fun updateDetails(newName: String?, newPin: String?, newAccountNumber: String?, newBranch: String?, newRole: String?) {
        newName?.let { name = it }
        newPin?.let { pin = it }
        newAccountNumber?.let { accountNumber = it }
        newBranch?.let { branch = it }
        newRole?.let { role = it }
    }
}
