package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Categories
import com.example.ui.theme.GreenContainerLight
import com.example.ui.theme.GreenPrimary
import com.example.util.BanglaUtils
import java.util.Calendar

@Composable
fun AnalyticsScreen(
  uiState: HisabUiState,
  modifier: Modifier = Modifier
) {
  val total = uiState.totalExpense
  val sortedCategories = uiState.categoryBreakdown.entries
    .sortedByDescending { it.value }

  val topCategory = sortedCategories.firstOrNull()

  // Calendar calculations for daily average
  val cal = Calendar.getInstance()
  val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
  val totalDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
  val remainingDays = (totalDaysInMonth - dayOfMonth + 1).coerceAtLeast(1)

  val dailyAverage = if (dayOfMonth > 0) total / dayOfMonth else 0.0
  val dailyRemainingAllowed = if (uiState.remainingBudget > 0 && remainingDays > 0) {
    uiState.remainingBudget / remainingDays
  } else 0.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Title
    item {
      Column {
        Text(
          text = "খরচের বিশ্লেষণ ও পরিসংখ্যান",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.ExtraBold,
            color = GreenPrimary
          )
        )
        Text(
          text = "${BanglaUtils.getCurrentMonthName()} মাসের সামগ্রিক খরচের চিত্র",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }

    // Chart Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("analytics_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "খাতভিত্তিক খরচের অনুপাত",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(20.dp))

          if (total <= 0.0 || sortedCategories.isEmpty()) {
            Box(
              modifier = Modifier
                .size(200.dp)
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "কোনো খরচের ডেটা নেই",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.outline
                )
              )
            }
          } else {
            // Donut Chart
            Box(
              modifier = Modifier.size(200.dp),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.size(190.dp)) {
                var currentAngle = -90f
                val strokeWidth = 36.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = Offset(size.width / 2, size.height / 2)
                val topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                val arcSize = Size(radius * 2, radius * 2)

                for (entry in sortedCategories) {
                  val cat = Categories.findByName(entry.key)
                  val sweepAngle = ((entry.value / total) * 360f).toFloat()

                  drawArc(
                    color = cat.fgColor,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                  )

                  currentAngle += sweepAngle
                }
              }

              // Center Total Label
              Column(
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "মোট",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.outline
                  )
                )
                Text(
                  text = BanglaUtils.formatTaka(total, uiState.useBanglaDigits),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenPrimary
                  )
                )
              }
            }
          }
        }
      }
    }

    // Top Expense Highlight Banner
    if (topCategory != null) {
      item {
        val topCatInfo = Categories.findByName(topCategory.key)
        val percentage = (topCategory.value / total * 100).toInt()

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = topCatInfo.bgColor)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White),
              contentAlignment = Alignment.Center
            ) {
              Text(topCatInfo.emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "সর্বোচ্চ খরচের খাত",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = topCatInfo.fgColor,
                  fontWeight = FontWeight.SemiBold
                )
              )
              Text(
                text = "${topCatInfo.nameBn} (${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(percentage) else percentage}%)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF191C19)
                )
              )
            }

            Text(
              text = BanglaUtils.formatTaka(topCategory.value, uiState.useBanglaDigits),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = topCatInfo.fgColor
              )
            )
          }
        }
      }
    }

    // Insights Mini Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Daily average
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "দৈনিক গড় খরচ",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = BanglaUtils.formatTaka(dailyAverage, uiState.useBanglaDigits),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GreenPrimary)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(dayOfMonth) else dayOfMonth} দিন হিসেবে",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            )
          }
        }

        // Daily Remaining Allowed
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "দৈনিক খরচের লক্ষ্য",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = BanglaUtils.formatTaka(dailyRemainingAllowed, uiState.useBanglaDigits),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (uiState.remainingBudget >= 0) GreenPrimary else MaterialTheme.colorScheme.error
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "বাকি ${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(remainingDays) else remainingDays} দিন",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            )
          }
        }
      }
    }

    // Category List Breakdown
    item {
      Text(
        text = "সব খাতের বিস্তারিত হিসাব",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    items(sortedCategories) { (catName, amount) ->
      val catInfo = Categories.findByName(catName)
      val percent = if (total > 0) (amount / total * 100).toInt() else 0
      val count = uiState.allExpenses.count { it.category.equals(catName, ignoreCase = true) }

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(catInfo.bgColor),
                contentAlignment = Alignment.Center
              ) {
                Text(catInfo.emoji, fontSize = 18.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = catName,
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(count) else count} টি ভাউচার/এন্ট্রি",
                  style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = BanglaUtils.formatTaka(amount, uiState.useBanglaDigits),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = GreenPrimary
                )
              )
              Text(
                text = "${if (uiState.useBanglaDigits) BanglaUtils.toBanglaDigits(percent) else percent}%",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = catInfo.fgColor,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Progress bar for this category
          LinearProgressIndicator(
            progress = { if (total > 0) (amount / total).toFloat() else 0f },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = catInfo.fgColor,
            trackColor = catInfo.bgColor
          )
        }
      }
    }
  }
}
