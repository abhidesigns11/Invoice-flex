package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "Nova Electronics & Retail",
    val tagline: String = "Wholesale & Retail Hub",
    val phone: String = "+91 98765 43210",
    val email: String = "contact@novaelectronics.com",
    val address: String = "Plot 42, Commercial Complex, Sector 18",
    val city: String = "Mumbai",
    val state: String = "Maharashtra",
    val stateCode: String = "27",
    val pincode: String = "400001",
    val gstin: String = "27ABCDE1234F1Z5",
    val upiId: String = "novabusiness@okaxis",
    val bankName: String = "HDFC Bank Ltd.",
    val accountHolderName: String = "Nova Electronics & Retail",
    val accountNumber: String = "50200012345678",
    val ifscCode: String = "HDFC0001234",
    val branch: String = "Fort, Mumbai",
    val termsAndConditions: String = "1. Goods once sold will not be taken back.\n2. Interest @18% p.a. will be charged if bill is not paid within 15 days.\n3. Subject to local jurisdiction.",
    val invoicePrefix: String = "INV-2026-",
    val estimatePrefix: String = "EST-2026-",
    val posPrefix: String = "POS-2026-",
    val isGstEnabled: Boolean = true
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(id = 1)
}

@Entity(
    tableName = "parties",
    indices = [Index(value = ["phone"]), Index(value = ["partyType"])]
)
data class Party(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val partyType: PartyType = PartyType.CUSTOMER,
    val gstin: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "Maharashtra",
    val stateCode: String = "27",
    val openingBalance: Double = 0.0,
    val currentBalance: Double = 0.0, // > 0: Receivable (To Collect), < 0: Payable (To Pay)
    val creditLimit: Double = 50000.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(name = "", phone = "")
}

@Entity(
    tableName = "items",
    indices = [Index(value = ["itemCode"]), Index(value = ["category"])]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val itemCode: String = "",
    val barcode: String = "",
    val category: String = "General",
    val unit: String = "Pcs", // Pcs, Kg, Box, Litre, Mtr, Pack, Set
    val salePrice: Double = 0.0,
    val wholesalePrice: Double = 0.0,
    val purchasePrice: Double = 0.0,
    val taxRate: Double = 18.0, // 0, 5, 12, 18, 28 %
    val hsnCode: String = "8517",
    val currentStock: Double = 0.0,
    val minStockAlert: Double = 5.0,
    val isService: Boolean = false,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(name = "")
}

@Entity(
    tableName = "invoices",
    indices = [Index(value = ["invoiceNumber"], unique = true), Index(value = ["partyId"]), Index(value = ["date"])]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val invoiceType: InvoiceType = InvoiceType.SALE_INVOICE,
    val partyId: Long? = null,
    val partyName: String = "Cash Customer",
    val partyPhone: String = "",
    val partyGstin: String = "",
    val partyAddress: String = "",
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000L,
    val subTotal: Double = 0.0,
    val totalTax: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val roundOff: Double = 0.0,
    val grandTotal: Double = 0.0,
    val paidAmount: Double = 0.0,
    val balanceDue: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PAID,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val isInterState: Boolean = false,
    val notes: String = "",
    val terms: String = "",
    val eWayBillNo: String = "",
    val vehicleNo: String = "",
    val billPhotoUri: String? = null,
    val hasPaymentReminder: Boolean = false,
    val reminderDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(invoiceNumber = "")
}

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"]), Index(value = ["itemId"])]
)
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long = 0,
    val itemId: Long? = null,
    val itemName: String,
    val hsnCode: String = "",
    val quantity: Double = 1.0,
    val unit: String = "Pcs",
    val unitPrice: Double = 0.0,
    val taxRate: Double = 18.0,
    val taxAmount: Double = 0.0,
    val discountPercent: Double = 0.0,
    val totalAmount: Double = 0.0
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(itemName = "")
}

@Entity(
    tableName = "payment_transactions",
    indices = [Index(value = ["partyId"]), Index(value = ["invoiceId"]), Index(value = ["date"])]
)
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val partyId: Long? = null,
    val partyName: String = "",
    val invoiceId: Long? = null,
    val invoiceNumber: String = "",
    val type: String = "PAYMENT_IN", // PAYMENT_IN, PAYMENT_OUT, EXPENSE
    val amount: Double = 0.0,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val date: Long = System.currentTimeMillis(),
    val referenceNo: String = "",
    val notes: String = "",
    val expenseCategory: ExpenseCategory? = null
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(id = 0)
}

@Entity(
    tableName = "stock_transactions",
    indices = [Index(value = ["itemId"]), Index(value = ["date"])]
)
data class StockTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val itemName: String,
    val type: StockTransactionType,
    val quantity: Double,
    val referenceInvoiceId: Long? = null,
    val referenceInvoiceNumber: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(itemId = 0, itemName = "", type = StockTransactionType.STOCK_IN, quantity = 0.0)
}

data class AppointmentTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val partyName: String,
    val partyPhone: String = "",
    val type: String = "Site Measurement", // Site Measurement, Delivery, Payment Collection, Inspection, Meeting
    val date: String,
    val time: String = "11:00 AM",
    val priority: String = "High", // High, Medium, Low
    val notes: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class RewardEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val partyId: Long,
    val partyName: String,
    val points: Int,
    val type: String = "EARNED", // EARNED, REDEEMED
    val description: String,
    val date: Long = System.currentTimeMillis()
)

data class RecurringBillProfile(
    val id: String = java.util.UUID.randomUUID().toString(),
    val partyName: String,
    val partyPhone: String = "",
    val itemName: String,
    val amount: Double,
    val frequency: String = "Monthly", // Weekly, Monthly, Quarterly, Annually
    val nextBillingDate: String,
    val isAutoNotifyEnabled: Boolean = true,
    val status: String = "ACTIVE"
)

data class OnlineStoreConfig(
    val storeName: String = "Apex S.S. Digital Store",
    val storeSlug: String = "invoiceflex.store/apex-fabrication",
    val whatsappNumber: String = "+919876543210",
    val aboutText: String = "Premier Stainless Steel Fabrication & Engineering Workstation Hub.",
    val deliveryInfo: String = "All Maharashtra & Pan-India Dispatch via Safe Transport.",
    val isStoreLive: Boolean = true
)

