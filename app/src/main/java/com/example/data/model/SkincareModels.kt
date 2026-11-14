package com.example.data.model

enum class UserRole(val displayName: String, val badgeColor: Long) {
  CUSTOMER("Customer Shopper", 0xFF4A7C59),
  SUPER_ADMIN("Super Admin (HQ)", 0xFF8C5346),
  OPERATIONS_ADMIN("Operations & Logistics", 0xFFD4AF37),
  SELLER_VENDOR("Beauty Brand Seller", 0xFF6366F1),
  DELIVERY_PARTNER("Glow Logistics Partner", 0xFF0284C7),
  CUSTOMER_SUPPORT("Care Desk Specialist", 0xFFEC4899)
}

enum class SkincareCategory(val label: String) {
  ALL("All"),
  SERUMS("Serums & Treatments"),
  CLEANSERS("Gentle Cleansers"),
  MOISTURIZERS("Hydration Creams"),
  SUNSCREENS("Invisible SPF"),
  RETINOLS("Retinol & Peptides"),
  TONERS("Balancing Toners")
}

enum class SkinType(val label: String) {
  ALL("All Types"),
  DRY("Dry Skin"),
  OILY("Oily / Acne-Prone"),
  SENSITIVE("Sensitive / Reactive"),
  COMBINATION("Combination"),
  NORMAL("Normal")
}

enum class SkinConcern(val label: String) {
  ACNE("Acne & Breakouts"),
  HYPERPIGMENTATION("Dark Spots & Melasma"),
  AGING("Fine Lines & Loss of Elasticity"),
  BARRIER_REPAIR("Compromised Skin Barrier"),
  DULLNESS("Dullness & Uneven Tone"),
  SUN_PROTECTION("UV Damage & Protection")
}

data class IngredientDetail(
  val name: String,
  val concentration: String,
  val clinicalRole: String
)

data class ProductVariant(
  val variantId: String,
  val name: String,
  val volumeMl: Int,
  val priceMultiplier: Double = 1.0
)

data class BatchInfo(
  val batchNumber: String,
  val mfgDate: String,
  val expiryDate: String,
  val shelfLifeMonths: Int,
  val isNearExpiry: Boolean = false,
  val autoDiscountPercent: Int = 0
)

data class SkincareProduct(
  val id: String,
  val title: String,
  val brand: String,
  val category: SkincareCategory,
  val skinTypes: List<SkinType>,
  val skinConcerns: List<SkinConcern>,
  val price: Double,
  val originalPrice: Double,
  val rating: Double,
  val reviewCount: Int,
  val volume: String,
  val dermatologyIngredients: List<IngredientDetail>,
  val fullIngredientsText: String,
  val usageInstructions: String,
  val patchTestWarning: String,
  val hsnCode: String = "330499",
  val taxRate: Double = 0.18,
  val isVegan: Boolean = true,
  val isCrueltyFree: Boolean = true,
  val batchInfo: BatchInfo,
  val warehouseStock: Map<String, Int>,
  val variants: List<ProductVariant>,
  val description: String,
  val isBestSeller: Boolean = false
) {
  val totalStock: Int get() = warehouseStock.values.sum()
  val effectivePrice: Double
    get() = if (batchInfo.isNearExpiry && batchInfo.autoDiscountPercent > 0) {
      price * (1.0 - (batchInfo.autoDiscountPercent / 100.0))
    } else price
}

data class CartItem(
  val product: SkincareProduct,
  val selectedVariant: ProductVariant,
  val quantity: Int
)

data class Address(
  val id: String,
  val fullName: String,
  val phone: String,
  val street: String,
  val landmark: String,
  val city: String,
  val state: String,
  val pincode: String,
  val isServiceable: Boolean = true,
  val deliveryInstructions: String = "Leave at front desk"
)

enum class DeliverySpeed(val title: String, val duration: String, val fee: Double) {
  STANDARD("Standard Courier", "3-4 Business Days", 49.0),
  EXPRESS("Express Air", "24-48 Hours", 99.0),
  GLOW_SAME_DAY("Glow Same-Day Priority", "Delivered Today by 8 PM", 149.0)
}

enum class PaymentMethod(val label: String, val subtitle: String) {
  COD("Cash on Delivery (COD)", "Pay cash/UPI at doorstep with OTP verification"),
  UPI("UPI Quick Pay", "Google Pay, PhonePe, Paytm, BHIM QR"),
  CARD("Credit & Debit Card", "Visa, Mastercard, RuPay, Amex (Encrypted 3DS)"),
  BNPL("Buy Now Pay Later", "Simpl, ZestMoney, LazyPay 0% interest"),
  GLOW_WALLET("GlowPoints Balance", "Instant checkout using earned reward credits")
}

enum class DeliveryMilestone(val stepIndex: Int, val title: String, val description: String) {
  PLACED(0, "Order Placed", "Payment confirmed, inventory locked across warehouse hubs."),
  PACKED(1, "Packed at Hub", "Dermatologically inspected, batch seal & GST invoice enclosed."),
  DISPATCHED(2, "Dispatched via Courier", "In transit with courier partner. Live tracking enabled."),
  OUT_FOR_DELIVERY(3, "Out for Delivery", "Delivery executive is en route with your package."),
  DELIVERED(4, "Delivered Successfully", "Delivered with signature/OTP verification."),
  RETURN_REQUESTED(5, "Return / Exchange Requested", "Quality verification ticket generated."),
  REFUNDED(6, "Refund Settled", "Amount credited to payment source.")
}

data class Order(
  val id: String,
  val orderNumber: String,
  val customerName: String,
  val customerPhone: String,
  val customerEmail: String,
  val shippingAddress: Address,
  val items: List<CartItem>,
  val subtotal: Double,
  val taxGst: Double,
  val shippingFee: Double,
  val discountAmount: Double,
  val appliedCoupon: String?,
  val totalAmount: Double,
  val paymentMethod: PaymentMethod,
  val deliverySpeed: DeliverySpeed,
  val milestone: DeliveryMilestone,
  val placedTimestamp: Long,
  val courierPartner: String,
  val awbTrackingNumber: String,
  val deliveryOtp: String,
  val deliveryExecutiveName: String,
  val deliveryExecutivePhone: String,
  val vehicleNumber: String,
  val codCashCollected: Boolean = false,
  val warehouseAllocated: String = "Mumbai Central Fulfillment Hub"
)

data class ProductReview(
  val id: String,
  val author: String,
  val rating: Int,
  val verifiedSkinType: SkinType,
  val date: String,
  val headline: String,
  val comment: String,
  val helpfulVotes: Int,
  val sellerReply: String? = null
)

data class ReturnRequest(
  val id: String,
  val orderId: String,
  val orderNumber: String,
  val reason: String,
  val itemTitle: String,
  val status: String, // "REQUESTED", "PICKUP_SCHEDULED", "QC_PASSED", "REFUND_COMPLETED"
  val refundAmount: Double,
  val requestDate: String
)

data class SupportTicket(
  val id: String,
  val customerEmail: String,
  val subject: String,
  val category: String,
  val status: String, // "OPEN", "IN_PROGRESS", "RESOLVED"
  val priority: String, // "URGENT", "HIGH", "NORMAL"
  val lastMessage: String,
  val updatedTime: String
)

data class AuditLog(
  val id: String,
  val role: UserRole,
  val action: String,
  val entityType: String,
  val entityId: String,
  val timestamp: String,
  val metadata: String
)

data class Coupon(
  val code: String,
  val discountPercent: Int,
  val minCartValue: Double,
  val maxDiscountAmount: Double,
  val description: String
)
