package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GreenPrimary

@Composable
fun ProfileDialog(
  currentName: String,
  currentPhone: String,
  currentBudget: Double,
  useBanglaDigits: Boolean,
  onDismiss: () -> Unit,
  onSaveProfile: (name: String, budget: Double, useBangla: Boolean) -> Unit,
  onLogout: () -> Unit
) {
  var name by remember { mutableStateOf(currentName) }
  var budgetText by remember {
    mutableStateOf(if (currentBudget % 1.0 == 0.0) currentBudget.toInt().toString() else currentBudget.toString())
  }
  var banglaDigits by remember { mutableStateOf(useBanglaDigits) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = "প্রোফাইল ও বাজেট সেটিংস",
        fontWeight = FontWeight.Bold,
        color = GreenPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Manager Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("ম্যানেজারের নাম") },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_name_input"),
          shape = RoundedCornerShape(12.dp)
        )

        // Monthly Budget
        OutlinedTextField(
          value = budgetText,
          onValueChange = { input ->
            if (input.all { it.isDigit() || it == '.' }) {
              budgetText = input
            }
          },
          label = { Text("মাসিক মোট বাজেট (৳)") },
          leadingIcon = {
            Icon(Icons.Default.Savings, contentDescription = null, tint = GreenPrimary)
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_budget_input"),
          shape = RoundedCornerShape(12.dp)
        )

        // Bangla Digits Switch
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "বাংলা সংখ্যা ব্যবহার",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
              )
              Text(
                text = if (banglaDigits) "যেমন: ৳ ২,৫০০" else "যেমন: ৳ 2,500",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
              )
            }
            Switch(
              checked = banglaDigits,
              onCheckedChange = { banglaDigits = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenPrimary
              )
            )
          }
        }

        // Phone information (read-only)
        if (currentPhone.isNotBlank()) {
          Text(
            text = "লগইন ফোন নম্বর: $currentPhone",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
          )
        }

        Divider()

        // Logout action
        OutlinedButton(
          onClick = {
            onDismiss()
            onLogout()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("logout_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          )
        ) {
          Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("লগআউট করুন")
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val budget = budgetText.toDoubleOrNull() ?: currentBudget
          onSaveProfile(name.trim().ifBlank { currentName }, budget, banglaDigits)
          onDismiss()
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("save_profile_button")
      ) {
        Text("সংরক্ষণ করুন", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("বাতিল")
      }
    }
  )
}
