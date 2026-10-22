package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.BanglaUtils

data class CalcKeyItem(
  val label: String,
  val sendValue: String,
  val bgColor: Color,
  val textColor: Color = Color.White
)

@Composable
fun CalculatorScreen(
  calcDisplay: String,
  calcHistory: String,
  useBanglaDigits: Boolean,
  onKeyClick: (String) -> Unit,
  onAddResultToExpense: () -> Unit,
  modifier: Modifier = Modifier
) {
  // iPhone style color tokens
  val bgScreen = Color(0xFF1A1A1A)
  val redC = Color(0xFFFF3B30)
  val opGreen = Color(0xFF2A9D5C)
  val eqGreen = Color(0xFF30D158)
  val numDark = Color(0xFF2C2C2E)
  val funcDark = Color(0xFF3A3A3C)

  // 5 rows x 4 columns keypad with Bangla digits and iPhone style
  val keyRows = listOf(
    listOf(
      CalcKeyItem(label = "C", sendValue = "C", bgColor = redC),
      CalcKeyItem(label = "⌫", sendValue = "⌫", bgColor = funcDark),
      CalcKeyItem(label = "%", sendValue = "%", bgColor = funcDark),
      CalcKeyItem(label = "÷", sendValue = "÷", bgColor = opGreen)
    ),
    listOf(
      CalcKeyItem(label = "৭", sendValue = "7", bgColor = numDark),
      CalcKeyItem(label = "৮", sendValue = "8", bgColor = numDark),
      CalcKeyItem(label = "৯", sendValue = "9", bgColor = numDark),
      CalcKeyItem(label = "×", sendValue = "×", bgColor = opGreen)
    ),
    listOf(
      CalcKeyItem(label = "৪", sendValue = "4", bgColor = numDark),
      CalcKeyItem(label = "৫", sendValue = "5", bgColor = numDark),
      CalcKeyItem(label = "৬", sendValue = "6", bgColor = numDark),
      CalcKeyItem(label = "−", sendValue = "−", bgColor = opGreen)
    ),
    listOf(
      CalcKeyItem(label = "১", sendValue = "1", bgColor = numDark),
      CalcKeyItem(label = "২", sendValue = "2", bgColor = numDark),
      CalcKeyItem(label = "৩", sendValue = "3", bgColor = numDark),
      CalcKeyItem(label = "+", sendValue = "+", bgColor = opGreen)
    ),
    listOf(
      CalcKeyItem(label = "০", sendValue = "0", bgColor = numDark),
      CalcKeyItem(label = "০০", sendValue = "00", bgColor = numDark),
      CalcKeyItem(label = ".", sendValue = ".", bgColor = numDark),
      CalcKeyItem(label = "=", sendValue = "=", bgColor = eqGreen)
    )
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(bgScreen)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .padding(bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "🧮 প্রিমিয়াম ক্যালকুলেটর",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        )
        Text(
          text = "হিসাব করুন এবং এক ক্লিকেই খরচে যোগ করুন",
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color.White.copy(alpha = 0.6f)
          )
        )
      }
    }

    // Display Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("calc_display_card"),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.End
      ) {
        // Calculation History Line in bright green (#30D158)
        val historyBangla = BanglaUtils.toBanglaDigits(calcHistory)
        Text(
          text = historyBangla.ifEmpty { " " },
          color = eqGreen,
          fontSize = 17.sp,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.Monospace,
          maxLines = 1,
          textAlign = TextAlign.End,
          modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .testTag("calc_history_text")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Main Display 5xl (~46sp)
        val displayBangla = BanglaUtils.toBanglaDigits(calcDisplay)
        Text(
          text = displayBangla,
          color = Color.White,
          fontSize = if (displayBangla.length > 7) 36.sp else 46.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          maxLines = 1,
          textAlign = TextAlign.End,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("calc_display_text")
        )
      }
    }

    // Keypad Rows with active:scale-95 animation
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      keyRows.forEach { row ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          row.forEach { item ->
            IPhoneCalcButton(
              item = item,
              onClick = { onKeyClick(item.sendValue) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Action button: Add this result to expense
    Button(
      onClick = onAddResultToExpense,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("calc_add_result_button"),
      shape = RoundedCornerShape(18.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = opGreen
      )
    ) {
      Icon(
        imageVector = Icons.Default.AddShoppingCart,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "এই রেজাল্ট খরচে যোগ করুন",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
    }
  }
}

@Composable
private fun IPhoneCalcButton(
  item: CalcKeyItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // active:scale-95 animation
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "button_scale_${item.sendValue}"
  )

  Surface(
    modifier = modifier
      .height(60.dp)
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .clip(RoundedCornerShape(18.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = ripple(color = Color.White.copy(alpha = 0.35f)),
        onClick = onClick
      )
      .testTag("calc_key_${item.sendValue}"),
    shape = RoundedCornerShape(18.dp),
    color = item.bgColor
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize()
    ) {
      Text(
        text = item.label,
        fontSize = if (item.label.length > 2) 20.sp else 24.sp,
        fontWeight = FontWeight.Bold,
        color = item.textColor
      )
    }
  }
}
