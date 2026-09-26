package com.example.data.model

enum class PartyType(val label: String) {
    CUSTOMER("Customer"),
    SUPPLIER("Supplier / Vendor")
}

enum class InvoiceType(val label: String, val prefix: String) {
    SALE_INVOICE("Sale Bill (Tax Invoice)", "INV"),
    PURCHASE_INVOICE("Purchase Bill", "PUR"),
    QUOTATION_ESTIMATE("Estimate / Quotation", "EST"),
    CREDIT_NOTE_RETURN("Sales Return (Credit Note)", "CRN"),
    DELIVERY_CHALLAN("Delivery Challan", "DC"),
    POS_BILL("Quick POS Bill", "POS")
}

enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    PARTIAL("Partially Paid"),
    UNPAID("Unpaid"),
    OVERDUE("Overdue")
}

enum class PaymentMode(val label: String) {
    CASH("Cash"),
    UPI("UPI / QR Code"),
    BANK_TRANSFER("Bank Transfer (NEFT/IMPS)"),
    CHEQUE("Cheque"),
    CREDIT("Credit (Udhaar)"),
    SPLIT("Split Payment")
}

enum class StockTransactionType(val label: String) {
    STOCK_IN("Stock Added / In"),
    STOCK_OUT("Stock Sold / Out"),
    ADJUSTMENT_ADD("Audit Addition (+)"),
    ADJUSTMENT_REDUCE("Audit Reduction (-)"),
    DAMAGED("Damaged / Expired")
}

enum class ExpenseCategory(val label: String) {
    RENT("Office / Shop Rent"),
    SALARY("Staff Salaries & Wages"),
    ELECTRICITY_UTILITIES("Electricity & Water"),
    LOGISTICS_TRANSPORT("Logistics & Courier"),
    MARKETING_ADS("Marketing & Promotions"),
    PACKAGING("Packaging Materials"),
    MAINTENANCE("Maintenance & Repairs"),
    OTHER("Other Expenses")
}
