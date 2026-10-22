package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.GreenContainerLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val viewModel: GhorerHisabViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        // Handle snackbar messages
        LaunchedEffect(uiState.snackbarMessage) {
          uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
          }
        }

        if (!uiState.isLoggedIn) {
          LoginScreen(
            onLogin = { phone, pass ->
              viewModel.login(phone, pass)
            },
            onRegister = { name, phone, pass, confirmPass ->
              viewModel.register(name, phone, pass, confirmPass)
            },
            onResetPassword = { phone, newPass, confirmPass ->
              viewModel.resetPassword(phone, newPass, confirmPass)
            }
          )
        } else {
          // Back handling
          BackHandler(
            enabled = uiState.selectedTab != 0 ||
              uiState.selectedCategoryFilter != null ||
              uiState.searchQuery.isNotEmpty()
          ) {
            if (uiState.selectedCategoryFilter != null || uiState.searchQuery.isNotEmpty()) {
              viewModel.setCategoryFilter(null)
              viewModel.setSearchQuery("")
            } else if (uiState.selectedTab != 0) {
              viewModel.setTab(0)
            }
          }

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
              NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                windowInsets = WindowInsets.navigationBars
              ) {
                // Tab 0: Home / Expenses
                NavigationBarItem(
                  selected = uiState.selectedTab == 0,
                  onClick = { viewModel.setTab(0) },
                  icon = {
                    Icon(
                      imageVector = if (uiState.selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                      contentDescription = "হোম"
                    )
                  },
                  label = {
                    Text(
                      text = "হোম",
                      fontSize = 11.sp,
                      fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GreenContainerLight,
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary
                  ),
                  modifier = Modifier.testTag("nav_tab_home")
                )

                // Tab 1: Calculator
                NavigationBarItem(
                  selected = uiState.selectedTab == 1,
                  onClick = { viewModel.setTab(1) },
                  icon = {
                    Icon(
                      imageVector = if (uiState.selectedTab == 1) Icons.Filled.Calculate else Icons.Outlined.Calculate,
                      contentDescription = "ক্যালকুলেটর"
                    )
                  },
                  label = {
                    Text(
                      text = "ক্যালকুলেটর",
                      fontSize = 11.sp,
                      fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GreenContainerLight,
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary
                  ),
                  modifier = Modifier.testTag("nav_tab_calc")
                )

                // Tab 2: Members
                NavigationBarItem(
                  selected = uiState.selectedTab == 2,
                  onClick = { viewModel.setTab(2) },
                  icon = {
                    Icon(
                      imageVector = if (uiState.selectedTab == 2) Icons.Filled.Group else Icons.Outlined.Group,
                      contentDescription = "সদস্য"
                    )
                  },
                  label = {
                    Text(
                      text = "সদস্য",
                      fontSize = 11.sp,
                      fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GreenContainerLight,
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary
                  ),
                  modifier = Modifier.testTag("nav_tab_members")
                )

                // Tab 3: Settings
                NavigationBarItem(
                  selected = uiState.selectedTab == 3,
                  onClick = { viewModel.setTab(3) },
                  icon = {
                    Icon(
                      imageVector = if (uiState.selectedTab == 3) Icons.Filled.Settings else Icons.Outlined.Settings,
                      contentDescription = "সেটিংস"
                    )
                  },
                  label = {
                    Text(
                      text = "সেটিংস",
                      fontSize = 11.sp,
                      fontWeight = if (uiState.selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GreenContainerLight,
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary
                  ),
                  modifier = Modifier.testTag("nav_tab_settings")
                )
              }
            },
            floatingActionButton = {
              if (uiState.selectedTab == 0) {
                ExtendedFloatingActionButton(
                  onClick = { viewModel.openAddExpenseSheet() },
                  containerColor = GreenPrimary,
                  contentColor = Color.White,
                  icon = {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "যোগ করুন"
                    )
                  },
                  text = {
                    Text(
                      text = "খরচ যোগ করুন",
                      fontWeight = FontWeight.Bold
                    )
                  },
                  modifier = Modifier
                    .testTag("add_expense_fab")
                    .windowInsetsPadding(WindowInsets.navigationBars)
                )
              }
            }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              AnimatedContent(
                targetState = uiState.selectedTab,
                label = "screen_transition"
              ) { tab ->
                when (tab) {
                  0 -> DashboardScreen(
                    uiState = uiState,
                    onCategorySelected = { viewModel.setCategoryFilter(it) },
                    onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                    onMonthSelected = { viewModel.setSelectedMonth(it) },
                    onYearSelected = { viewModel.setSelectedYear(it) },
                    onAddExpenseClicked = { viewModel.openAddExpenseSheet() },
                    onEditExpenseClicked = { viewModel.openAddExpenseSheet(it) },
                    onDeleteExpenseClicked = { viewModel.deleteExpense(it) },
                    onSettingsClicked = { viewModel.setTab(3) },
                    onToggleBanglaDigits = { viewModel.toggleBanglaDigits() }
                  )
                  1 -> CalculatorScreen(
                    calcDisplay = uiState.calcDisplay,
                    calcHistory = uiState.calcHistory,
                    useBanglaDigits = uiState.useBanglaDigits,
                    onKeyClick = { viewModel.handleCalc(it) },
                    onAddResultToExpense = { viewModel.applyCalcResultToExpense() }
                  )
                  2 -> MembersScreen(
                    uiState = uiState,
                    onAddMember = { viewModel.addMember(it) },
                    onDeleteMember = { viewModel.deleteMember(it) },
                    getMemberTotal = { viewModel.getMemberTotal(it) },
                    getShopperTotal = { viewModel.getShopperTotal(it) }
                  )
                  3 -> SettingsScreen(
                    uiState = uiState,
                    onSaveManager = { viewModel.saveManagerName(it) },
                    onUpdateBudget = { viewModel.updateProfile(uiState.managerName, it, uiState.useBanglaDigits) },
                    onToggleBanglaDigits = { viewModel.toggleBanglaDigits() },
                    onLogout = { viewModel.logout() }
                  )
                }
              }

              // Add / Edit Modal Sheet
              if (uiState.showAddExpenseSheet) {
                AddExpenseSheet(
                  expenseToEdit = uiState.expenseToEdit,
                  initialTaka = uiState.initialTakaForAdd,
                  members = uiState.allMembers,
                  useBanglaDigits = uiState.useBanglaDigits,
                  onDismiss = { viewModel.closeAddExpenseSheet() },
                  onSave = { cat, taka, dateMillis, note, payment, member, shopper ->
                    viewModel.saveExpense(cat, taka, dateMillis, note, payment, member, shopper)
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}
