package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Categories
import com.example.model.KharchEntity
import com.example.model.MemberEntity
import com.example.ui.theme.GreenContainerLight
import com.example.ui.theme.GreenPrimary
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
  expenseToEdit: KharchEntity?,
  initialTaka: String = "",
  members: List<MemberEntity>,
  useBanglaDigits: Boolean,
  onDismiss: () -> Unit,
  onSave: (
    category: String,
    taka: Double,
    dateMillis: Long,
    note: String?,
    paymentMethod: String,
    member: String,
    bazarKoreche: String
  ) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember {
    mutableStateOf(expenseToEdit?.category ?: Categories.BAZAR.nameBn)
  }
  var takaText by remember {
    mutableStateOf(
      if (expenseToEdit != null) {
        val takaVal = expenseToEdit.taka
        if (takaVal % 1.0 == 0.0) takaVal.toInt().toString() else takaVal.toString()
      } else if (initialTaka.isNotBlank()) {
        initialTaka
      } else ""
    )
  }
  var noteText by remember {
    mutableStateOf(expenseToEdit?.note ?: "")
  }
  var selectedPaymentMethod by remember {
    mutableStateOf(expenseToEdit?.paymentMethod ?: "নগদ")
  }
  var forMember by remember {
    mutableStateOf(expenseToEdit?.member ?: "পরিবার")
  }
  var bazarBy by remember {
    mutableStateOf(expenseToEdit?.bazarKoreche ?: (members.firstOrNull()?.name ?: "আবদুল্লাহ আল মামুন"))
  }
  val dateMillis by remember {
    mutableStateOf(expenseToEdit?.dateMillis ?: System.currentTimeMillis())
  }

  val paymentMethods = listOf("নগদ", "বিকাশ", "রকেট", "কার্ড", "ব্যাংক")
  val quickAmounts = listOf(100, 500, 1000, 2000, 5000)

  // Candidate member names for "forMember" includes "পরিবার" + member list
  val memberOptions = remember(members) {
    listOf("পরিবার") + members.map { it.name }.filter { it != "পরিবার" }
  }

  // Candidate shoppers from members
  val shopperOptions = remember(members) {
    if (members.isNotEmpty()) members.map { it.name } else listOf("আবদুল্লাহ আল মামুন")
  }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    dragHandle = { BottomSheetDefaults.DragHandle() }
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (expenseToEdit != null) "খরচ সম্পাদনা করুন" else "নতুন খরচ যোগ করুন",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
          )
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Amount Input (Highlighted)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainerLight.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "টাকার পরিমাণ (৳)",
            style = MaterialTheme.typography.labelMedium.copy(
              color = GreenPrimary,
              fontWeight = FontWeight.Bold
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = takaText,
            onValueChange = { input ->
              if (input.all { it.isDigit() || it == '.' }) {
                takaText = input
              }
            },
            placeholder = {
              Text(
                "০",
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              color = GreenPrimary,
              textAlign = TextAlign.Center,
              fontSize = 32.sp
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("add_expense_taka_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GreenPrimary,
              unfocusedBorderColor = Color.Transparent,
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick Amount Pills
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
          ) {
            items(quickAmounts) { amt ->
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    val currentVal = takaText.toDoubleOrNull() ?: 0.0
                    val newVal = currentVal + amt
                    takaText = if (newVal % 1.0 == 0.0) newVal.toInt().toString() else newVal.toString()
                  },
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
              ) {
                Text(
                  text = "+${BanglaUtils.formatTaka(amt, useBanglaDigits)}",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                  )
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Category Selector
      Text(
        text = "ক্যাটাগরি নির্বাচন করুন",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(Categories.ALL) { cat ->
          val isSelected = selectedCategory.equals(cat.nameBn, ignoreCase = true)
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { selectedCategory = cat.nameBn }
              .testTag("category_select_${cat.id}"),
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) GreenPrimary else cat.bgColor,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = cat.emoji, fontSize = 18.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = cat.nameBn,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else Color(0xFF191C19)
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // "কার পিছনে খরচ" (For Whom)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "কার পিছনে খরচ:",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(memberOptions) { name ->
          val isSelected = forMember == name
          FilterChip(
            selected = isSelected,
            onClick = { forMember = name },
            label = { Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GreenPrimary,
              selectedLabelColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("for_member_$name")
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // "কে বাজার করলো / টাকা দিলো" (Shopper / Paid By)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "কে বাজার করলো / টাকা দিলো:",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(shopperOptions) { name ->
          val isSelected = bazarBy == name
          FilterChip(
            selected = isSelected,
            onClick = { bazarBy = name },
            label = { Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GreenPrimary,
              selectedLabelColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("shopper_$name")
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Payment Method Selector
      Text(
        text = "পেমেন্ট মাধ্যম",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(paymentMethods) { method ->
          val isSelected = selectedPaymentMethod == method
          FilterChip(
            selected = isSelected,
            onClick = { selectedPaymentMethod = method },
            label = { Text(method, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GreenPrimary,
              selectedLabelColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Note / Remarks Input
      OutlinedTextField(
        value = noteText,
        onValueChange = { noteText = it },
        label = { Text("নোট বা বিবরণ (ঐচ্ছিক)") },
        placeholder = { Text("যেমন: মাছ, আলু, চাল বা বিল নম্বর...") },
        leadingIcon = {
          Icon(Icons.Default.Notes, contentDescription = "নোট", tint = GreenPrimary)
        },
        singleLine = false,
        maxLines = 3,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_expense_note_input"),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Date Indicator
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.DateRange,
          contentDescription = "তারিখ",
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "তারিখ: ${BanglaUtils.formatTarikh(dateMillis, useBanglaDigits)}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Save Button
      val canSave = (takaText.toDoubleOrNull() ?: 0.0) > 0.0

      Button(
        onClick = {
          val taka = takaText.toDoubleOrNull() ?: 0.0
          if (taka > 0) {
            onSave(
              selectedCategory,
              taka,
              dateMillis,
              noteText.trim().ifBlank { null },
              selectedPaymentMethod,
              forMember,
              bazarBy
            )
          }
        },
        enabled = canSave,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("add_expense_save_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = GreenPrimary
        )
      ) {
        Text(
          text = if (expenseToEdit != null) "খরচ আপডেট করুন" else "খরচ সংরক্ষণ করুন",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
      }
    }
  }
}
