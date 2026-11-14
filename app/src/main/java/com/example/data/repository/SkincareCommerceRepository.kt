package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SkincareCommerceRepository {

  private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  private val _products = MutableStateFlow<List<SkincareProduct>>(emptyList())
  val products: StateFlow<List<SkincareProduct>> = _products.asStateFlow()

  private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
  val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

  private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
  val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

  private val _orders = MutableStateFlow<List<Order>>(emptyList())
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  private val _returnRequests = MutableStateFlow<List<ReturnRequest>>(emptyList())
  val returnRequests: StateFlow<List<ReturnRequest>> = _returnRequests.asStateFlow()

  private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
  val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

  private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
  val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

  private val _loyaltyPoints = MutableStateFlow(1420)
  val loyaltyPoints: StateFlow<Int> = _loyaltyPoints.asStateFlow()

  private val _savedAddresses = MutableStateFlow<List<Address>>(emptyList())
  val savedAddresses: StateFlow<List<Address>> = _savedAddresses.asStateFlow()

  private val _selectedAddress = MutableStateFlow<Address?>(null)
  val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

  private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
  val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

  val availableCoupons = listOf(
    Coupon("GLOW20", 20, 999.0, 500.0, "20% off on all clinical serums above ₹999"),
    Coupon("FREESHIP", 100, 499.0, 99.0, "Free Express Delivery on orders above ₹499"),
    Coupon("SKINGLOW", 15, 799.0, 300.0, "15% off instant dermatologist welcome discount")
  )

  init {
    seedInitialData()
  }

  private fun seedInitialData() {
    val initialAddresses = listOf(
      Address(
        id = "ADDR-1",
        fullName = "Aditi Sharma",
        phone = "+91 98201 44521",
        street = "Flat 402, Royal Palms, Bandra West",
        landmark = "Near Olive Bistro",
        city = "Mumbai",
        state = "Maharashtra",
        pincode = "400050",
        isServiceable = true,
        deliveryInstructions = "Call before delivery. Leave with security guard if unavailable."
      ),
      Address(
        id = "ADDR-2",
        fullName = "Aditi Sharma",
        phone = "+91 98201 44521",
        street = "DLF CyberCity, Tower B, 7th Floor",
        landmark = "Opposite Gate 3",
        city = "Gurugram",
        state = "Haryana",
        pincode = "122002",
        isServiceable = true,
        deliveryInstructions = "Corporate reception delivery 10 AM - 6 PM only."
      )
    )
    _savedAddresses.value = initialAddresses
    _selectedAddress.value = initialAddresses.first()

    val initialProducts = listOf(
      SkincareProduct(
        id = "PROD-001",
        title = "Radiance Boost 10% Niacinamide Serum",
        brand = "Luminia Laboratories",
        category = SkincareCategory.SERUMS,
        skinTypes = listOf(SkinType.OILY, SkinType.COMBINATION, SkinType.SENSITIVE, SkinType.NORMAL),
        skinConcerns = listOf(SkinConcern.ACNE, SkinConcern.HYPERPIGMENTATION, SkinConcern.DULLNESS),
        price = 699.0,
        originalPrice = 999.0,
        rating = 4.8,
        reviewCount = 1420,
        volume = "30 ml / 1.0 fl. oz.",
        dermatologyIngredients = listOf(
          IngredientDetail("Niacinamide (Vitamin B3)", "10.0%", "Inhibits melanin transfer and tightens enlarged pores"),
          IngredientDetail("Zinc PCA", "1.0%", "Balances sebum secretion and prevents bacterial breakout"),
          IngredientDetail("Centella Asiatica", "2.0%", "Calms skin redness and reinforces barrier recovery")
        ),
        fullIngredientsText = "Aqua, Niacinamide, Zinc PCA, Centella Asiatica Leaf Extract, Glycerin, Ethoxydiglycol, Hydroxyethylcellulose, Phenoxyethanol, Ethylhexylglycerin.",
        usageInstructions = "Apply 2-3 drops to clean skin morning and evening before heavier creams. Follow with broad-spectrum SPF in the morning.",
        patchTestWarning = "Dermatologically tested. Apply a small coin-sized droplet to inner wrist or jawline for 24 hours prior to full facial use.",
        batchInfo = BatchInfo(
          batchNumber = "BATCH-LUM-2026-X09",
          mfgDate = "02/2026",
          expiryDate = "02/2028",
          shelfLifeMonths = 24,
          isNearExpiry = false,
          autoDiscountPercent = 0
        ),
        warehouseStock = mapOf(
          "WH-Mumbai Central" to 85,
          "WH-Delhi NCR" to 140,
          "WH-Bengaluru South" to 110
        ),
        variants = listOf(
          ProductVariant("V1", "Standard 30ml Dropper", 30, 1.0),
          ProductVariant("V2", "Jumbo Value 50ml", 50, 1.45),
          ProductVariant("V3", "Duo Pack (2 x 30ml)", 60, 1.80)
        ),
        description = "High-strength clinical vitamin and mineral blemish formula designed to refine texture and fade post-acne marks without irritation.",
        isBestSeller = true
      ),
      SkincareProduct(
        id = "PROD-002",
        title = "DermaShield Ultra-Light Invisible Sunscreen Fluid SPF 50+ PA++++",
        brand = "DermaShield Clinical",
        category = SkincareCategory.SUNSCREENS,
        skinTypes = listOf(SkinType.ALL, SkinType.SENSITIVE, SkinType.OILY),
        skinConcerns = listOf(SkinConcern.SUN_PROTECTION, SkinConcern.AGING, SkinConcern.BARRIER_REPAIR),
        price = 549.0,
        originalPrice = 750.0,
        rating = 4.9,
        reviewCount = 2890,
        volume = "50 ml / 1.7 fl. oz.",
        dermatologyIngredients = listOf(
          IngredientDetail("Tinosorb M + S Hybrid Filters", "8.5%", "Broad spectrum UVA/UVB photo-stable defense"),
          IngredientDetail("Hyaluronic Acid (Triple MW)", "1.5%", "Deep hydration without greasiness"),
          IngredientDetail("Cica Extract", "2.0%", "Prevents heat-induced redness and calms irritation")
        ),
        fullIngredientsText = "Water, Cyclopentasiloxane, Ethylhexyl Methoxycinnamate, Methylene Bis-Benzotriazolyl Tetramethylbutylphenol, Sodium Hyaluronate, Centella Extract, Silica, Tocopherol.",
        usageInstructions = "Dispense 2 finger lengths generously across face, neck, and ears 15 minutes before sun exposure. Reapply every 2-3 hours.",
        patchTestWarning = "Hypoallergenic, non-comedogenic, fragrance-free. Suitable for hyper-reactive and post-procedure skin.",
        batchInfo = BatchInfo(
          batchNumber = "BATCH-DS-2026-S12",
          mfgDate = "01/2026",
          expiryDate = "01/2028",
          shelfLifeMonths = 24,
          isNearExpiry = false,
          autoDiscountPercent = 0
        ),
        warehouseStock = mapOf(
          "WH-Mumbai Central" to 220,
          "WH-Delhi NCR" to 180,
          "WH-Bengaluru South" to 140
        ),
        variants = listOf(
          ProductVariant("V1", "Bottle 50ml", 50, 1.0),
          ProductVariant("V2", "Double Pack (2 x 50ml)", 100, 1.85)
        ),
        description = "Weightless non-greasy matte finish sunscreen that leaves zero white cast on all skin phototypes and layers seamlessly under makeup.",
        isBestSeller = true
      ),
      SkincareProduct(
        id = "PROD-003",
        title = "Botanical Hydration Velvet Barrier Cream with 5 Essential Ceramides",
        brand = "Auraea Botanical Essence",
        category = SkincareCategory.MOISTURIZERS,
        skinTypes = listOf(SkinType.DRY, SkinType.SENSITIVE, SkinType.NORMAL),
        skinConcerns = listOf(SkinConcern.BARRIER_REPAIR, SkinConcern.AGING, SkinConcern.DULLNESS),
        price = 849.0,
        originalPrice = 1199.0,
        rating = 4.7,
        reviewCount = 980,
        volume = "50 g / 1.76 oz.",
        dermatologyIngredients = listOf(
          IngredientDetail("5 Ceramides (NP, AP, EOP, AS, NS)", "3.0%", "Reconstructs stratum corneum lipid matrix"),
          IngredientDetail("Oat Beta-Glucan", "2.0%", "Instant relief from tightness, flaking, and itching"),
          IngredientDetail("Plant Squalane", "4.0%", "Bio-mimetic emollient lock-in moisture")
        ),
        fullIngredientsText = "Aqua, Caprylic/Capric Triglyceride, Squalane, Ceramide NP, Ceramide AP, Ceramide EOP, Phytosphingosine, Cholesterol, Avena Sativa Kernel Extract, Allantoin.",
        usageInstructions = "Warm a pea-sized amount between fingertips and gently press into face and neck. Ideal as PM sealing step.",
        patchTestWarning = "Formulated at healthy skin barrier pH 5.5. Free of parabens, essential oils, and synthetic colorants.",
        batchInfo = BatchInfo(
          batchNumber = "BATCH-AUR-2026-C04",
          mfgDate = "03/2026",
          expiryDate = "03/2028",
          shelfLifeMonths = 24,
          isNearExpiry = false,
          autoDiscountPercent = 0
        ),
        warehouseStock = mapOf(
          "WH-Mumbai Central" to 42,
          "WH-Delhi NCR" to 60,
          "WH-Bengaluru South" to 38
        ),
        variants = listOf(
          ProductVariant("V1", "Glass Jar 50g", 50, 1.0),
          ProductVariant("V2", "Jumbo Jar 100g", 100, 1.70)
        ),
        description = "Intensive lipid replenishment moisturizer developed for damaged skin barriers, retinoid acclimatization, and extreme dryness.",
        isBestSeller = false
      ),
      SkincareProduct(
        id = "PROD-004",
        title = "0.3% Micro-Encapsulated Retinol + Multi-Peptides Night Elixir",
        brand = "Luminia Laboratories",
        category = SkincareCategory.RETINOLS,
        skinTypes = listOf(SkinType.COMBINATION, SkinType.DRY, SkinType.NORMAL),
        skinConcerns = listOf(SkinConcern.AGING, SkinConcern.HYPERPIGMENTATION, SkinConcern.DULLNESS),
        price = 1099.0,
        originalPrice = 1499.0,
        rating = 4.6,
        reviewCount = 670,
        volume = "30 ml / 1.0 fl. oz.",
        dermatologyIngredients = listOf(
          IngredientDetail("Encapsulated Pure Retinol", "0.3%", "Time-released cellular renewal for minimal irritation"),
          IngredientDetail("Matrixyl 3000 Peptides", "3.0%", "Stimulates collagen I & IV synthesis for firm bounce"),
          IngredientDetail("Bakuchiol Plant Alternative", "1.0%", "Synergistic antioxidant retinoid booster")
        ),
        fullIngredientsText = "Water, Glycerin, Caprylic Triglyceride, Retinol, Bakuchiol, Palmitoyl Tripeptide-1, Palmitoyl Tetrapeptide-7, Phospholipids, Butylene Glycol.",
        usageInstructions = "Introduce slowly: 2 nights per week for first fortnight, increasing to alternate nights. Use exclusively in evening on dry skin.",
        patchTestWarning = "Contraindicated during pregnancy and lactation. Must use high SPF 50 during daytime while using this product.",
        batchInfo = BatchInfo(
          batchNumber = "BATCH-LUM-2025-R03",
          mfgDate = "05/2025",
          expiryDate = "01/2027",
          shelfLifeMonths = 20,
          isNearExpiry = true,
          autoDiscountPercent = 35 // Near-expiry auto-discount engine!
        ),
        warehouseStock = mapOf(
          "WH-Mumbai Central" to 14,
          "WH-Delhi NCR" to 9,
          "WH-Bengaluru South" to 7
        ),
        variants = listOf(
          ProductVariant("V1", "Amber Airless Pump 30ml", 30, 1.0)
        ),
        description = "Slow-release anti-aging night elixir targeting stubborn fine lines, crow's feet, and photo-aging with minimal redness.",
        isBestSeller = false
      ),
      SkincareProduct(
        id = "PROD-005",
        title = "Salicylic Clarifying 2% BHA Foaming Gel Cleanser",
        brand = "ClearDerm Cosmeceuticals",
        category = SkincareCategory.CLEANSERS,
        skinTypes = listOf(SkinType.OILY, SkinType.COMBINATION),
        skinConcerns = listOf(SkinConcern.ACNE, SkinConcern.DULLNESS),
        price = 449.0,
        originalPrice = 599.0,
        rating = 4.7,
        reviewCount = 1830,
        volume = "150 ml / 5.1 fl. oz.",
        dermatologyIngredients = listOf(
          IngredientDetail("Salicylic Acid (BHA)", "2.0%", "Penetrates lipid pores to dissolve comedones and blackheads"),
          IngredientDetail("Tea Tree Hydrosol", "5.0%", "Natural antibacterial botanical agent"),
          IngredientDetail("Allantoin", "0.5%", "Soothes skin during active exfoliation")
        ),
        fullIngredientsText = "Water, Cocamidopropyl Betaine, Sodium Lauroyl Sarcosinate, Salicylic Acid, Melaleuca Alternifolia Water, Allantoin, Citric Acid, Disodium EDTA.",
        usageInstructions = "Lather a dime size with water. Gently massage for 60 seconds targeting T-zone. Rinse with lukewarm water.",
        patchTestWarning = "Do not use on broken, weeping eczema or open cuts. Avoid contact with ocular mucosa.",
        batchInfo = BatchInfo(
          batchNumber = "BATCH-CD-2026-F11",
          mfgDate = "02/2026",
          expiryDate = "02/2028",
          shelfLifeMonths = 24,
          isNearExpiry = false,
          autoDiscountPercent = 0
        ),
        warehouseStock = mapOf(
          "WH-Mumbai Central" to 130,
          "WH-Delhi NCR" to 95,
          "WH-Bengaluru South" to 125
        ),
        variants = listOf(
          ProductVariant("V1", "Pump Bottle 150ml", 150, 1.0),
          ProductVariant("V2", "Refill Pouch 300ml", 300, 1.65)
        ),
        description = "Pore-clearing foaming wash with pharmaceutical-grade BHA to combat cystic blemishes, congestion, and midday excess shine.",
        isBestSeller = true
      ),
      SkincareProduct(
        id = "PROD-006",
        title = "Centella Calming Botanical Essence Toner (84% Cica)",
        brand = "Auraea Botanical Essence",
        category = SkincareCategory.TONERS,
        skinTypes = listOf(SkinType.SENSITIVE, SkinType.DRY, SkinType.ALL),
        skinConcerns = listOf(SkinConcern.BARRIER_REPAIR, SkinConcern.DULLNESS, SkinConcern.ACNE),
        price = 599.0,
        originalPrice = 799.0,
        rating = 4.8,
        reviewCount = 1120,
        volume = "200 ml / 6.7 fl. oz.",
        dermatologyIngredients = listOf(
          IngredientDetail("Centella Asiatica Extract", "84.0%", "High concentration of Asiaticoside to quench irritation"),
          IngredientDetail("Polyglutamic Acid", "1.0%", "Holds 5x more moisture than Hyaluronic Acid"),
          IngredientDetail("Panthenol (Pro-Vitamin B5)", "2.0%", "Deep dermal hydration and elasticity booster")
        ),
        fullIngredientsText = "Centella Asiatica Leaf Extract, Butylene Glycol, Panthenol, Polyglutamic Acid, 1,2-Hexanediol, Betaine, Disodium EDTA, Sodium Hyaluronate.",
        usageInstructions = "After cleansing, pour onto palms or cotton pad and gently pat into skin until absorbed. Layer 2-3 times for glass skin hydration.",
        patchTestWarning = "100% alcohol-free, fragrance-free, essential oil-free. Tested on sensitive skin subjects.",
        batchInfo = BatchInfo(
          batchNumber = "BATCH-AUR-2026-T84",
          mfgDate = "03/2026",
          expiryDate = "03/2028",
          shelfLifeMonths = 24,
          isNearExpiry = false,
          autoDiscountPercent = 0
        ),
        warehouseStock = mapOf(
          "WH-Mumbai Central" to 90,
          "WH-Delhi NCR" to 110,
          "WH-Bengaluru South" to 80
        ),
        variants = listOf(
          ProductVariant("V1", "Frosted Glass 200ml", 200, 1.0)
        ),
        description = "Ultra-soothing moisture barrier balancing essence toner that immediately neutralizes stinging, flushing, and dehydration.",
        isBestSeller = false
      )
    )
    _products.value = initialProducts

    val sampleOrders = listOf(
      Order(
        id = "ORD-98210",
        orderNumber = "GLOW-2026-98210",
        customerName = "Aditi Sharma",
        customerPhone = "+91 98201 44521",
        customerEmail = "aditi.sharma@example.com",
        shippingAddress = initialAddresses.first(),
        items = listOf(
          CartItem(initialProducts[0], initialProducts[0].variants[0], 1),
          CartItem(initialProducts[1], initialProducts[1].variants[0], 1)
        ),
        subtotal = 1248.0,
        taxGst = 224.64,
        shippingFee = 0.0,
        discountAmount = 249.6,
        appliedCoupon = "GLOW20",
        totalAmount = 1223.04,
        paymentMethod = PaymentMethod.UPI,
        deliverySpeed = DeliverySpeed.EXPRESS,
        milestone = DeliveryMilestone.OUT_FOR_DELIVERY,
        placedTimestamp = System.currentTimeMillis() - 86400000L,
        courierPartner = "BlueDart Express AWB",
        awbTrackingNumber = "AWB-BLU-849201948",
        deliveryOtp = "4819",
        deliveryExecutiveName = "Ramesh Kumar (Van #MH-02-CB-9102)",
        deliveryExecutivePhone = "+91 98112 00921",
        vehicleNumber = "MH-02-CB-9102",
        codCashCollected = false,
        warehouseAllocated = "WH-Mumbai Central Fulfillment Hub"
      ),
      Order(
        id = "ORD-98104",
        orderNumber = "GLOW-2026-98104",
        customerName = "Aditi Sharma",
        customerPhone = "+91 98201 44521",
        customerEmail = "aditi.sharma@example.com",
        shippingAddress = initialAddresses.first(),
        items = listOf(
          CartItem(initialProducts[2], initialProducts[2].variants[0], 1)
        ),
        subtotal = 849.0,
        taxGst = 152.82,
        shippingFee = 49.0,
        discountAmount = 0.0,
        appliedCoupon = null,
        totalAmount = 1050.82,
        paymentMethod = PaymentMethod.COD,
        deliverySpeed = DeliverySpeed.STANDARD,
        milestone = DeliveryMilestone.DELIVERED,
        placedTimestamp = System.currentTimeMillis() - 432000000L,
        courierPartner = "Delhivery Logistics",
        awbTrackingNumber = "AWB-DEL-109283741",
        deliveryOtp = "7291",
        deliveryExecutiveName = "Sunil Verma",
        deliveryExecutivePhone = "+91 98765 43210",
        vehicleNumber = "MH-03-AX-4412",
        codCashCollected = true,
        warehouseAllocated = "WH-Mumbai Central Fulfillment Hub"
      )
    )
    _orders.value = sampleOrders

    _returnRequests.value = listOf(
      ReturnRequest(
        id = "RET-001",
        orderId = "ORD-98104",
        orderNumber = "GLOW-2026-98104",
        reason = "Damaged outer seal upon doorstep inspection",
        itemTitle = "Botanical Hydration Velvet Barrier Cream",
        status = "PICKUP_SCHEDULED",
        refundAmount = 849.0,
        requestDate = "03 Nov 2026"
      )
    )

    _supportTickets.value = listOf(
      SupportTicket(
        id = "TCK-8812",
        customerEmail = "priya.k@example.com",
        subject = "Question on combining 10% Niacinamide with Retinol Night Elixir",
        category = "Dermatological Guidance",
        status = "IN_PROGRESS",
        priority = "NORMAL",
        lastMessage = "You can safely use Niacinamide in AM and Retinol at PM, separated by hydration barrier cream.",
        updatedTime = "10 mins ago"
      ),
      SupportTicket(
        id = "TCK-8819",
        customerEmail = "aditi.sharma@example.com",
        subject = "Change delivery address for Order GLOW-2026-98210",
        category = "Fulfillment & Courier",
        status = "OPEN",
        priority = "URGENT",
        lastMessage = "Customer requested delivery after 4 PM at Bandra West residence.",
        updatedTime = "25 mins ago"
      )
    )

    _auditLogs.value = listOf(
      AuditLog(
        id = "LOG-101",
        role = UserRole.OPERATIONS_ADMIN,
        action = "BATCH_EXPIRY_ALERT",
        entityType = "BATCH",
        entityId = "BATCH-LUM-2025-R03",
        timestamp = getCurrentFormattedDate(),
        metadata = "Near-expiry engine auto-applied 35% clearance discount for Retinol stock"
      ),
      AuditLog(
        id = "LOG-102",
        role = UserRole.SUPER_ADMIN,
        action = "RBAC_ROLE_CHANGE",
        entityType = "USER",
        entityId = "US-98124",
        timestamp = getCurrentFormattedDate(),
        metadata = "Granted OperationsAdmin access with multi-warehouse allocation permission"
      ),
      AuditLog(
        id = "LOG-103",
        role = UserRole.DELIVERY_PARTNER,
        action = "DOORSTEP_COD_COLLECTED",
        entityType = "ORDER",
        entityId = "ORD-98104",
        timestamp = getCurrentFormattedDate(),
        metadata = "COD Cash ₹1050.82 collected & settled via executive wallet"
      )
    )
  }

  fun switchRole(role: UserRole) {
    _currentRole.value = role
    recordAuditLog(
      role = role,
      action = "SWITCH_ROLE",
      entityType = "SESSION",
      entityId = UUID.randomUUID().toString().take(8),
      metadata = "User switched active operational view to ${role.displayName}"
    )
  }

  fun addToCart(product: SkincareProduct, variant: ProductVariant, quantity: Int = 1) {
    val current = _cart.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.product.id == product.id && it.selectedVariant.variantId == variant.variantId
    }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
    } else {
      current.add(CartItem(product, variant, quantity))
    }
    _cart.value = current
  }

  fun updateCartQuantity(productId: String, variantId: String, newQty: Int) {
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst {
      it.product.id == productId && it.selectedVariant.variantId == variantId
    }
    if (index >= 0) {
      if (newQty <= 0) {
        current.removeAt(index)
      } else {
        current[index] = current[index].copy(quantity = newQty)
      }
      _cart.value = current
    }
  }

  fun removeFromCart(productId: String, variantId: String) {
    _cart.value = _cart.value.filterNot {
      it.product.id == productId && it.selectedVariant.variantId == variantId
    }
  }

  fun clearCart() {
    _cart.value = emptyList()
    _appliedCoupon.value = null
  }

  fun toggleWishlist(productId: String) {
    val set = _wishlist.value.toMutableSet()
    if (set.contains(productId)) {
      set.remove(productId)
    } else {
      set.add(productId)
    }
    _wishlist.value = set
  }

  fun applyCoupon(code: String): Pair<Boolean, String> {
    val coupon = availableCoupons.find { it.code.equals(code.trim(), ignoreCase = true) }
      ?: return Pair(false, "Invalid coupon code. Try GLOW20 or FREESHIP.")
    val subtotal = _cart.value.sumOf { it.product.effectivePrice * it.selectedVariant.priceMultiplier * it.quantity }
    if (subtotal < coupon.minCartValue) {
      return Pair(false, "Minimum cart value for ${coupon.code} is ₹${coupon.minCartValue.toInt()}")
    }
    _appliedCoupon.value = coupon
    return Pair(true, "${coupon.code} applied! You saved up to ₹${coupon.maxDiscountAmount.toInt()}")
  }

  fun removeCoupon() {
    _appliedCoupon.value = null
  }

  fun placeOrder(
    deliverySpeed: DeliverySpeed,
    paymentMethod: PaymentMethod
  ): Order {
    val cartItems = _cart.value
    val subtotal = cartItems.sumOf { it.product.effectivePrice * it.selectedVariant.priceMultiplier * it.quantity }
    val taxGst = subtotal * 0.18
    val shippingFee = if (subtotal >= 499.0 || _appliedCoupon.value?.code == "FREESHIP") 0.0 else deliverySpeed.fee

    val coupon = _appliedCoupon.value
    val discountAmount = if (coupon != null && coupon.code != "FREESHIP") {
      minOf(subtotal * (coupon.discountPercent / 100.0), coupon.maxDiscountAmount)
    } else 0.0

    val totalAmount = subtotal + taxGst + shippingFee - discountAmount
    val orderNum = "GLOW-2026-${(10000..99999).random()}"
    val randomOtp = (1000..9999).random().toString()
    val randomAwb = "AWB-DEL-${(100000000..999999999).random()}"

    val newOrder = Order(
      id = "ORD-${System.currentTimeMillis().toString().takeLast(6)}",
      orderNumber = orderNum,
      customerName = _selectedAddress.value?.fullName ?: "Aditi Sharma",
      customerPhone = _selectedAddress.value?.phone ?: "+91 98201 44521",
      customerEmail = "aditi.sharma@example.com",
      shippingAddress = _selectedAddress.value ?: _savedAddresses.value.first(),
      items = cartItems,
      subtotal = subtotal,
      taxGst = taxGst,
      shippingFee = shippingFee,
      discountAmount = discountAmount,
      appliedCoupon = coupon?.code,
      totalAmount = totalAmount,
      paymentMethod = paymentMethod,
      deliverySpeed = deliverySpeed,
      milestone = DeliveryMilestone.PLACED,
      placedTimestamp = System.currentTimeMillis(),
      courierPartner = if (deliverySpeed == DeliverySpeed.GLOW_SAME_DAY) "GlowExpress Priority Fleet" else "BlueDart Air Logistics",
      awbTrackingNumber = randomAwb,
      deliveryOtp = randomOtp,
      deliveryExecutiveName = "Vikram Patil",
      deliveryExecutivePhone = "+91 98200 11982",
      vehicleNumber = "MH-01-AZ-8012",
      warehouseAllocated = "WH-Mumbai Central Fulfillment Hub"
    )

    // Lock and reduce stock in warehouse
    val updatedProducts = _products.value.map { product ->
      val inCart = cartItems.find { it.product.id == product.id }
      if (inCart != null) {
        val currentMap = product.warehouseStock.toMutableMap()
        val mumbaiStock = currentMap["WH-Mumbai Central"] ?: 10
        currentMap["WH-Mumbai Central"] = maxOf(0, mumbaiStock - inCart.quantity)
        product.copy(warehouseStock = currentMap)
      } else product
    }
    _products.value = updatedProducts

    _orders.value = listOf(newOrder) + _orders.value
    // Award 5% loyalty points on purchase
    val pointsEarned = (totalAmount * 0.05).toInt()
    _loyaltyPoints.value += pointsEarned

    recordAuditLog(
      role = UserRole.CUSTOMER,
      action = "ORDER_PLACED",
      entityType = "ORDER",
      entityId = newOrder.orderNumber,
      metadata = "Order placed via ${paymentMethod.name} for ₹${String.format(Locale.US, "%.2f", totalAmount)}. Earned $pointsEarned GlowPoints."
    )

    clearCart()
    return newOrder
  }

  fun updateOrderStatus(orderId: String, newMilestone: DeliveryMilestone) {
    _orders.value = _orders.value.map { order ->
      if (order.id == orderId) {
        order.copy(milestone = newMilestone)
      } else order
    }
    recordAuditLog(
      role = _currentRole.value,
      action = "ORDER_STATUS_UPDATED",
      entityType = "ORDER",
      entityId = orderId,
      metadata = "Milestone transitioned to ${newMilestone.title}"
    )
  }

  fun verifyDeliveryOtp(orderId: String, enteredOtp: String): Boolean {
    val order = _orders.value.find { it.id == orderId } ?: return false
    return if (order.deliveryOtp == enteredOtp.trim()) {
      updateOrderStatus(orderId, DeliveryMilestone.DELIVERED)
      if (order.paymentMethod == PaymentMethod.COD) {
        collectCodPayment(orderId)
      }
      true
    } else {
      false
    }
  }

  fun collectCodPayment(orderId: String) {
    _orders.value = _orders.value.map { order ->
      if (order.id == orderId) {
        order.copy(codCashCollected = true)
      } else order
    }
    recordAuditLog(
      role = UserRole.DELIVERY_PARTNER,
      action = "COD_COLLECTED",
      entityType = "ORDER",
      entityId = orderId,
      metadata = "COD amount collected & verified by OTP"
    )
  }

  fun initiateReturn(orderId: String, itemTitle: String, reason: String) {
    val order = _orders.value.find { it.id == orderId } ?: return
    val newReturn = ReturnRequest(
      id = "RET-${(100..999).random()}",
      orderId = orderId,
      orderNumber = order.orderNumber,
      reason = reason,
      itemTitle = itemTitle,
      status = "REQUESTED",
      refundAmount = order.totalAmount,
      requestDate = getCurrentFormattedDate()
    )
    _returnRequests.value = listOf(newReturn) + _returnRequests.value
    updateOrderStatus(orderId, DeliveryMilestone.RETURN_REQUESTED)
    recordAuditLog(
      role = UserRole.CUSTOMER,
      action = "RETURN_INITIATED",
      entityType = "RETURN",
      entityId = newReturn.id,
      metadata = "Return requested for reason: $reason"
    )
  }

  fun approveReturnAndRefund(returnId: String) {
    _returnRequests.value = _returnRequests.value.map { ret ->
      if (ret.id == returnId) {
        ret.copy(status = "REFUND_COMPLETED")
      } else ret
    }
    val targetReturn = _returnRequests.value.find { it.id == returnId }
    if (targetReturn != null) {
      updateOrderStatus(targetReturn.orderId, DeliveryMilestone.REFUNDED)
    }
    recordAuditLog(
      role = _currentRole.value,
      action = "RETURN_REFUNDED",
      entityType = "RETURN",
      entityId = returnId,
      metadata = "Quality check passed, refund settled to original customer account"
    )
  }

  fun createSupportTicket(subject: String, category: String, message: String) {
    val newTicket = SupportTicket(
      id = "TCK-${(1000..9999).random()}",
      customerEmail = "aditi.sharma@example.com",
      subject = subject,
      category = category,
      status = "OPEN",
      priority = "NORMAL",
      lastMessage = message,
      updatedTime = "Just now"
    )
    _supportTickets.value = listOf(newTicket) + _supportTickets.value
  }

  fun resolveSupportTicket(ticketId: String, resolutionNote: String) {
    _supportTickets.value = _supportTickets.value.map { ticket ->
      if (ticket.id == ticketId) {
        ticket.copy(status = "RESOLVED", lastMessage = resolutionNote, updatedTime = "Just now")
      } else ticket
    }
    recordAuditLog(
      role = UserRole.CUSTOMER_SUPPORT,
      action = "TICKET_RESOLVED",
      entityType = "TICKET",
      entityId = ticketId,
      metadata = "Support ticket closed with note: $resolutionNote"
    )
  }

  fun overrideRefundAsStoreCredit(orderId: String, amount: Double) {
    _loyaltyPoints.value += (amount).toInt()
    recordAuditLog(
      role = UserRole.CUSTOMER_SUPPORT,
      action = "REFUND_OVERRIDE_STORE_CREDIT",
      entityType = "ORDER",
      entityId = orderId,
      metadata = "Support specialist issued instant store credit of ₹${amount.toInt()}"
    )
  }

  fun updateStock(productId: String, warehouseKey: String, newStock: Int) {
    _products.value = _products.value.map { product ->
      if (product.id == productId) {
        val newMap = product.warehouseStock.toMutableMap()
        newMap[warehouseKey] = newStock
        product.copy(warehouseStock = newMap)
      } else product
    }
    recordAuditLog(
      role = _currentRole.value,
      action = "STOCK_UPDATED",
      entityType = "PRODUCT",
      entityId = productId,
      metadata = "Warehouse $warehouseKey stock adjusted to $newStock units"
    )
  }

  fun triggerBatchDiscount(productId: String, discountPercent: Int) {
    _products.value = _products.value.map { product ->
      if (product.id == productId) {
        product.copy(
          batchInfo = product.batchInfo.copy(
            isNearExpiry = true,
            autoDiscountPercent = discountPercent
          )
        )
      } else product
    }
    recordAuditLog(
      role = _currentRole.value,
      action = "BATCH_DISCOUNT_TRIGGERED",
      entityType = "PRODUCT",
      entityId = productId,
      metadata = "Near-expiry $discountPercent% markdown applied to batch"
    )
  }

  fun addNewProduct(product: SkincareProduct) {
    _products.value = listOf(product) + _products.value
    recordAuditLog(
      role = UserRole.SELLER_VENDOR,
      action = "PRODUCT_CREATED",
      entityType = "PRODUCT",
      entityId = product.id,
      metadata = "Brand registered new SKU: ${product.title}"
    )
  }

  fun recordAuditLog(
    role: UserRole,
    action: String,
    entityType: String,
    entityId: String,
    metadata: String
  ) {
    val newLog = AuditLog(
      id = "LOG-${System.currentTimeMillis().toString().takeLast(5)}",
      role = role,
      action = action,
      entityType = entityType,
      entityId = entityId,
      timestamp = getCurrentFormattedDate(),
      metadata = metadata
    )
    _auditLogs.value = listOf(newLog) + _auditLogs.value
  }

  private fun getCurrentFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date())
  }
}
