package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MemberEntity
import com.example.ui.theme.GreenContainerLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryLight
import com.example.util.BanglaUtils

@Composable
fun MembersScreen(
  uiState: HisabUiState,
  onAddMember: (String) -> Unit,
  onDeleteMember: (MemberEntity) -> Unit,
  getMemberTotal: (String) -> Double,
  getShopperTotal: (String) -> Double,
  modifier: Modifier = Modifier
) {
  var newMemberName by remember { mutableStateOf("") }
  var memberToDelete by remember { mutableStateOf<MemberEntity?>(null) }
  val focusManager = LocalFocusManager.current

  // Delete Member Confirmation Dialog
  if (memberToDelete != null) {
    AlertDialog(
      onDismissRequest = { memberToDelete = null },
      title = { Text("সদস্য মুছে ফেলবেন?", fontWeight = FontWeight.Bold) },
      text = { Text("আপনি কি নিশ্চিত যে '${memberToDelete!!.name}' সদস্য তালিকা থেকে মুছে ফেলতে চান?") },
      confirmButton = {
        Button(
          onClick = {
            memberToDelete?.let { onDeleteMember(it) }
            memberToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("মুছে ফেলুন", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { memberToDelete = null }) {
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
    // Header
    item {
      Column {
        Text(
          text = "পরিবারের সদস্য ও খরচ বন্টন",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            color = GreenPrimary
          )
        )
        Text(
          text = "কাউকে উদ্দেশ্য করে খরচ ও কে বাজার করেছে তার হিসাব",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }

    // Add Member Input Box
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = newMemberName,
            onValueChange = { newMemberName = it },
            placeholder = { Text("নতুন সদস্যের নাম (যেমন: ভাই, বোন)") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
            },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("add_member_input"),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
              onDone = {
                if (newMemberName.isNotBlank()) {
                  onAddMember(newMemberName.trim())
                  newMemberName = ""
                  focusManager.clearFocus()
                }
              }
            )
          )

          Button(
            onClick = {
              if (newMemberName.isNotBlank()) {
                onAddMember(newMemberName.trim())
                newMemberName = ""
                focusManager.clearFocus()
              }
            },
            enabled = newMemberName.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            modifier = Modifier
              .height(52.dp)
              .testTag("add_member_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("যোগ")
          }
        }
      }
    }

    // Special item for "পরিবার (সামগ্রিক)"
    item {
      val familyTotal = getMemberTotal("পরিবার")
      val totalExpenses = uiState.totalExpense

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainerLight.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(GreenPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = Color.White)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "যৌথ / পুরো পরিবার",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                  )
                )
                Text(
                  text = "সবার জন্য সাধারণ সংসার খরচ",
                  style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                )
              }
            }

            Text(
              text = BanglaUtils.formatTaka(familyTotal, uiState.useBanglaDigits),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GreenPrimary
              )
            )
          }

          if (totalExpenses > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
              progress = { (familyTotal / totalExpenses).toFloat().coerceIn(0f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = GreenPrimary,
              trackColor = Color.White
            )
          }
        }
      }
    }

    // Section title
    item {
      Text(
        text = "সদস্য তালিকা (${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(uiState.allMembers.size) else uiState.allMembers.size} জন)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    // Members List
    items(uiState.allMembers, key = { it.id }) { member ->
      val memberForTotal = getMemberTotal(member.name)
      val memberShopperTotal = getShopperTotal(member.name)

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("member_card_${member.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "👤",
                  fontSize = 20.sp
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = member.name,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (member.name == uiState.managerName) {
                  Text(
                    text = "প্রধান ম্যানেজার",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = GreenPrimary,
                      fontWeight = FontWeight.SemiBold
                    )
                  )
                }
              }
            }

            // Total spent for this member
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = BanglaUtils.formatTaka(memberForTotal, uiState.useBanglaDigits),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenPrimary
                )
              )
              Text(
                text = "তার জন্য খরচ",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.outline
                )
              )
            }

            if (member.name != uiState.managerName) {
              IconButton(
                onClick = { memberToDelete = member },
                modifier = Modifier.padding(start = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Delete,
                  contentDescription = "সদস্য মুছুন",
                  tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Divider(color = MaterialTheme.colorScheme.surfaceVariant)

          Spacer(modifier = Modifier.height(8.dp))

          // Shopper summary row: কে কত টাকার বাজার/পেমেন্ট করেছে
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "বাজার / পরিশোধ করেছে:",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
            Text(
              text = BanglaUtils.formatTaka(memberShopperTotal, uiState.useBanglaDigits),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GreenPrimaryLight
              )
            )
          }
        }
      }
    }
  }
}
