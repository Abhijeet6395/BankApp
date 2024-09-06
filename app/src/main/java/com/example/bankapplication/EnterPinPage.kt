package com.example.bankapplication

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EnterPinPage(
    navController: NavController,
    userType: String,
    bankViewModel: BankViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter ${if (userType == "banker") "Banker" else "Customer"} PIN",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                showError = false
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            ),
            singleLine = true,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                pin = it
                showError = false
            },
            label = { Text("PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            ),
            singleLine = true,
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (showError) {
            Text(
                text = "Incorrect email or PIN",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val result = bankViewModel.signIn(email, pin, userType)
            if (result) {
                bankViewModel.setCurrentCustomerEmail(email = email)

                val destination = if (userType == "banker")
                    "bankerDetails"
                else "customerDetails"
                navController.navigate("$destination/$email") {
                    popUpTo("login")
                    { inclusive = true }
                }
            } else {
                showError = true
            }
        }) {
            Text("Login")
        }
    }
}