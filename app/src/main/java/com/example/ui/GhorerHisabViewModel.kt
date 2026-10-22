package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.KharchRepository
import com.example.model.Categories
import com.example.model.KharchEntity
import com.example.model.MemberEntity
import com.example.util.BanglaUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Calendar

data class HisabUiState(
  val isLoggedIn: Boolean = false,
  val managerName: String = "আবদুল্লাহ আল মামুন",
  val managerPhone: String = "",
  val monthlyBudget: Double = 20000.0,
  val useBanglaDigits: Boolean = true,
  val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
  val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
  val allExpenses: List<KharchEntity> = emptyList(),
  val filteredExpenses: List<KharchEntity> = emptyList(),
  val allMembers: List<MemberEntity> = emptyList(),
  val selectedCategoryFilter: String? = null,
  val searchQuery: String = "",
  val totalExpense: Double = 0.0,
  val monthlyTotal: Double = 0.0,
  val otherMonthsTotal: Double = 0.0,
  val remainingBudget: Double = 20000.0,
  val categoryBreakdown: Map<String, Double> = emptyMap(),
  val selectedTab: Int = 0, // 0 = Home, 1 = Calculator, 2 = Members, 3 = Settings, 4 = Analytics
  val showAddExpenseSheet: Boolean = false,
  val expenseToEdit: KharchEntity? = null,
  val initialTakaForAdd: String = "",
  val showProfileDialog: Boolean = false,
  val snackbarMessage: String? = null,
  // Calculator State
  val calcDisplay: String = "0",
  val calcHistory: String = ""
)

class GhorerHisabViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: KharchRepository
  private val _uiState = MutableStateFlow(HisabUiState())
  val uiState: StateFlow<HisabUiState> = _uiState.asStateFlow()

  // Internal calculator operand & operator tracking
  private var calcFirstOperand: Double? = null
  private var calcPendingOperator: String? = null
  private var calcResetNext: Boolean = false

  init {
    val database = AppDatabase.getDatabase(application, viewModelScope)
    repository = KharchRepository(database.kharchDao(), database.memberDao(), application)

    viewModelScope.launch {
      combine(
        repository.allExpenses,
        repository.allMembers,
        repository.isLoggedIn,
        repository.managerName,
        repository.monthlyBudget
      ) { expenses: List<KharchEntity>, members: List<MemberEntity>, loggedIn: Boolean, name: String, budget: Double ->
        _uiState.update { current ->
          computeState(
            expenses = expenses,
            members = members,
            loggedIn = loggedIn,
            name = name,
            budget = budget,
            useBangla = repository.useBanglaDigits.value,
            current = current
          )
        }
      }.collect()
    }
  }

  private fun computeState(
    expenses: List<KharchEntity>,
    members: List<MemberEntity>,
    loggedIn: Boolean,
    name: String,
    budget: Double,
    useBangla: Boolean,
    current: HisabUiState
  ): HisabUiState {
    val filtered = filterExpenses(
      list = expenses,
      month = current.selectedMonth,
      year = current.selectedYear,
      categoryFilter = current.selectedCategoryFilter,
      query = current.searchQuery
    )

    val currentMonthExpenses = expenses.filter { it.month == current.selectedMonth && it.year == current.selectedYear }
    val monthTotal = currentMonthExpenses.sumOf { it.taka }
    val otherTotal = expenses.filter { !(it.month == current.selectedMonth && it.year == current.selectedYear) }.sumOf { it.taka }
    val totalAll = expenses.sumOf { it.taka }
    val remaining = budget - monthTotal

    val breakdown = currentMonthExpenses.groupBy { it.category }
      .mapValues { entry -> entry.value.sumOf { it.taka } }

    return current.copy(
      isLoggedIn = loggedIn,
      managerName = name,
      managerPhone = repository.managerPhone.value,
      monthlyBudget = budget,
      useBanglaDigits = useBangla,
      allExpenses = expenses,
      filteredExpenses = filtered,
      allMembers = members,
      totalExpense = totalAll,
      monthlyTotal = monthTotal,
      otherMonthsTotal = otherTotal,
      remainingBudget = remaining,
      categoryBreakdown = breakdown
    )
  }

  private fun filterExpenses(
    list: List<KharchEntity>,
    month: Int,
    year: Int,
    categoryFilter: String?,
    query: String
  ): List<KharchEntity> {
    return list.filter { item ->
      val matchesMonthAndYear = item.month == month && item.year == year
      val matchesCategory = categoryFilter == null || item.category.equals(categoryFilter, ignoreCase = true)
      val matchesQuery = query.isBlank() ||
        item.category.contains(query, ignoreCase = true) ||
        (item.note != null && item.note.contains(query, ignoreCase = true)) ||
        item.member.contains(query, ignoreCase = true) ||
        item.bazarKoreche.contains(query, ignoreCase = true) ||
        item.taka.toInt().toString().contains(query)
      matchesMonthAndYear && matchesCategory && matchesQuery
    }
  }

  fun setTab(tabIndex: Int) {
    _uiState.update { it.copy(selectedTab = tabIndex) }
  }

  fun setSelectedMonth(month: Int) {
    _uiState.update { current ->
      val updated = current.copy(selectedMonth = month)
      computeState(
        expenses = current.allExpenses,
        members = current.allMembers,
        loggedIn = current.isLoggedIn,
        name = current.managerName,
        budget = current.monthlyBudget,
        useBangla = current.useBanglaDigits,
        current = updated
      )
    }
  }

  fun setSelectedYear(year: Int) {
    _uiState.update { current ->
      val updated = current.copy(selectedYear = year)
      computeState(
        expenses = current.allExpenses,
        members = current.allMembers,
        loggedIn = current.isLoggedIn,
        name = current.managerName,
        budget = current.monthlyBudget,
        useBangla = current.useBanglaDigits,
        current = updated
      )
    }
  }

  fun setCategoryFilter(category: String?) {
    _uiState.update { current ->
      val filtered = filterExpenses(
        list = current.allExpenses,
        month = current.selectedMonth,
        year = current.selectedYear,
        categoryFilter = category,
        query = current.searchQuery
      )
      current.copy(selectedCategoryFilter = category, filteredExpenses = filtered)
    }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { current ->
      val filtered = filterExpenses(
        list = current.allExpenses,
        month = current.selectedMonth,
        year = current.selectedYear,
        categoryFilter = current.selectedCategoryFilter,
        query = query
      )
      current.copy(searchQuery = query, filteredExpenses = filtered)
    }
  }

  fun login(phone: String, pass: String): Boolean {
    val normPhone = BanglaUtils.toEnglishDigits(phone.trim())
    val authenticatedName = repository.authenticate(normPhone, pass)
    return authenticatedName != null
  }

  fun register(name: String, phone: String, pass: String, confirmPass: String): String? {
    val normPhone = BanglaUtils.toEnglishDigits(phone.trim())
    if (name.isBlank()) {
      return "ম্যানেজার / গৃহকর্তার নাম দিন"
    }
    if (normPhone.length != 11 || !normPhone.all { it.isDigit() }) {
      return "ফোন নম্বর অবশ্যই ১১ ডিজিটের হতে হবে"
    }
    if (pass.length < 4) {
      return "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে"
    }
    if (pass != confirmPass) {
      return "পাসওয়ার্ড ও কনফার্ম পাসওয়ার্ড মেলেনি"
    }
    if (repository.checkUserExists(normPhone)) {
      return "এই ফোন নম্বরে ইতিমধ্যে অ্যাকাউন্ট তৈরি করা আছে"
    }

    repository.registerUser(normPhone, pass, name.trim())
    // Add manager to family members table
    addMember(name.trim())
    return null // Success
  }

  fun resetPassword(phone: String, newPass: String, confirmPass: String): String? {
    val normPhone = BanglaUtils.toEnglishDigits(phone.trim())
    if (normPhone.isBlank()) {
      return "ফোন নম্বর দিন"
    }
    if (!repository.checkUserExists(normPhone)) {
      return "এই ফোন নম্বরে কোনো অ্যাকাউন্ট নেই"
    }
    if (newPass.length < 4) {
      return "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে"
    }
    if (newPass != confirmPass) {
      return "পাসওয়ার্ড দুটি মেলেনি"
    }

    val success = repository.updatePassword(normPhone, newPass)
    return if (success) null else "পাসওয়ার্ড পরিবর্তন করতে সমস্যা হয়েছে"
  }

  fun logout() {
    repository.logout()
  }

  fun saveManagerName(newName: String) {
    if (newName.isBlank()) return
    repository.updateProfile(newName.trim(), _uiState.value.monthlyBudget, _uiState.value.useBanglaDigits)
    _uiState.update { it.copy(managerName = newName.trim()) }
    showSnackbar("ম্যানেজার পরিবর্তন হয়েছে")
  }

  fun openAddExpenseSheet(expense: KharchEntity? = null, initialTaka: String = "") {
    _uiState.update {
      it.copy(
        showAddExpenseSheet = true,
        expenseToEdit = expense,
        initialTakaForAdd = initialTaka
      )
    }
  }

  fun closeAddExpenseSheet() {
    _uiState.update {
      it.copy(
        showAddExpenseSheet = false,
        expenseToEdit = null,
        initialTakaForAdd = ""
      )
    }
  }

  fun saveExpense(
    category: String,
    taka: Double,
    dateMillis: Long,
    note: String?,
    paymentMethod: String,
    member: String,
    bazarKoreche: String
  ) {
    viewModelScope.launch {
      val tarikh = BanglaUtils.formatShortTarikh(dateMillis, _uiState.value.useBanglaDigits)
      val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
      val expMonth = cal.get(Calendar.MONTH)
      val expYear = cal.get(Calendar.YEAR)

      val currentEdit = _uiState.value.expenseToEdit

      if (currentEdit != null) {
        val updated = currentEdit.copy(
          category = category,
          taka = taka,
          dateMillis = dateMillis,
          tarikhDisplay = tarikh,
          note = note,
          paymentMethod = paymentMethod,
          member = member,
          bazarKoreche = bazarKoreche,
          month = expMonth,
          year = expYear
        )
        repository.update(updated)
        showSnackbar("খরচ সফলভাবে আপডেট করা হয়েছে")
      } else {
        val newExpense = KharchEntity(
          category = category,
          taka = taka,
          dateMillis = dateMillis,
          tarikhDisplay = tarikh,
          note = note,
          paymentMethod = paymentMethod,
          member = member,
          bazarKoreche = bazarKoreche,
          month = expMonth,
          year = expYear
        )
        repository.insert(newExpense)
        showSnackbar("নতুন খরচ যোগ করা হয়েছে")
      }
      closeAddExpenseSheet()
    }
  }

  fun deleteExpense(expense: KharchEntity) {
    viewModelScope.launch {
      repository.delete(expense)
      showSnackbar("খরচ মুছে ফেলা হয়েছে")
    }
  }

  // Member Management
  fun addMember(name: String) {
    if (name.isBlank()) return
    viewModelScope.launch {
      repository.insertMember(name.trim())
      showSnackbar("নতুন সদস্য '${name.trim()}' যোগ করা হয়েছে")
    }
  }

  fun deleteMember(member: MemberEntity) {
    viewModelScope.launch {
      repository.deleteMember(member)
      showSnackbar("সদস্য '${member.name}' মুছে ফেলা হয়েছে")
    }
  }

  fun getMemberTotal(memberName: String): Double {
    return _uiState.value.allExpenses
      .filter {
        it.member.equals(memberName, ignoreCase = true) &&
        it.month == _uiState.value.selectedMonth &&
        it.year == _uiState.value.selectedYear
      }
      .sumOf { it.taka }
  }

  fun getMemberAllTimeTotal(memberName: String): Double {
    return _uiState.value.allExpenses
      .filter { it.member.equals(memberName, ignoreCase = true) }
      .sumOf { it.taka }
  }

  fun getShopperTotal(shopperName: String): Double {
    return _uiState.value.allExpenses
      .filter {
        it.bazarKoreche.equals(shopperName, ignoreCase = true) &&
        it.month == _uiState.value.selectedMonth &&
        it.year == _uiState.value.selectedYear
      }
      .sumOf { it.taka }
  }

  // Calculator Logic
  fun handleCalc(rawKey: String) {
    // Normalize Bangla digits to English digits and operator symbols
    val key = when (val k = BanglaUtils.toEnglishDigits(rawKey)) {
      "÷" -> "/"
      "×" -> "*"
      "−" -> "-"
      else -> k
    }

    when (key) {
      "C" -> {
        calcFirstOperand = null
        calcPendingOperator = null
        calcResetNext = false
        _uiState.update { it.copy(calcDisplay = "0", calcHistory = "") }
      }
      "⌫", "BACKSPACE" -> {
        if (calcResetNext) {
          _uiState.update { it.copy(calcDisplay = "0") }
          calcResetNext = false
        } else {
          val current = _uiState.value.calcDisplay
          val updated = if (current.length > 1) current.dropLast(1) else "0"
          _uiState.update { it.copy(calcDisplay = updated) }
        }
      }
      "%" -> {
        val engDigits = BanglaUtils.toEnglishDigits(_uiState.value.calcDisplay)
        val currentVal = engDigits.toDoubleOrNull() ?: 0.0
        val percentVal = currentVal / 100.0
        _uiState.update { it.copy(calcDisplay = formatCalcResult(percentVal)) }
      }
      "+", "-", "*", "/" -> {
        val engDigits = BanglaUtils.toEnglishDigits(_uiState.value.calcDisplay)
        val currentVal = engDigits.toDoubleOrNull() ?: 0.0
        val displayOp = when (key) {
          "/" -> "÷"
          "*" -> "×"
          "-" -> "−"
          else -> "+"
        }

        if (calcFirstOperand != null && calcPendingOperator != null && !calcResetNext) {
          val result = evaluate(calcFirstOperand!!, currentVal, calcPendingOperator!!)
          calcFirstOperand = result
          _uiState.update {
            it.copy(
              calcDisplay = formatCalcResult(result),
              calcHistory = "${formatCalcResult(result)} $displayOp"
            )
          }
        } else {
          calcFirstOperand = currentVal
          _uiState.update {
            it.copy(calcHistory = "${formatCalcResult(currentVal)} $displayOp")
          }
        }
        calcPendingOperator = key
        calcResetNext = true
      }
      "=" -> {
        if (calcFirstOperand != null && calcPendingOperator != null) {
          val engDigits = BanglaUtils.toEnglishDigits(_uiState.value.calcDisplay)
          val secondOperand = engDigits.toDoubleOrNull() ?: 0.0
          val result = evaluate(calcFirstOperand!!, secondOperand, calcPendingOperator!!)
          val displayOp = when (calcPendingOperator) {
            "/" -> "÷"
            "*" -> "×"
            "-" -> "−"
            else -> "+"
          }
          val hist = "${formatCalcResult(calcFirstOperand!!)} $displayOp ${formatCalcResult(secondOperand)} ="
          calcFirstOperand = null
          calcPendingOperator = null
          calcResetNext = true
          _uiState.update {
            it.copy(calcDisplay = formatCalcResult(result), calcHistory = hist)
          }
        }
      }
      "." -> {
        if (calcResetNext) {
          _uiState.update { it.copy(calcDisplay = "0.") }
          calcResetNext = false
        } else if (!_uiState.value.calcDisplay.contains(".")) {
          _uiState.update { it.copy(calcDisplay = "${it.calcDisplay}.") }
        }
      }
      "00" -> {
        if (calcResetNext || _uiState.value.calcDisplay == "0") {
          _uiState.update { it.copy(calcDisplay = "0") }
          calcResetNext = false
        } else {
          _uiState.update { it.copy(calcDisplay = "${it.calcDisplay}00") }
        }
      }
      else -> { // Digits 0-9
        if (calcResetNext || _uiState.value.calcDisplay == "0") {
          _uiState.update { it.copy(calcDisplay = key) }
          calcResetNext = false
        } else {
          _uiState.update { it.copy(calcDisplay = "${it.calcDisplay}$key") }
        }
      }
    }
  }

  private fun evaluate(first: Double, second: Double, op: String): Double {
    return when (op) {
      "+" -> first + second
      "-" -> first - second
      "*" -> first * second
      "/" -> if (second != 0.0) first / second else 0.0
      else -> second
    }
  }

  private fun formatCalcResult(value: Double): String {
    return if (value % 1.0 == 0.0) {
      value.toLong().toString()
    } else {
      val df = DecimalFormat("#.##")
      df.format(value)
    }
  }

  fun applyCalcResultToExpense() {
    val amount = _uiState.value.calcDisplay
    val parsed = amount.toDoubleOrNull() ?: 0.0
    if (parsed > 0) {
      val cleanAmount = if (parsed % 1.0 == 0.0) parsed.toLong().toString() else parsed.toString()
      _uiState.update { it.copy(selectedTab = 0) }
      openAddExpenseSheet(initialTaka = cleanAmount)
    } else {
      showSnackbar("ক্যালকুলেটরে সঠিক টাকার পরিমাণ হিসাব করুন")
    }
  }

  fun updateProfile(name: String, budget: Double, useBangla: Boolean) {
    repository.updateProfile(name, budget, useBangla)
    _uiState.update { it.copy(useBanglaDigits = useBangla) }
    showSnackbar("প্রোফাইল তথ্য সংরক্ষিত হয়েছে")
  }

  fun toggleBanglaDigits() {
    val newSetting = !_uiState.value.useBanglaDigits
    repository.updateProfile(
      _uiState.value.managerName,
      _uiState.value.monthlyBudget,
      newSetting
    )
    _uiState.update { it.copy(useBanglaDigits = newSetting) }
  }

  fun showSnackbar(message: String) {
    _uiState.update { it.copy(snackbarMessage = message) }
  }

  fun clearSnackbar() {
    _uiState.update { it.copy(snackbarMessage = null) }
  }

  fun setProfileDialogVisible(visible: Boolean) {
    _uiState.update { it.copy(showProfileDialog = visible) }
  }
}
