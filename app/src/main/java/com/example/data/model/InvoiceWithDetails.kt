package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class InvoiceWithDetails(
    @Embedded val invoice: Invoice,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItem>
)
