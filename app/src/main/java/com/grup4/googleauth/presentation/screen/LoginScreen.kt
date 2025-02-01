package com.grup4.googleauth.presentation.screen

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.grup4.googleauth.R
import com.grup4.googleauth.presentation.navigation.AppNavigation
import com.grup4.googleauth.presentation.navigation.AppNavigationActivity
import com.grup4.googleauth.ui.theme.GoogleAuthTheme
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleAuthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginEmail()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "LoginEmailPreview")
@Composable
fun LoginEmailPreview() {
    GoogleAuthTheme {
        LoginEmail()
    }
}

@Composable
fun LoginEmail() {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showLoginForm.value) {
                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 60.sp
                )
                UserForm(
                    isCreateUser = false,
                ) { email, password ->
                    Log.d("Login", "Email: $email, Password: $password")
                    singWithEmailAndPassword(email, password)
                }
            } else {
                Text(
                    text = "Crea una cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 60.sp
                )
                UserForm(
                    isCreateUser = true,
                ) { email, password ->
                    Log.d("Crear cuenta", "Email: $email, Password: $password")
                    createUserWithEmailAndPassword(email, password)
                }
            }
            Spacer(
                modifier = Modifier
                    .height(10.dp),
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text1 =
                    if (showLoginForm.value) "¿No tienes cuenta? " else "¿Ya tienes cuenta? "
                val text2 =
                    if (showLoginForm.value) "Regístrate" else "Inicia sesión"
                Text(text1)
                Text(
                    text2,
                    modifier = Modifier
                        .clickable { showLoginForm.value = !showLoginForm.value }
                        .padding(5.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GoogleSignInButton()
            }
        }
    }
}

@Composable
fun UserForm(
    isCreateUser: Boolean = false,
    onLogin: (String, String) -> Unit = { email, password -> }
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val password = rememberSaveable {
        mutableStateOf("")
    }
    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmailInput(email = email)
        PasswordInput(
            password = password,
            labelId = "Password",
            passwordVisible = passwordVisible
        )
        SubmitButton(
            textId = if (isCreateUser) "Crear cuenta" else "Iniciar sesión",
            inputValido = valido
        ) {
            onLogin(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        enabled = inputValido
    ) {
        Text(
            text = textId,
            modifier = Modifier
                .padding(5.dp),
        )
    }

}

@Composable
fun PasswordInput(
    password: MutableState<String>,
    labelId: String,
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text(text = labelId) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        modifier = Modifier
            .padding(
                bottom = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (password.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else {
                null
            }
        }
    )
}

@Composable
fun PasswordVisibleIcon(passwordVisible: MutableState<Boolean>) {
    val image = if (passwordVisible.value) {
        Icons.Default.VisibilityOff
    } else {
        Icons.Default.Visibility
    }
    IconButton(onClick = {
        passwordVisible.value = !passwordVisible.value
    }) {
        Icon(
            imageVector = image,
            contentDescription = ""
        )
    }
}

@Composable
fun EmailInput(
    email: MutableState<String>,
    labelId: String = "Email"
) {
    InputFiel(
        valeState = email,
        labelId = labelId,
        keyboardType = KeyboardType.Email
    )
}

@Composable
fun InputFiel(
    valeState: MutableState<String>,
    labelId: String,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = valeState.value,
        onValueChange = { valeState.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(
                bottom = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}


fun singWithEmailAndPassword(email: String, password: String): Boolean {
    val auth: FirebaseAuth = Firebase.auth
    var result = false
    try {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = true
                } else {
                    result = false
                }
            }
    } catch (e: Exception) {
        Log.d("Login", "Login failed: ${e.message}")
    }
    return result
}

fun createUserWithEmailAndPassword(email: String, password: String) {
    val auth: FirebaseAuth = Firebase.auth
    var result = false
    try {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = true
                } else {
                    result = false
                }
            }
    } catch (e: Exception) {
        Log.d("Create user", "Create user failed: ${e.message}")
    }
}

@Composable
fun GoogleSignInButton() {
    val context = LocalContext.current
    val courutineScope = rememberCoroutineScope()

    val onClik: () -> Unit = {
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.googleServerClientId))
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        courutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                val credential = result.credential


                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                FirebaseAuth.getInstance().signInWithCredential(authCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var intent = Intent(context, AppNavigationActivity::class.java)
                            intent.putExtra("email", task.result?.user?.email)
                            intent.putExtra("provider", "Google")
                            context.startActivity(intent, null)
                        } else {
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                Log.i(TAG, googleIdToken)

                Toast.makeText(context, "You are signed in", Toast.LENGTH_SHORT).show()

            } catch (e: GetCredentialException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Button(onClick = onClik) {
        Text("Sign in with Google")
    }
}