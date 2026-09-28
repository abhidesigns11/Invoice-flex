package com.example.data.repository

import android.util.Log
import com.example.data.db.SampleDataGenerator
import com.example.data.model.BusinessProfile
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.InvoiceType
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.Item
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentMode
import com.example.data.model.PaymentStatus
import com.example.data.model.PaymentTransaction
import com.example.data.model.StockTransaction
import com.example.data.model.StockTransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * A single invoice plus its line items, exactly as one Firestore document
 * (no separate join needed — Firestore nests the items right inside).
 */
data class InvoiceRecord(
    val invoice: Invoice = Invoice(),
    val items: List<InvoiceItem> = emptyList()
) {
    constructor() : this(invoice = Invoice())
}

/**
 * All of this account's business data — Parties, Items, Invoices, Payments,
 * Stock — lives in Firestore under users/{uid}/..., so it's tied to the
 * signed-in account and follows them to any device. The public functions
 * here are unchanged from the old Room-backed version on purpose: nothing
 * else in the app (ViewModel, screens) had to change for this migration.
 */
class BillingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun uid(): String =
        FirebaseAuth.getInstance().currentUser?.uid
            ?: error("BillingRepository was used while signed out")

    private fun userDoc() = firestore.collection("users").document(uid())
    private fun partiesCol() = userDoc().collection("parties")
    private fun itemsCol() = userDoc().collection("items")
    private fun invoicesCol() = userDoc().collection("invoices")
    private fun transactionsCol() = userDoc().collection("transactions")
    private fun stockTransactionsCol() = userDoc().collection("stock_transactions")
    private fun countersDoc() = userDoc().collection("meta").document("counters")
    private fun businessProfileDoc() = userDoc().collection("meta").document("business_profile")

    /** Hands out the next sequential Long id for a given kind of record, per account. */
    private suspend fun nextId(counterField: String): Long {
        return firestore.runTransaction { txn ->
            val snapshot = txn.get(countersDoc())
            val current = snapshot.getLong(counterField) ?: 0L
            val next = current + 1
            txn.set(countersDoc(), mapOf(counterField to next), SetOptions.merge())
            next
        }.await()
    }

    private inline fun <reified T : Any> liveList(query: Query): Flow<List<T>> = callbackFlow {
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // e.g. permission denied — show an empty list rather than crashing the app
                Log.e("BillingRepository", "Firestore listen failed", error)
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                try {
                    doc.toObject(T::class.java)
                } catch (e: Exception) {
                    Log.e("BillingRepository", "Skipping unreadable document ${doc.id}", e)
                    null
                }
            }.orEmpty()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    private fun <T> liveDoc(
        docRef: com.google.firebase.firestore.DocumentReference,
        clazz: Class<T>
    ): Flow<T?> = callbackFlow {
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("BillingRepository", "Firestore doc listen failed", error)
                trySend(null)
                return@addSnapshotListener
            }
            val value = try {
                if (snapshot != null && snapshot.exists()) snapshot.toObject(clazz) else null
            } catch (e: Exception) {
                Log.e("BillingRepository", "Unreadable document", e)
                null
            }
            trySend(value)
        }
        awaitClose { listener.remove() }
    }

    // --- Profile ---
    // (The Q&A onboarding profile itself lives in Firestore too, but is owned by
    // AuthViewModel/ProfileRepository — this is the separate invoicing-specific
    // profile: tagline, invoice prefixes, terms & conditions, etc.)
    val businessProfile: Flow<BusinessProfile?>
        get() = liveDoc(businessProfileDoc(), BusinessProfile::class.java)

    suspend fun getBusinessProfileSync(): BusinessProfile? {
        val snapshot = businessProfileDoc().get().await()
        return if (snapshot.exists()) snapshot.toObject(BusinessProfile::class.java) else null
    }

    suspend fun saveBusinessProfile(profile: BusinessProfile) {
        businessProfileDoc().set(profile).await()
    }

    // --- Parties ---
    val allParties: Flow<List<Party>>
        get() = liveList(partiesCol().orderBy("name"))
    val customers: Flow<List<Party>>
        get() = allParties.map { list -> list.filter { it.partyType == PartyType.CUSTOMER } }
    val suppliers: Flow<List<Party>>
        get() = allParties.map { list -> list.filter { it.partyType == PartyType.SUPPLIER } }

    fun getParty(id: Long): Flow<Party?> = allParties.map { list -> list.find { it.id == id } }
    fun getPartyById(id: Long): Flow<Party?> = getParty(id)
    fun searchParties(query: String): Flow<List<Party>> = allParties.map { list ->
        list.filter { it.name.contains(query, ignoreCase = true) || it.phone.contains(query) }
    }

    suspend fun saveParty(party: Party): Long {
        val id = if (party.id != 0L) party.id else nextId("party")
        partiesCol().document(id.toString()).set(party.copy(id = id)).await()
        return id
    }

    suspend fun updateParty(party: Party) {
        partiesCol().document(party.id.toString()).set(party).await()
    }

    suspend fun deleteParty(party: Party) {
        partiesCol().document(party.id.toString()).delete().await()
    }

    private suspend fun adjustPartyBalance(partyId: Long, amountDiff: Double) {
        val ref = partiesCol().document(partyId.toString())
        firestore.runTransaction { txn ->
            val snapshot = txn.get(ref)
            val current = snapshot.getDouble("currentBalance") ?: 0.0
            txn.update(ref, "currentBalance", current + amountDiff)
        }.await()
    }

    // --- Items ---
    val allItems: Flow<List<Item>>
        get() = liveList(itemsCol().orderBy("name"))
    val lowStockItems: Flow<List<Item>>
        get() = allItems.map { list -> list.filter { !it.isService && it.currentStock <= it.minStockAlert } }
    val categories: Flow<List<String>>
        get() = allItems.map { list -> list.map { it.category }.filter { it.isNotBlank() }.distinct().sorted() }

    fun getItem(id: Long): Flow<Item?> = allItems.map { list -> list.find { it.id == id } }
    fun searchItems(query: String): Flow<List<Item>> = allItems.map { list ->
        list.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.itemCode.contains(query, ignoreCase = true) ||
                it.barcode.contains(query, ignoreCase = true)
        }
    }

    suspend fun saveItem(item: Item): Long {
        val id = if (item.id != 0L) item.id else nextId("item")
        itemsCol().document(id.toString()).set(item.copy(id = id)).await()
        return id
    }

    suspend fun updateItem(item: Item) {
        itemsCol().document(item.id.toString()).set(item).await()
    }

    suspend fun deleteItem(item: Item) {
        itemsCol().document(item.id.toString()).delete().await()
    }

    private suspend fun adjustItemStock(itemId: Long, qtyChange: Double) {
        val ref = itemsCol().document(itemId.toString())
        firestore.runTransaction { txn ->
            val snapshot = txn.get(ref)
            val current = snapshot.getDouble("currentStock") ?: 0.0
            txn.update(ref, "currentStock", current + qtyChange)
        }.await()
    }

    private suspend fun insertStockTransaction(tx: StockTransaction) {
        val id = nextId("stockTransaction")
        stockTransactionsCol().document(id.toString()).set(tx.copy(id = id)).await()
    }

    suspend fun adjustStock(
        itemId: Long,
        itemName: String,
        adjustmentQty: Double,
        reason: StockTransactionType,
        notes: String
    ) {
        adjustItemStock(itemId, adjustmentQty)
        insertStockTransaction(
            StockTransaction(
                itemId = itemId,
                itemName = itemName,
                type = reason,
                quantity = adjustmentQty,
                notes = notes
            )
        )
    }

    // --- Invoices ---
    private val allInvoiceRecords: Flow<List<InvoiceRecord>>
        get() = liveList<InvoiceRecord>(invoicesCol())

    val allInvoices: Flow<List<InvoiceWithDetails>>
        get() = allInvoiceRecords.map { list ->
            list.map { InvoiceWithDetails(it.invoice, it.items) }
                .sortedWith(compareByDescending<InvoiceWithDetails> { it.invoice.date }.thenByDescending { it.invoice.id })
        }

    fun getInvoicesByType(type: InvoiceType): Flow<List<InvoiceWithDetails>> =
        allInvoices.map { list -> list.filter { it.invoice.invoiceType == type } }

    fun getInvoiceById(id: Long): Flow<InvoiceWithDetails?> =
        allInvoices.map { list -> list.find { it.invoice.id == id } }

    suspend fun getInvoiceByIdSync(id: Long): InvoiceWithDetails? =
        allInvoices.firstOrNull()?.find { it.invoice.id == id }

    fun getInvoicesByParty(partyId: Long): Flow<List<InvoiceWithDetails>> =
        allInvoices.map { list -> list.filter { it.invoice.partyId == partyId } }

    suspend fun createInvoice(
        invoice: Invoice,
        items: List<InvoiceItem>,
        updateStockAndBalance: Boolean = true
    ): Long {
        val invoiceId = nextId("invoice")
        val invoiceWithId = invoice.copy(id = invoiceId)
        val itemsWithId = items.map { it.copy(invoiceId = invoiceId) }
        invoicesCol().document(invoiceId.toString())
            .set(InvoiceRecord(invoiceWithId, itemsWithId))
            .await()

        if (updateStockAndBalance) {
            invoiceWithId.partyId?.let { pId ->
                when (invoiceWithId.invoiceType) {
                    InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL -> {
                        if (invoiceWithId.balanceDue > 0) adjustPartyBalance(pId, invoiceWithId.balanceDue)
                    }
                    InvoiceType.PURCHASE_INVOICE -> {
                        if (invoiceWithId.balanceDue > 0) adjustPartyBalance(pId, -invoiceWithId.balanceDue)
                    }
                    InvoiceType.CREDIT_NOTE_RETURN -> {
                        adjustPartyBalance(pId, -invoiceWithId.grandTotal)
                    }
                    else -> {}
                }
            }

            itemsWithId.forEach { invItem ->
                invItem.itemId?.let { itemId ->
                    when (invoiceWithId.invoiceType) {
                        InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL -> {
                            adjustItemStock(itemId, -invItem.quantity)
                            insertStockTransaction(
                                StockTransaction(
                                    itemId = itemId,
                                    itemName = invItem.itemName,
                                    type = StockTransactionType.STOCK_OUT,
                                    quantity = invItem.quantity,
                                    referenceInvoiceId = invoiceId,
                                    referenceInvoiceNumber = invoiceWithId.invoiceNumber,
                                    notes = "Sold in ${invoiceWithId.invoiceNumber}"
                                )
                            )
                        }
                        InvoiceType.PURCHASE_INVOICE -> {
                            adjustItemStock(itemId, invItem.quantity)
                            insertStockTransaction(
                                StockTransaction(
                                    itemId = itemId,
                                    itemName = invItem.itemName,
                                    type = StockTransactionType.STOCK_IN,
                                    quantity = invItem.quantity,
                                    referenceInvoiceId = invoiceId,
                                    referenceInvoiceNumber = invoiceWithId.invoiceNumber,
                                    notes = "Purchased in ${invoiceWithId.invoiceNumber}"
                                )
                            )
                        }
                        InvoiceType.CREDIT_NOTE_RETURN -> {
                            adjustItemStock(itemId, invItem.quantity)
                            insertStockTransaction(
                                StockTransaction(
                                    itemId = itemId,
                                    itemName = invItem.itemName,
                                    type = StockTransactionType.STOCK_IN,
                                    quantity = invItem.quantity,
                                    referenceInvoiceId = invoiceId,
                                    referenceInvoiceNumber = invoiceWithId.invoiceNumber,
                                    notes = "Returned in ${invoiceWithId.invoiceNumber}"
                                )
                            )
                        }
                        else -> {}
                    }
                }
            }

            if (invoiceWithId.paidAmount > 0) {
                insertTransaction(
                    PaymentTransaction(
                        partyId = invoiceWithId.partyId,
                        partyName = invoiceWithId.partyName,
                        invoiceId = invoiceId,
                        invoiceNumber = invoiceWithId.invoiceNumber,
                        type = if (invoiceWithId.invoiceType == InvoiceType.PURCHASE_INVOICE) "PAYMENT_OUT" else "PAYMENT_IN",
                        amount = invoiceWithId.paidAmount,
                        paymentMode = invoiceWithId.paymentMode,
                        date = invoiceWithId.date,
                        notes = "Payment for ${invoiceWithId.invoiceNumber}"
                    )
                )
            }
        }

        return invoiceId
    }

    suspend fun deleteInvoice(invoiceWithDetails: InvoiceWithDetails) {
        invoicesCol().document(invoiceWithDetails.invoice.id.toString()).delete().await()
    }

    suspend fun updateInvoicePayment(invoiceId: Long, additionalPayment: Double, paymentMode: PaymentMode, notes: String = "") {
        val invWithDetails = getInvoiceByIdSync(invoiceId) ?: return
        val inv = invWithDetails.invoice
        val newPaid = (inv.paidAmount + additionalPayment).coerceAtMost(inv.grandTotal)
        val newBalanceDue = (inv.grandTotal - newPaid).coerceAtLeast(0.0)
        val newStatus = when {
            newBalanceDue <= 0.0 -> PaymentStatus.PAID
            newPaid > 0.0 -> PaymentStatus.PARTIAL
            else -> PaymentStatus.UNPAID
        }
        val updatedInvoice = inv.copy(paidAmount = newPaid, balanceDue = newBalanceDue, paymentStatus = newStatus)
        invoicesCol().document(invoiceId.toString())
            .set(InvoiceRecord(updatedInvoice, invWithDetails.items))
            .await()

        inv.partyId?.let { pId ->
            when (inv.invoiceType) {
                InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL -> adjustPartyBalance(pId, -additionalPayment)
                InvoiceType.PURCHASE_INVOICE -> adjustPartyBalance(pId, additionalPayment)
                else -> {}
            }
        }

        insertTransaction(
            PaymentTransaction(
                partyId = inv.partyId,
                partyName = inv.partyName,
                invoiceId = inv.id,
                invoiceNumber = inv.invoiceNumber,
                type = if (inv.invoiceType == InvoiceType.PURCHASE_INVOICE) "PAYMENT_OUT" else "PAYMENT_IN",
                amount = additionalPayment,
                paymentMode = paymentMode,
                date = System.currentTimeMillis(),
                notes = notes.ifBlank { "Payment for ${inv.invoiceNumber}" }
            )
        )
    }

    suspend fun generateNextInvoiceNumber(type: InvoiceType): String {
        val profile = getBusinessProfileSync() ?: SampleDataGenerator.defaultProfile
        val count = (allInvoices.firstOrNull() ?: emptyList()).count { it.invoice.invoiceType == type } + 1
        val prefix = when (type) {
            InvoiceType.SALE_INVOICE -> profile.invoicePrefix
            InvoiceType.QUOTATION_ESTIMATE -> profile.estimatePrefix
            InvoiceType.POS_BILL -> profile.posPrefix
            InvoiceType.PURCHASE_INVOICE -> "PUR-2026-"
            InvoiceType.CREDIT_NOTE_RETURN -> "CRN-2026-"
            InvoiceType.DELIVERY_CHALLAN -> "DC-2026-"
        }
        return String.format("%s%03d", prefix, count)
    }

    // --- Payments & Ledgers ---
    val allTransactions: Flow<List<PaymentTransaction>>
        get() = liveList(transactionsCol().orderBy("date", Query.Direction.DESCENDING))
    val allExpenses: Flow<List<PaymentTransaction>>
        get() = allTransactions.map { list -> list.filter { it.type == "EXPENSE" } }

    fun getTransactionsByParty(partyId: Long): Flow<List<PaymentTransaction>> =
        allTransactions.map { list -> list.filter { it.partyId == partyId } }

    suspend fun recordPartyPayment(
        partyId: Long,
        partyName: String,
        amount: Double,
        isPaymentIn: Boolean,
        paymentMode: PaymentMode,
        referenceNo: String,
        notes: String
    ): Long {
        val type = if (isPaymentIn) "PAYMENT_IN" else "PAYMENT_OUT"
        val txId = insertTransaction(
            PaymentTransaction(
                partyId = partyId,
                partyName = partyName,
                type = type,
                amount = amount,
                paymentMode = paymentMode,
                referenceNo = referenceNo,
                notes = notes
            )
        )
        val balanceDelta = if (isPaymentIn) -amount else amount
        adjustPartyBalance(partyId, balanceDelta)
        return txId
    }

    suspend fun insertTransaction(tx: PaymentTransaction): Long {
        val id = nextId("transaction")
        transactionsCol().document(id.toString()).set(tx.copy(id = id)).await()
        return id
    }

    suspend fun recordExpense(tx: PaymentTransaction): Long = insertTransaction(tx)

    suspend fun deleteTransaction(tx: PaymentTransaction) {
        transactionsCol().document(tx.id.toString()).delete().await()
    }

    // --- Stock Transactions ---
    val allStockTransactions: Flow<List<StockTransaction>>
        get() = liveList(stockTransactionsCol().orderBy("date", Query.Direction.DESCENDING))

    fun getStockTransactionsByItem(itemId: Long): Flow<List<StockTransaction>> =
        allStockTransactions.map { list -> list.filter { it.itemId == itemId } }

    // --- Dashboard Aggregates ---
    val totalReceivables: Flow<Double?>
        get() = allParties.map { list ->
            list.filter { it.currentBalance > 0 && it.partyType == PartyType.CUSTOMER }.sumOf { it.currentBalance }
        }
    val totalPayables: Flow<Double?>
        get() = allParties.map { list ->
            list.filter { it.currentBalance < 0 || it.partyType == PartyType.SUPPLIER }.sumOf { kotlin.math.abs(it.currentBalance) }
        }
    val totalSales: Flow<Double?>
        get() = allInvoices.map { list ->
            list.filter { it.invoice.invoiceType == InvoiceType.SALE_INVOICE || it.invoice.invoiceType == InvoiceType.POS_BILL }
                .sumOf { it.invoice.grandTotal }
        }
    val totalPurchases: Flow<Double?>
        get() = allInvoices.map { list ->
            list.filter { it.invoice.invoiceType == InvoiceType.PURCHASE_INVOICE }.sumOf { it.invoice.grandTotal }
        }

    fun getTodaySales(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        return allInvoices.map { list ->
            list.filter {
                (it.invoice.invoiceType == InvoiceType.SALE_INVOICE || it.invoice.invoiceType == InvoiceType.POS_BILL) &&
                    it.invoice.date >= startOfDay
            }.sumOf { it.invoice.grandTotal }
        }
    }
}
