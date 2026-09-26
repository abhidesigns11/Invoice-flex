package com.example.ui.viewmodel

import com.example.data.model.*

data class InvoiceDraftItem(
    val tempId: String = java.util.UUID.randomUUID().toString(),
    val itemId: Long? = null,
    val itemName: String = "",
    val hsnCode: String = "",
    val quantity: Double = 1.0,
    val unit: String = "Pcs",
    val unitPrice: Double = 0.0,
    val taxRate: Double = 18.0,
    val discountPercent: Double = 0.0
) {
    val taxableAmount: Double
        get() = (quantity * unitPrice) * (1 - (discountPercent / 100.0))

    val taxAmount: Double
        get() = taxableAmount * (taxRate / 100.0)

    val totalAmount: Double
        get() = taxableAmount + taxAmount
}

data class InvoiceDraft(
    val invoiceType: InvoiceType = InvoiceType.SALE_INVOICE,
    val invoiceNumber: String = "",
    val partyId: Long? = null,
    val partyName: String = "",
    val partyPhone: String = "",
    val partyGstin: String = "",
    val partyAddress: String = "",
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000L,
    val items: List<InvoiceDraftItem> = emptyList(),
    val isInterState: Boolean = false,
    val discountAmount: Double = 0.0,
    val roundOff: Double = 0.0,
    val paidAmount: Double = 0.0,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val notes: String = "",
    val terms: String = "",
    val eWayBillNo: String = "",
    val vehicleNo: String = ""
) {
    val subTotal: Double
        get() = items.sumOf { it.taxableAmount }

    val totalTax: Double
        get() = items.sumOf { it.taxAmount }

    val cgstAmount: Double
        get() = if (isInterState) 0.0 else totalTax / 2.0

    val sgstAmount: Double
        get() = if (isInterState) 0.0 else totalTax / 2.0

    val igstAmount: Double
        get() = if (isInterState) totalTax else 0.0

    val calculatedGrandTotal: Double
        get() = (subTotal + totalTax - discountAmount + roundOff).coerceAtLeast(0.0)

    val balanceDue: Double
        get() = (calculatedGrandTotal - paidAmount).coerceAtLeast(0.0)

    val paymentStatus: PaymentStatus
        get() = when {
            balanceDue <= 0.01 -> PaymentStatus.PAID
            paidAmount > 0.0 -> PaymentStatus.PARTIAL
            else -> PaymentStatus.UNPAID
        }
}

data class PosCartItem(
    val item: Item,
    val quantity: Double = 1.0,
    val customPrice: Double = item.salePrice,
    val discountPercent: Double = 0.0
) {
    val totalAmount: Double
        get() {
            val base = quantity * customPrice * (1 - (discountPercent / 100.0))
            val tax = base * (item.taxRate / 100.0)
            return base + tax
        }
}

data class BillingUiState(
    val isLoading: Boolean = false,
    val profile: BusinessProfile? = null,
    val allInvoices: List<InvoiceWithDetails> = emptyList(),
    val allParties: List<Party> = emptyList(),
    val allItems: List<Item> = emptyList(),
    val lowStockItems: List<Item> = emptyList(),
    val categories: List<String> = emptyList(),
    val transactions: List<PaymentTransaction> = emptyList(),
    val expenses: List<PaymentTransaction> = emptyList(),
    val stockTransactions: List<StockTransaction> = emptyList(),

    // Aggregates
    val totalReceivables: Double = 0.0,
    val totalPayables: Double = 0.0,
    val todaySales: Double = 0.0,
    val totalSales: Double = 0.0,
    val totalPurchases: Double = 0.0,

    // Active Invoice Draft (for Sale / Purchase / Quotation creation)
    val invoiceDraft: InvoiceDraft = InvoiceDraft(),

    // POS Mode Cart
    val posCart: List<PosCartItem> = emptyList(),
    val posSelectedCategory: String = "All",
    val posTenderedCash: Double = 0.0,
    val posCustomerPhone: String = "",
    val posCustomerName: String = "Walk-in Customer",

    // Active Preview
    val selectedInvoice: InvoiceWithDetails? = null,
    val selectedParty: Party? = null,
    val selectedPartyTransactions: List<PaymentTransaction> = emptyList(),
    val selectedPartyInvoices: List<InvoiceWithDetails> = emptyList(),

    // Search and Filters
    val invoiceFilterType: InvoiceType? = null,
    val partyFilterType: PartyType = PartyType.CUSTOMER,
    val searchQuery: String = "",

    // Feature States
    val rewardEntries: List<RewardEntry> = emptyList(),
    val appointments: List<AppointmentTask> = emptyList(),
    val recurringBills: List<RecurringBillProfile> = emptyList(),
    val onlineStoreConfig: OnlineStoreConfig = OnlineStoreConfig(),

    // User Message Toast / Banner
    val userMessage: String? = null
)

