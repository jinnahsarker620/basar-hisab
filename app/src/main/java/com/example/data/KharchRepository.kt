package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.KharchEntity
import com.example.model.MemberEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KharchRepository(
  private val kharchDao: KharchDao,
  private val memberDao: MemberDao,
  context: Context
) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("ghorer_hisab_prefs", Context.MODE_PRIVATE)

  init {
    // Seed default demo user if not present
    val defaultPhone = "01712345678"
    if (!prefs.contains(KEY_USER_PREFIX + defaultPhone + "_pass")) {
      prefs.edit()
        .putString(KEY_USER_PREFIX + defaultPhone + "_name", "আবদুল্লাহ আল মামুন")
        .putString(KEY_USER_PREFIX + defaultPhone + "_pass", "1234")
        .apply()
    }
  }

  val allExpenses: Flow<List<KharchEntity>> = kharchDao.getAllExpenses()
  val totalExpense: Flow<Double?> = kharchDao.getTotalExpense()
  val allMembers: Flow<List<MemberEntity>> = memberDao.getAllMembers()

  private val _managerName = MutableStateFlow(
    prefs.getString(KEY_MANAGER_NAME, "আবদুল্লাহ আল মামুন") ?: "আবদুল্লাহ আল মামুন"
  )
  val managerName: StateFlow<String> = _managerName.asStateFlow()

  private val _managerPhone = MutableStateFlow(
    prefs.getString(KEY_MANAGER_PHONE, "01712345678") ?: "01712345678"
  )
  val managerPhone: StateFlow<String> = _managerPhone.asStateFlow()

  private val _isLoggedIn = MutableStateFlow(
    prefs.getBoolean(KEY_IS_LOGGED_IN, false)
  )
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _monthlyBudget = MutableStateFlow(
    prefs.getFloat(KEY_MONTHLY_BUDGET, 20000f).toDouble()
  )
  val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()

  private val _useBanglaDigits = MutableStateFlow(
    prefs.getBoolean(KEY_USE_BANGLA_DIGITS, true)
  )
  val useBanglaDigits: StateFlow<Boolean> = _useBanglaDigits.asStateFlow()

  suspend fun insert(kharch: KharchEntity): Long = kharchDao.insertExpense(kharch)

  suspend fun update(kharch: KharchEntity) = kharchDao.updateExpense(kharch)

  suspend fun delete(kharch: KharchEntity) = kharchDao.deleteExpense(kharch)

  suspend fun deleteById(id: Long) = kharchDao.deleteById(id)

  suspend fun insertMember(name: String): Long {
    return memberDao.insertMember(MemberEntity(name = name))
  }

  suspend fun deleteMember(member: MemberEntity) {
    memberDao.deleteMember(member)
  }

  suspend fun deleteMemberById(id: Long) {
    memberDao.deleteById(id)
  }

  fun checkUserExists(phone: String): Boolean {
    return prefs.contains(KEY_USER_PREFIX + phone + "_pass")
  }

  fun registerUser(phone: String, pass: String, name: String) {
    prefs.edit()
      .putString(KEY_USER_PREFIX + phone + "_name", name)
      .putString(KEY_USER_PREFIX + phone + "_pass", pass)
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .putString(KEY_MANAGER_PHONE, phone)
      .putString(KEY_MANAGER_NAME, name)
      .apply()

    _isLoggedIn.value = true
    _managerPhone.value = phone
    _managerName.value = name
  }

  fun authenticate(phone: String, pass: String): String? {
    val savedPass = prefs.getString(KEY_USER_PREFIX + phone + "_pass", null)
    if (savedPass != null && savedPass == pass) {
      val name = prefs.getString(KEY_USER_PREFIX + phone + "_name", "আবদুল্লাহ আল মামুন") ?: "আবদুল্লাহ আল মামুন"
      prefs.edit()
        .putBoolean(KEY_IS_LOGGED_IN, true)
        .putString(KEY_MANAGER_PHONE, phone)
        .putString(KEY_MANAGER_NAME, name)
        .apply()

      _isLoggedIn.value = true
      _managerPhone.value = phone
      _managerName.value = name
      return name
    }
    return null
  }

  fun updatePassword(phone: String, newPass: String): Boolean {
    if (!checkUserExists(phone)) return false

    prefs.edit()
      .putString(KEY_USER_PREFIX + phone + "_pass", newPass)
      .apply()
    return true
  }

  fun logout() {
    prefs.edit()
      .putBoolean(KEY_IS_LOGGED_IN, false)
      .apply()
    _isLoggedIn.value = false
  }

  fun updateProfile(name: String, budget: Double, useBangla: Boolean) {
    val phone = _managerPhone.value
    prefs.edit()
      .putString(KEY_MANAGER_NAME, name)
      .putString(KEY_USER_PREFIX + phone + "_name", name)
      .putFloat(KEY_MONTHLY_BUDGET, budget.toFloat())
      .putBoolean(KEY_USE_BANGLA_DIGITS, useBangla)
      .apply()

    _managerName.value = name
    _monthlyBudget.value = budget
    _useBanglaDigits.value = useBangla
  }

  companion object {
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_MANAGER_NAME = "manager_name"
    private const val KEY_MANAGER_PHONE = "manager_phone"
    private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    private const val KEY_USE_BANGLA_DIGITS = "use_bangla_digits"
    private const val KEY_USER_PREFIX = "user_"
  }
}
