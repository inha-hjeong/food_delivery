package com.example.fooddelivery.screens.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fooddelivery.Destinations
import com.example.fooddelivery.screens.viewmodels.OrderViewModel
import com.example.fooddelivery.ui.theme.Yellow500
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun RegistrationScreen(navController: NavController, orderListViewModel: OrderViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold(

        topBar = {
            // App bar content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "FOOD DELIVERY", style = TextStyle(
                        fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Yellow500
                    )
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(30.dp))
                if (error.isNotEmpty()) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error,
                        style = TextStyle(fontSize = 14.sp)
                    )
                } else {
                    Text(text = "")
                }
                Button(
                    onClick = {
                        if (email.isNotEmpty() and password.isNotEmpty()) {
                            FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // add user email to order list for identification
                                        orderListViewModel.setUpUser(email)

                                        // set up database instance for authenticated user
                                        orderListViewModel.setUpDBInstance(
                                            FirebaseDatabase.getInstance(
                                                Destinations.firebaseDatabaseUrl
                                            )
                                        )

                                        // set up orders in the DB
                                        val usersRef =
                                            orderListViewModel.getDBRef()?.getReference("users")
                                        usersRef?.child(email.replace("@", "_").replace(".", "-"))
                                            ?.child("orders")

                                        navController.navigate(Destinations.Home)
                                    } else {
                                        error = task.exception?.localizedMessage ?: "Unknown error"
                                    }
                                }
                        } else {
                            error = "Please fill both email and password fields"
                        }
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow500
                    )
                ) {
                    Text("Register", style = TextStyle(fontSize = 20.sp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Back to ", style = TextStyle(fontSize = 14.sp)
                    )
                    Text(
                        text = "Login", modifier = Modifier.clickable {
                            navController.popBackStack()
                        }, style = TextStyle(
                            color = Color.Blue, fontSize = 14.sp
                        )
                    )
                }
            }
        },
    )

}