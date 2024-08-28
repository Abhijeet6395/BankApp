package com.example.bankapplication

import android.content.ContentValues.TAG
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapplication.Customer.Companion.customerMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    navController: NavController,
    email: String,
    bankViewModel: BankViewModel = viewModel()
) {

    val customer = customerMap[email]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") },
                actions = {
                    IconButton(onClick = {
                        performLogout(navController = navController)
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Logout")
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userType = "customer")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            customer?.let {
                Text(
                    text = "Customer Details",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Name: ${it.name}")
                Text(text = "Email: ${it.email}")
                Text(text = "Account Type: ${it.account.accountType}")
                Text(text = "Balance: ${it.account.balance}")
            }
        }
    }
}
