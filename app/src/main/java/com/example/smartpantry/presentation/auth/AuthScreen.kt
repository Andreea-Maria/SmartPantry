package com.example.smartpantry.presentation.auth


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val errorMessage by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Smart Pantry Login",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
                emailError = false
                authViewModel.clearError()
            },
            label = {
                Text("Email")
            },
            isError = emailError,
            supportingText = {
                if (emailError) {
                    Text("Please enter a valid email")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                passwordError = false
                authViewModel.clearError()
            },
            label = {
                Text("Password")
            },
            isError = passwordError,
            supportingText = {
                if (passwordError) {
                    Text("Password must contain at least 6 characters")
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val cleanEmail = email.trim()

                emailError =
                    cleanEmail.isBlank() ||
                            !android.util.Patterns.EMAIL_ADDRESS
                                .matcher(cleanEmail)
                                .matches()

                passwordError = password.length < 6

                if (emailError || passwordError) {
                    return@Button
                }

                authViewModel.login(
                    cleanEmail,
                    password
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        OutlinedButton(
            onClick = {
                val cleanEmail = email.trim()

                emailError =
                    cleanEmail.isBlank() ||
                            !android.util.Patterns.EMAIL_ADDRESS
                                .matcher(cleanEmail)
                                .matches()

                passwordError = password.length < 6

                if (emailError || passwordError) {
                    return@OutlinedButton
                }

                authViewModel.register(
                    cleanEmail,
                    password
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        errorMessage?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}