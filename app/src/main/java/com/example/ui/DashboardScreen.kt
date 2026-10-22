package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Categories
import com.example.model.KharchEntity
import com.example.ui.theme.GreenContainerLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryLight
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  uiState: HisabUiState,
  onCategorySelected: (String?) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onMonthSelected: (Int) -> Unit,
  onYearSelected: (Int) -> Unit,
  onAddExpenseClicked: () -> Unit,
  onEditExpenseClicked: (KharchEntity) -> Unit,
  onDeleteExpenseClicked: (KharchEntity) -> Unit,
  onSettingsClicked: () -> Unit,
  onToggleBanglaDigits: () -> Unit,
  modifier: Modifier = Modifier
) {
  var expenseToDelete by remember { mutableStateOf<KharchEntity?>(null) }
  var monthDropdownExpanded by remember { mutableStateOf(false) }
  var yearDropdownExpanded by remember { mutableStateOf(false) }

  val years = listOf(2024, 2025, 2026, 2027, 2028, 2029, 2030)

  // Delete confirmation dialog
  if (expenseToDelete != null) {
    AlertDialog(
      onDismissRequest = { expenseToDelete = null },
      title = { Text("খরচ মুছে ফেলবেন?", fontWeight = FontWeight.Bold) },
      text = {
        Text(
          "আপনি কি নিশ্চিত যে '${expenseToDelete!!.category}' খাতের ${BanglaUtils.formatTaka(expenseToDelete!!.taka, uiState.useBanglaDigits)} টাকার এন্ট্রি মুছে ফেলতে চান?"
        )
      },
      confirmButton = {
        Button(
          onClick = {
            expenseToDelete?.let { onDeleteExpenseClicked(it) }
            expenseToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("মুছে ফেলুন", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { expenseToDelete = null }) {
          Text("বাতিল")
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar / Manager Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSettingsClicked() }
            .padding(end = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(GreenPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "🏠",
              fontSize = 22.sp
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = uiState.managerName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "ম্যানেজার: ${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.allExpenses.size) else uiState.allExpenses.size} টি মোট খরচ",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Bangla digits toggle chip
          FilterChip(
            selected = uiState.useBanglaDigits,
            onClick = onToggleBanglaDigits,
            label = {
              Text(
                if (uiState.useBanglaDigits) "বাংলা" else "ENG",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GreenContainerLight,
              selectedLabelColor = GreenPrimary
            ),
            modifier = Modifier.testTag("bangla_digits_toggle")
          )

          IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier.testTag("settings_button")
          ) {
            Icon(
              imageVector = Icons.Outlined.Settings,
              contentDescription = "সেটিংস",
              tint = GreenPrimary
            )
          }
        }
      }
    }

    // Month & Year Selector Controls
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Month Picker Box
        Box(modifier = Modifier.weight(1.5f)) {
          OutlinedCard(
            onClick = { monthDropdownExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = BanglaUtils.getMonthName(uiState.selectedMonth),
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
              Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GreenPrimary)
            }
          }

          DropdownMenu(
            expanded = monthDropdownExpanded,
            onDismissRequest = { monthDropdownExpanded = false }
          ) {
            BanglaUtils.MONTHS_LIST.forEachIndexed { index, monthName ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = monthName,
                    fontWeight = if (uiState.selectedMonth == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (uiState.selectedMonth == index) GreenPrimary else Color.Unspecified
                  )
                },
                onClick = {
                  onMonthSelected(index)
                  monthDropdownExpanded = false
                }
              )
            }
          }
        }

        // Year Picker Box
        Box(modifier = Modifier.weight(1f)) {
          OutlinedCard(
            onClick = { yearDropdownExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = BanglaUtils.toBanglaDigits(uiState.selectedYear),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GreenPrimary)
            }
          }

          DropdownMenu(
            expanded = yearDropdownExpanded,
            onDismissRequest = { yearDropdownExpanded = false }
          ) {
            years.forEach { yr ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = BanglaUtils.toBanglaDigits(yr),
                    fontWeight = if (uiState.selectedYear == yr) FontWeight.Bold else FontWeight.Normal,
                    color = if (uiState.selectedYear == yr) GreenPrimary else Color.Unspecified
                  )
                },
                onClick = {
                  onYearSelected(yr)
                  yearDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    }

    // Hero Total Expense Card for Selected Month
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("monthly_expense_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(
                  GreenPrimary,
                  GreenPrimaryLight
                )
              )
            )
            .padding(22.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${BanglaUtils.getMonthName(uiState.selectedMonth)} ${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.selectedYear) else uiState.selectedYear} এর মোট খরচ",
                style = MaterialTheme.typography.bodyLarge.copy(
                  color = Color.White.copy(alpha = 0.9f),
                  fontWeight = FontWeight.Medium
                )
              )
              Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(
                  text = "${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.filteredExpenses.size) else uiState.filteredExpenses.size} টি এন্ট্রি",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Monthly Total Display
            Text(
              text = BanglaUtils.formatTaka(uiState.monthlyTotal, uiState.useBanglaDigits),
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 34.sp
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "অন্যান্য মাসে মোট ছিল: ${BanglaUtils.formatTaka(uiState.otherMonthsTotal, uiState.useBanglaDigits)}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Budget Progress
            val progress = if (uiState.monthlyBudget > 0) {
              (uiState.monthlyTotal / uiState.monthlyBudget).toFloat().coerceIn(0f, 1f)
            } else 0f

            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "বাজেট: ${BanglaUtils.formatTaka(uiState.monthlyBudget, uiState.useBanglaDigits)}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.85f)
                  )
                )
                Text(
                  text = if (uiState.remainingBudget >= 0) {
                    "অবশিষ্ট: ${BanglaUtils.formatTaka(uiState.remainingBudget, uiState.useBanglaDigits)}"
                  } else {
                    "অতিরিক্ত: ${BanglaUtils.formatTaka(-uiState.remainingBudget, uiState.useBanglaDigits)}"
                  },
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = if (uiState.remainingBudget >= 0) Color.White else Color(0xFFFFCDD2),
                    fontWeight = FontWeight.Bold
                  )
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = when {
                  progress > 0.9f -> Color(0xFFFF5252)
                  progress > 0.75f -> Color(0xFFFFD740)
                  else -> Color(0xFFB9F6CA)
                },
                trackColor = Color.White.copy(alpha = 0.25f)
              )
            }
          }
        }
      }
    }

    // Category Filter Horizontal Scroll
    item {
      Column {
        Text(
          text = "ক্যাটাগরি অনুযায়ী ফিল্টার",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          item {
            FilterChip(
              selected = uiState.selectedCategoryFilter == null,
              onClick = { onCategorySelected(null) },
              label = { Text("সব (${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.filteredExpenses.size) else uiState.filteredExpenses.size})") },
              leadingIcon = {
                Text("✨", fontSize = 14.sp)
              },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenPrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("filter_chip_all")
            )
          }

          items(Categories.ALL) { category ->
            val count = uiState.filteredExpenses.count { it.category.equals(category.nameBn, ignoreCase = true) }
            val isSelected = uiState.selectedCategoryFilter.equals(category.nameBn, ignoreCase = true)

            FilterChip(
              selected = isSelected,
              onClick = {
                onCategorySelected(if (isSelected) null else category.nameBn)
              },
              label = {
                Text(
                  text = "${category.nameBn} (${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(count) else count})"
                )
              },
              leadingIcon = {
                Text(category.emoji, fontSize = 14.sp)
              },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenPrimary,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
              ),
              modifier = Modifier.testTag("filter_chip_${category.id}")
            )
          }
        }
      }
    }

    // Search Field
    item {
      OutlinedTextField(
        value = uiState.searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = { Text("খরচের নোট, সদস্য বা বিবরণ খুঁজুন...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "সার্চ আইকন",
            tint = GreenPrimary
          )
        },
        trailingIcon = {
          if (uiState.searchQuery.isNotBlank()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "মুছে ফেলুন"
              )
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_input"),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GreenPrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
      )
    }

    // Section Title
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (uiState.selectedCategoryFilter != null) {
            "${uiState.selectedCategoryFilter} (${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.filteredExpenses.size) else uiState.filteredExpenses.size})"
          } else {
            "খরচের তালিকা (${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.filteredExpenses.size) else uiState.filteredExpenses.size})"
          },
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
          )
        )

        val filteredTotal = uiState.filteredExpenses.sumOf { it.taka }
        Text(
          text = BanglaUtils.formatTaka(filteredTotal, uiState.useBanglaDigits),
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
          )
        )
      }
    }

    // Empty State for Selected Month
    if (uiState.filteredExpenses.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("🗓️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "এই মাসে কোনো খরচ নেই",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "${BanglaUtils.getMonthName(uiState.selectedMonth)} মাসের খরচের হিসাব লিখতে নিচের বাটনে চাপ দিন",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = onAddExpenseClicked,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("নতুন খরচ যোগ করুন")
            }
          }
        }
      }
    } else {
      // Expense Items
      items(
        items = uiState.filteredExpenses,
        key = { it.id }
      ) { expense ->
        val categoryInfo = Categories.findByName(expense.category)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_card_${expense.id}"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Category Icon Badge
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(categoryInfo.bgColor),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = categoryInfo.emoji,
                fontSize = 22.sp
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(
              modifier = Modifier.weight(1f)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = expense.category,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                  )
                )

                // Payment Method Tag
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GreenContainerLight.copy(alpha = 0.7f)
                ) {
                  Text(
                    text = expense.paymentMethod,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = GreenPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              if (!expense.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = expense.note,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = "কার জন্য: ${expense.member} | বাজার: ${expense.bazarKoreche}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = expense.tarikhDisplay.ifBlank {
                  BanglaUtils.formatShortTarikh(expense.dateMillis, uiState.useBanglaDigits)
                },
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.outline
                )
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Taka amount & actions
            Column(
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = BanglaUtils.formatTaka(expense.taka, uiState.useBanglaDigits),
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenPrimary
                )
              )

              Row(
                horizontalArrangement = Arrangement.End
              ) {
                IconButton(
                  onClick = { onEditExpenseClicked(expense) },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "সম্পাদনা",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { expenseToDelete = expense },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "মুছুন",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
