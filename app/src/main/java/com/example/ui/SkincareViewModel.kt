package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.SkincareCommerceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
  STOREFRONT,
  PRODUCT_DETAIL,
  SKIN_QUIZ,
  CART_CHECKOUT,
  ORDER_TRACKING,
  ADMIN_OPS,
  LOYALTY_ACCOUNT
}

class SkincareViewModel(
  val repository: SkincareCommerceRepository = SkincareCommerceRepository()
) : ViewModel() {

  private val _currentScreen = MutableStateFlow(AppScreen.STOREFRONT)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _selectedProduct = MutableStateFlow<SkincareProduct?>(null)
  val selectedProduct: StateFlow<SkincareProduct?> = _selectedProduct.asStateFlow()

  private val _selectedOrder = MutableStateFlow<Order?>(null)
  val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

  // Search and filter state
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow(SkincareCategory.ALL)
  val selectedCategory: StateFlow<SkincareCategory> = _selectedCategory.asStateFlow()

  private val _selectedSkinType = MutableStateFlow(SkinType.ALL)
  val selectedSkinType: StateFlow<SkinType> = _selectedSkinType.asStateFlow()

  private val _selectedSkinConcern = MutableStateFlow<SkinConcern?>(null)
  val selectedSkinConcern: StateFlow<SkinConcern?> = _selectedSkinConcern.asStateFlow()

  // Dialogs and sheets
  private val _showRoleSwitcher = MutableStateFlow(false)
  val showRoleSwitcher: StateFlow<Boolean> = _showRoleSwitcher.asStateFlow()

  private val _showInvoiceDialog = MutableStateFlow(false)
  val showInvoiceDialog: StateFlow<Boolean> = _showInvoiceDialog.asStateFlow()
  private val _invoiceOrder = MutableStateFlow<Order?>(null)
  val invoiceOrder: StateFlow<Order?> = _invoiceOrder.asStateFlow()

  private val _showReturnDialog = MutableStateFlow(false)
  val showReturnDialog: StateFlow<Boolean> = _showReturnDialog.asStateFlow()
  private val _returnTargetOrder = MutableStateFlow<Order?>(null)
  val returnTargetOrder: StateFlow<Order?> = _returnTargetOrder.asStateFlow()

  // Flash sale countdown timer (simulated live countdown: e.g. 04h 32m 19s)
  private val _flashSaleSeconds = MutableStateFlow(16345L)
  val flashSaleSeconds: StateFlow<Long> = _flashSaleSeconds.asStateFlow()

  // Delivery simulation progress (0f to 1f) for live map animation
  private val _deliverySimProgress = MutableStateFlow(0.65f)
  val deliverySimProgress: StateFlow<Float> = _deliverySimProgress.asStateFlow()

  init {
    viewModelScope.launch {
      while (true) {
        delay(1000L)
        if (_flashSaleSeconds.value > 0) {
          _flashSaleSeconds.value -= 1
        }
        // Animate courier vehicle along route
        val nextProg = _deliverySimProgress.value + 0.005f
        _deliverySimProgress.value = if (nextProg > 1f) 0.2f else nextProg
      }
    }
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun openProductDetail(product: SkincareProduct) {
    _selectedProduct.value = product
    _currentScreen.value = AppScreen.PRODUCT_DETAIL
  }

  fun openOrderTracking(order: Order) {
    _selectedOrder.value = order
    _currentScreen.value = AppScreen.ORDER_TRACKING
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setCategory(category: SkincareCategory) {
    _selectedCategory.value = category
  }

  fun setSkinType(skinType: SkinType) {
    _selectedSkinType.value = skinType
  }

  fun setSkinConcern(concern: SkinConcern?) {
    _selectedSkinConcern.value = concern
  }

  fun toggleRoleSwitcher(show: Boolean) {
    _showRoleSwitcher.value = show
  }

  fun openInvoice(order: Order) {
    _invoiceOrder.value = order
    _showInvoiceDialog.value = true
  }

  fun closeInvoice() {
    _showInvoiceDialog.value = false
    _invoiceOrder.value = null
  }

  fun openReturnDialog(order: Order) {
    _returnTargetOrder.value = order
    _showReturnDialog.value = true
  }

  fun closeReturnDialog() {
    _showReturnDialog.value = false
    _returnTargetOrder.value = null
  }
}
