package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenContainerLight
import com.example.ui.theme.GreenPrimary
import com.example.util.BanglaUtils

@Composable
fun SettingsScreen(
  uiState: HisabUiState,
  onSaveManager: (String) -> Unit,
  onUpdateBudget: (Double) -> Unit,
  onToggleBanglaDigits: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  var newManagerName by remember(uiState.managerName) { mutableStateOf(uiState.managerName) }
  var budgetText by remember(uiState.monthlyBudget) {
    mutableStateOf(
      if (uiState.monthlyBudget % 1.0 == 0.0) uiState.monthlyBudget.toInt().toString() else uiState.monthlyBudget.toString()
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .padding(bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(GreenContainerLight),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Settings, contentDescription = null, tint = GreenPrimary)
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "⚙️ সেটিংস ও প্রোফাইল",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            color = GreenPrimary
          )
        )
        Text(
          text = "ম্যানেজার নাম, বাজেট এবং অ্যাকাউন্ট ব্যবস্থাপনা",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }

    // 1. Change Manager Name Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Text(
          text = "ম্যানেজার নাম পরিবর্তন করুন",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = newManagerName,
          onValueChange = { newManagerName = it },
          label = { Text("ম্যানেজার / গৃহকর্তার নাম") },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_manager_name_input"),
          shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = { onSaveManager(newManagerName) },
          enabled = newManagerName.isNotBlank() && newManagerName != uiState.managerName,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_manager_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("সেভ করুন", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // 2. Monthly Budget Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Text(
          text = "মাসিক বাজেট নির্ধারণ",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

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
            .testTag("settings_budget_input"),
          shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = {
            val budget = budgetText.toDoubleOrNull() ?: uiState.monthlyBudget
            onUpdateBudget(budget)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_budget_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("বাজেট আপডেট করুন", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // 3. Number Preferences & Toggle
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "বাংলা সংখ্যা ব্যবহার",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = if (uiState.useBanglaDigits) "টাকা ও তারিখ বাংলা অঙ্কে প্রদর্শিত হচ্ছে (৳ ২,৫০০)" else "English Digits (৳ 2,500)",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        Switch(
          checked = uiState.useBanglaDigits,
          onCheckedChange = { onToggleBanglaDigits() },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = GreenPrimary
          )
        )
      }
    }

    // 4. Login Info & Logout Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Text(
          text = "লগইন তথ্য ও নিরাপত্তা",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "ম্যানেজার: ${uiState.managerName}", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "ফোন নম্বর: ${uiState.managerPhone}", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "💡 FB এর মতো একবার লগইন করলে মনে রাখবে",
          style = MaterialTheme.typography.labelSmall.copy(
            color = GreenPrimary,
            fontWeight = FontWeight.SemiBold
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
          onClick = onLogout,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("settings_logout_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          )
        ) {
          Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("লগআউট করুন", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
