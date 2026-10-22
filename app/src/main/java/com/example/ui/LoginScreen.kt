package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryLight

enum class AuthMode {
  LOGIN,
  REGISTRATION,
  FORGET_PASSWORD
}

@Composable
fun LoginScreen(
  onLogin: (phone: String, pass: String) -> Boolean,
  onRegister: (name: String, phone: String, pass: String, confirmPass: String) -> String?,
  onResetPassword: (phone: String, newPass: String, confirmPass: String) -> String?,
  modifier: Modifier = Modifier
) {
  var mode by remember { mutableStateOf(AuthMode.LOGIN) }

  // Fields
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("01712345678") }
  var pass by remember { mutableStateOf("1234") }
  var confirmPass by remember { mutableStateOf("") }

  var showPassword by remember { mutableStateOf(false) }
  var showConfirmPassword by remember { mutableStateOf(false) }

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }

  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  // Reset messages when switching mode
  fun switchMode(newMode: AuthMode) {
    mode = newMode
    errorMessage = null
    successMessage = null
    confirmPass = ""
    showPassword = false
    showConfirmPassword = false
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0F1F0F),
            Color(0xFF142915),
            Color(0xFF0B170B)
          )
        )
      )
      .windowInsetsPadding(WindowInsets.safeDrawing)
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 440.dp)
        .verticalScroll(scrollState),
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF1A261B)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Icon Avatar
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = listOf(GreenPrimaryLight, GreenPrimary)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "হোম আইকন",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
          text = "ঘরের হিসাব",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 0.5.sp
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = when (mode) {
            AuthMode.LOGIN -> "আপনার অ্যাকাউন্টে লগইন করুন"
            AuthMode.REGISTRATION -> "নতুন ম্যানেজার অ্যাকাউন্ট খুলুন"
            AuthMode.FORGET_PASSWORD -> "পাসওয়ার্ড পরিবর্তন করুন"
          },
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color.White.copy(alpha = 0.65f)
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields Animated
        AnimatedContent(
          targetState = mode,
          label = "auth_form_transition"
        ) { currentMode ->
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            when (currentMode) {
              AuthMode.LOGIN -> {
                // Phone
                OutlinedTextField(
                  value = phone,
                  onValueChange = {
                    phone = it
                    errorMessage = null
                  },
                  label = { Text("ফোন নম্বর") },
                  placeholder = { Text("01712345678", color = Color.White.copy(alpha = 0.35f)) },
                  leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_phone_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                  ),
                  keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                // Password
                OutlinedTextField(
                  value = pass,
                  onValueChange = {
                    pass = it
                    errorMessage = null
                  },
                  label = { Text("পাসওয়ার্ড") },
                  placeholder = { Text("****", color = Color.White.copy(alpha = 0.35f)) },
                  leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                      Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                      )
                    }
                  },
                  visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                  ),
                  keyboardActions = KeyboardActions(
                    onDone = {
                      focusManager.clearFocus()
                      handleLoginAction(phone, pass, onLogin, { errorMessage = it })
                    }
                  )
                )
              }

              AuthMode.REGISTRATION -> {
                // Name
                OutlinedTextField(
                  value = name,
                  onValueChange = {
                    name = it
                    errorMessage = null
                  },
                  label = { Text("ম্যানেজার / গৃহকর্তার নাম") },
                  placeholder = { Text("যেমন: আবদুল্লাহ আল মামুন", color = Color.White.copy(alpha = 0.35f)) },
                  leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_name_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                  keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                // Phone
                OutlinedTextField(
                  value = phone,
                  onValueChange = {
                    phone = it
                    errorMessage = null
                  },
                  label = { Text("ফোন নম্বর (১১ ডিজিট)") },
                  placeholder = { Text("01712345678", color = Color.White.copy(alpha = 0.35f)) },
                  leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_phone_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                  ),
                  keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                // Password
                OutlinedTextField(
                  value = pass,
                  onValueChange = {
                    pass = it
                    errorMessage = null
                  },
                  label = { Text("পাসওয়ার্ড (কমপক্ষে ৪ সংখ্যা)") },
                  leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                      Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                      )
                    }
                  },
                  visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_password_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                  ),
                  keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                // Confirm Password
                OutlinedTextField(
                  value = confirmPass,
                  onValueChange = {
                    confirmPass = it
                    errorMessage = null
                  },
                  label = { Text("কনফার্ম পাসওয়ার্ড") },
                  leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                      Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                      )
                    }
                  },
                  visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_confirm_password_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                  ),
                  keyboardActions = KeyboardActions(
                    onDone = {
                      focusManager.clearFocus()
                      val err = onRegister(name, phone, pass, confirmPass)
                      if (err != null) {
                        errorMessage = err
                      }
                    }
                  )
                )
              }

              AuthMode.FORGET_PASSWORD -> {
                // Phone
                OutlinedTextField(
                  value = phone,
                  onValueChange = {
                    phone = it
                    errorMessage = null
                  },
                  label = { Text("নিবন্ধিত ফোন নম্বর") },
                  placeholder = { Text("01712345678", color = Color.White.copy(alpha = 0.35f)) },
                  leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forget_phone_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                  ),
                  keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                // New Password
                OutlinedTextField(
                  value = pass,
                  onValueChange = {
                    pass = it
                    errorMessage = null
                  },
                  label = { Text("নতুন পাসওয়ার্ড") },
                  leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                      Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                      )
                    }
                  },
                  visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forget_new_password_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                  ),
                  keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                // Confirm Password
                OutlinedTextField(
                  value = confirmPass,
                  onValueChange = {
                    confirmPass = it
                    errorMessage = null
                  },
                  label = { Text("কনফার্ম পাসওয়ার্ড") },
                  leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimaryLight)
                  },
                  trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                      Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                      )
                    }
                  },
                  visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forget_confirm_password_input"),
                  shape = RoundedCornerShape(14.dp),
                  colors = authTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                  ),
                  keyboardActions = KeyboardActions(
                    onDone = {
                      focusManager.clearFocus()
                      val err = onResetPassword(phone, pass, confirmPass)
                      if (err != null) {
                        errorMessage = err
                      } else {
                        successMessage = "পাসওয়ার্ড সফলভাবে পরিবর্তন হয়েছে! এখন লগইন করুন।"
                        switchMode(AuthMode.LOGIN)
                      }
                    }
                  )
                )
              }
            }
          }
        }

        // Error message
        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = errorMessage!!,
            color = Color(0xFFFF8A80),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("auth_error_message")
          )
        }

        // Success message
        if (successMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = successMessage!!,
            color = Color(0xFF81C784),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("auth_success_message")
          )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Main Submit Button
        Button(
          onClick = {
            focusManager.clearFocus()
            when (mode) {
              AuthMode.LOGIN -> {
                handleLoginAction(phone, pass, onLogin, { errorMessage = it })
              }
              AuthMode.REGISTRATION -> {
                val err = onRegister(name, phone, pass, confirmPass)
                if (err != null) {
                  errorMessage = err
                }
              }
              AuthMode.FORGET_PASSWORD -> {
                val err = onResetPassword(phone, pass, confirmPass)
                if (err != null) {
                  errorMessage = err
                } else {
                  successMessage = "পাসওয়ার্ড সফলভাবে পরিবর্তন হয়েছে! এখন লগইন করুন।"
                  switchMode(AuthMode.LOGIN)
                }
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag(
              when (mode) {
                AuthMode.LOGIN -> "login_submit_button"
                AuthMode.REGISTRATION -> "register_submit_button"
                AuthMode.FORGET_PASSWORD -> "forget_submit_button"
              }
            ),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text(
            text = when (mode) {
              AuthMode.LOGIN -> "লগইন করুন"
              AuthMode.REGISTRATION -> "রেজিস্ট্রেশন করুন"
              AuthMode.FORGET_PASSWORD -> "পাসওয়ার্ড পরিবর্তন করুন"
            },
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Links
        when (mode) {
          AuthMode.LOGIN -> {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "নতুন অ্যাকাউন্ট? রেজিস্ট্রেশন করুন",
                color = GreenPrimaryLight,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                  .clickable { switchMode(AuthMode.REGISTRATION) }
                  .testTag("switch_to_registration")
              )

              Text(
                text = "পাসওয়ার্ড ভুলে গেছেন?",
                color = Color.White.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                  .clickable { switchMode(AuthMode.FORGET_PASSWORD) }
                  .testTag("switch_to_forget_password")
              )
            }
          }

          AuthMode.REGISTRATION -> {
            Text(
              text = "ইতিমধ্যেই অ্যাকাউন্ট আছে? লগইন করুন",
              color = GreenPrimaryLight,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier
                .clickable { switchMode(AuthMode.LOGIN) }
                .testTag("switch_to_login_from_reg")
            )
          }

          AuthMode.FORGET_PASSWORD -> {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clickable { switchMode(AuthMode.LOGIN) }
                .testTag("back_to_login")
            ) {
              Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = GreenPrimaryLight,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "লগইন পেজে ফিরে যান",
                color = GreenPrimaryLight,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Persistent FB-style helper tag
        Text(
          text = "FB এর মতো একবার লগইন করলে মনে রাখবে",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp
          ),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

private fun handleLoginAction(
  phone: String,
  pass: String,
  onLogin: (String, String) -> Boolean,
  onError: (String) -> Unit
) {
  if (phone.isBlank() || pass.isBlank()) {
    onError("ফোন নম্বর ও পাসওয়ার্ড দিন")
    return
  }
  val success = onLogin(phone, pass)
  if (!success) {
    onError("ফোন বা পাসওয়ার্ড ভুল!")
  }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = Color.White,
  unfocusedTextColor = Color.White,
  focusedBorderColor = GreenPrimaryLight,
  unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
  focusedLabelColor = GreenPrimaryLight,
  unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
  focusedContainerColor = Color.White.copy(alpha = 0.05f),
  unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
)
