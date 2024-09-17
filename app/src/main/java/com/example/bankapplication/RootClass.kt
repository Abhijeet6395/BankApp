package com.example.bankapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Embedded
import androidx.room.TypeConverter

// Base class for person

enum class AccountType {
    SAVINGS,
    CURRENT
}

class AccountTypeConverter {
    @TypeConverter
    fun fromAccountType(value: AccountType): String {
        return value.name
    }

    @TypeConverter
    fun toAccountType(value: String): AccountType {
        return AccountType.valueOf(value)
    }
}
// Data class for Account as a Room entity
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey
    @ColumnInfo(name = "account_number")
    var accountNumber: String,

    @ColumnInfo(name = "account_type")
    var accountType: String,

    @ColumnInfo(name = "balance")
    var balance: Double
)
@Entity(
    tableName = "customers",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["account_number"],
            childColumns = ["account_number"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Customer(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    @ColumnInfo(name = "email")
    val email: String,
    //Unique Section
    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "pin")
    var pin: String,

    @ColumnInfo(name = "account_number")
    var accountNumber: String,

    @ColumnInfo(name = "account_type")
    var accountType: String,//Enum banana hai

)

// Data class for BankManager as a Room entity
@Entity(tableName = "bank_managers")
data class BankManager(
    @PrimaryKey
    @ColumnInfo(name = "email")
    val email: String , // Sample email

    @ColumnInfo(name = "name")
    var name: String, // Sample name

    @ColumnInfo(name = "pin")
    var pin: String, // Sample PIN

    @ColumnInfo(name = "branch")
    var branch: String, // Sample branch

    @ColumnInfo(name = "role")
    var role: String // Sample role
)
