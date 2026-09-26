package com.example.data.db

import com.example.data.model.BusinessProfile
import com.example.data.model.ExpenseCategory
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.InvoiceType
import com.example.data.model.Item
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentMode
import com.example.data.model.PaymentStatus
import com.example.data.model.PaymentTransaction
import com.example.data.model.StockTransaction
import com.example.data.model.StockTransactionType

object SampleDataGenerator {

    val defaultProfile = BusinessProfile(
        id = 1,
        businessName = "Apex Stainless Steel & Engineering Works",
        tagline = "SS Lockers, Cabinets, Office Tables & Custom Fabrication",
        phone = "+91 98220 98765",
        email = "fabrication@apexsteel.in",
        address = "Plot 58, MIDC Industrial Area, Bhosari",
        city = "Pune",
        state = "Maharashtra",
        stateCode = "27",
        pincode = "411026",
        gstin = "27AAACA9876F1Z4",
        upiId = "apexsteel@upi",
        bankName = "State Bank of India",
        accountHolderName = "Apex Stainless Steel & Engineering Works",
        accountNumber = "40291827364",
        ifscCode = "SBIN0004521",
        branch = "MIDC Bhosari, Pune",
        termsAndConditions = "1. Goods & fabrication works once delivered according to drawing specs cannot be returned.\n2. Tolerance on laser cutting & bending as per DIN ISO 2768-m standards.\n3. Interest @ 18% p.a. applicable on bills overdue beyond 30 days.",
        invoicePrefix = "SS-INV-2026-",
        estimatePrefix = "SS-EST-2026-",
        posPrefix = "SS-JOB-2026-",
        isGstEnabled = true
    )

    val sampleParties = listOf(
        Party(
            id = 1,
            name = "Kirloskar Brothers Plant Project",
            phone = "9822114455",
            email = "procurement@kirloskarplants.com",
            partyType = PartyType.CUSTOMER,
            gstin = "27AAACK1122D1Z8",
            address = "Kirloskar Complex, Kothrud",
            city = "Pune",
            state = "Maharashtra",
            stateCode = "27",
            openingBalance = 0.0,
            currentBalance = 72500.0,
            creditLimit = 250000.0,
            notes = "Continuous buyer of SS lockers, cabinets and industrial furniture"
        ),
        Party(
            id = 2,
            name = "Horizon Architects & Corporate Spaces",
            phone = "9890556677",
            email = "projects@horizonbuilders.in",
            partyType = PartyType.CUSTOMER,
            gstin = "27AAACH4433E1Z1",
            address = "Baner Commercial Complex",
            city = "Pune",
            state = "Maharashtra",
            stateCode = "27",
            openingBalance = 0.0,
            currentBalance = 33000.0,
            creditLimit = 100000.0,
            notes = "SS Office Tables and Glass Railing contracts"
        ),
        Party(
            id = 3,
            name = "PharmaEquip Cleanroom Systems",
            phone = "9765123456",
            email = "orders@pharmaequip.co",
            partyType = PartyType.CUSTOMER,
            gstin = "27AABCP9988G1Z9",
            address = "Chakan Industrial Zone Phase 2",
            city = "Pune",
            state = "Maharashtra",
            stateCode = "27",
            openingBalance = 0.0,
            currentBalance = 0.0,
            creditLimit = 150000.0,
            notes = "Pharma grade SS 304/316 Storage Cabinets and Cleanroom Workstations"
        ),
        Party(
            id = 4,
            name = "Jindal Stainless Steel Stockists Ltd",
            phone = "9811443322",
            email = "sales@jindalstainlessdealers.com",
            partyType = PartyType.SUPPLIER,
            gstin = "27AAACJ8877K1Z3",
            address = "Steel Market, Kalamboli, Navi Mumbai",
            city = "Navi Mumbai",
            state = "Maharashtra",
            stateCode = "27",
            openingBalance = 0.0,
            currentBalance = -25000.0,
            creditLimit = 500000.0,
            notes = "Primary raw material supplier for SS 304/316 2B & No.4 finish sheets"
        ),
        Party(
            id = 5,
            name = "Pawan Steels & Hardware Distributors",
            phone = "9845011223",
            email = "pawan.steels@pipes.in",
            partyType = PartyType.SUPPLIER,
            gstin = "27AABCP3322H1Z6",
            address = "Loha Bhavan, P. D'Mello Road, Mumbai",
            city = "Mumbai",
            state = "Maharashtra",
            stateCode = "27",
            openingBalance = 0.0,
            currentBalance = -24500.0,
            creditLimit = 200000.0,
            notes = "SS pipes, locker handles, numeric locks and hinges supplier"
        )
    )

    val sampleItems = listOf(
        Item(
            id = 1,
            name = "S.S. 304 6-Door Staff Locker Unit",
            itemCode = "SS-LCK-6D-304",
            barcode = "9403200001",
            category = "S.S. Lockers",
            unit = "Pcs",
            salePrice = 24500.0,
            wholesalePrice = 21500.0,
            purchasePrice = 16200.0,
            taxRate = 18.0,
            hsnCode = "9403",
            currentStock = 12.0,
            minStockAlert = 3.0,
            isService = false,
            description = "Heavy duty SS 304 6-door staff locker with individual master keys, ventilation louvers & name card holder"
        ),
        Item(
            id = 2,
            name = "S.S. 304 12-Door Gym / Factory Locker",
            itemCode = "SS-LCK-12D-304",
            barcode = "9403200002",
            category = "S.S. Lockers",
            unit = "Pcs",
            salePrice = 38500.0,
            wholesalePrice = 34000.0,
            purchasePrice = 26500.0,
            taxRate = 18.0,
            hsnCode = "9403",
            currentStock = 6.0,
            minStockAlert = 2.0,
            isService = false,
            description = "12 compartment stainless steel 304 locker with heavy hinges, padlock hasps and mirror satin finish"
        ),
        Item(
            id = 3,
            name = "S.S. 304 Storage Cabinet with Glass Doors",
            itemCode = "SS-CAB-GLS-304",
            barcode = "9403200003",
            category = "S.S. Cabinets",
            unit = "Pcs",
            salePrice = 19800.0,
            wholesalePrice = 17500.0,
            purchasePrice = 13500.0,
            taxRate = 18.0,
            hsnCode = "9403",
            currentStock = 8.0,
            minStockAlert = 2.0,
            isService = false,
            description = "Pharma & Cleanroom grade SS 304 storage almirah with 4 adjustable shelves and toughened glass doors"
        ),
        Item(
            id = 4,
            name = "S.S. 304 Solid Dual Door Industrial Almirah",
            itemCode = "SS-CAB-SLD-304",
            barcode = "9403200004",
            category = "S.S. Cabinets",
            unit = "Pcs",
            salePrice = 22500.0,
            wholesalePrice = 19800.0,
            purchasePrice = 15200.0,
            taxRate = 18.0,
            hsnCode = "9403",
            currentStock = 2.0, // Low stock!
            minStockAlert = 4.0,
            isService = false,
            description = "Full stainless steel 304 heavy gauge cabinet with 3-point locking system for tool & document storage"
        ),
        Item(
            id = 5,
            name = "S.S. 304 Executive Office Table (5ft x 2.5ft)",
            itemCode = "SS-TBL-OFF-5X2",
            barcode = "9403200005",
            category = "S.S. Office Tables",
            unit = "Pcs",
            salePrice = 16500.0,
            wholesalePrice = 14500.0,
            purchasePrice = 10800.0,
            taxRate = 18.0,
            hsnCode = "9403",
            currentStock = 10.0,
            minStockAlert = 3.0,
            isService = false,
            description = "Modern SS 304 square tube frame office desk with 3 lockable drawers and wire grommet pass-through"
        ),
        Item(
            id = 6,
            name = "S.S. 304 Industrial Workstation Table (6ft x 3ft)",
            itemCode = "SS-TBL-IND-6X3",
            barcode = "9403200006",
            category = "S.S. Office Tables",
            unit = "Pcs",
            salePrice = 21000.0,
            wholesalePrice = 18500.0,
            purchasePrice = 14200.0,
            taxRate = 18.0,
            hsnCode = "9403",
            currentStock = 5.0,
            minStockAlert = 2.0,
            isService = false,
            description = "Heavy duty cleanroom / packing workstation table with bottom undershelf and 500 Kg load capacity"
        ),
        Item(
            id = 7,
            name = "SS 304 Sheet 2.0mm (2B Finish)",
            itemCode = "SS-SHT-304-2MM",
            barcode = "7219332001",
            category = "Raw Sheets & Plates",
            unit = "Kg",
            salePrice = 245.0,
            wholesalePrice = 225.0,
            purchasePrice = 185.0,
            taxRate = 18.0,
            hsnCode = "72193320",
            currentStock = 850.0,
            minStockAlert = 200.0,
            isService = false,
            description = "Cold Rolled Stainless Steel Sheet Grade 304, Width 1250mm x 2500mm, 2.0mm thickness"
        ),
        Item(
            id = 8,
            name = "SS Glass Railing Fabrication & Installation",
            itemCode = "SS-FAB-RAIL",
            barcode = "7308909001",
            category = "Custom Fabrication",
            unit = "RFT",
            salePrice = 1650.0,
            wholesalePrice = 1450.0,
            purchasePrice = 950.0,
            taxRate = 18.0,
            hsnCode = "7308",
            currentStock = 500.0,
            minStockAlert = 50.0,
            isService = false,
            description = "Complete SS 304 handrail with heavy glass bracket spigots and 12mm toughened glass mounting"
        ),
        Item(
            id = 9,
            name = "CNC Fiber Laser Cutting Job Work",
            itemCode = "SRV-LSR-CUT",
            barcode = "",
            category = "Job Work Services",
            unit = "Mtr",
            salePrice = 65.0,
            wholesalePrice = 50.0,
            purchasePrice = 0.0,
            taxRate = 18.0,
            hsnCode = "9988",
            currentStock = 9999.0,
            minStockAlert = 0.0,
            isService = true,
            description = "High precision CNC Fiber Laser Cutting for SS sheets up to 16mm with Nitrogen gas assist"
        )
    )

    fun getSampleInvoices(): List<Pair<Invoice, List<InvoiceItem>>> {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        // Sale Invoice 1: 2x SS Lockers for Kirloskar
        val inv1 = Invoice(
            id = 1,
            invoiceNumber = "SS-INV-2026-001",
            invoiceType = InvoiceType.SALE_INVOICE,
            partyId = 1,
            partyName = "Kirloskar Brothers Plant Project",
            partyPhone = "9822114455",
            partyGstin = "27AAACK1122D1Z8",
            partyAddress = "Kirloskar Complex, Kothrud, Pune",
            date = now - 3 * dayMillis,
            dueDate = now + 12 * dayMillis,
            subTotal = 41525.42,
            totalTax = 7474.58,
            cgstAmount = 3737.29,
            sgstAmount = 3737.29,
            igstAmount = 0.0,
            discountAmount = 0.0,
            roundOff = 0.0,
            grandTotal = 49000.0,
            paidAmount = 0.0,
            balanceDue = 49000.0,
            paymentStatus = PaymentStatus.UNPAID,
            paymentMode = PaymentMode.CREDIT,
            isInterState = false,
            notes = "Supply of 2 units S.S. 304 6-Door Staff Lockers for factory changing rooms"
        )
        val items1 = listOf(
            InvoiceItem(
                id = 1,
                invoiceId = 1,
                itemId = 1,
                itemName = "S.S. 304 6-Door Staff Locker Unit",
                hsnCode = "9403",
                quantity = 2.0,
                unit = "Pcs",
                unitPrice = 20762.71,
                taxRate = 18.0,
                taxAmount = 7474.58,
                discountPercent = 0.0,
                totalAmount = 49000.0
            )
        )

        // Sale Invoice 2: 2x SS Office Tables for Horizon Builders
        val inv2 = Invoice(
            id = 2,
            invoiceNumber = "SS-INV-2026-002",
            invoiceType = InvoiceType.SALE_INVOICE,
            partyId = 2,
            partyName = "Horizon Architects & Corporate Spaces",
            partyPhone = "9890556677",
            partyGstin = "27AAACH4433E1Z1",
            partyAddress = "Baner Commercial Complex, Pune",
            date = now - 1 * dayMillis,
            dueDate = now + 14 * dayMillis,
            subTotal = 27966.10,
            totalTax = 5033.90,
            cgstAmount = 2516.95,
            sgstAmount = 2516.95,
            igstAmount = 0.0,
            discountAmount = 0.0,
            roundOff = 0.0,
            grandTotal = 33000.0,
            paidAmount = 0.0,
            balanceDue = 33000.0,
            paymentStatus = PaymentStatus.UNPAID,
            paymentMode = PaymentMode.CREDIT,
            isInterState = false,
            notes = "Supply of 2 units S.S. 304 Executive Office Tables with Drawer Pedestals"
        )
        val items2 = listOf(
            InvoiceItem(
                id = 2,
                invoiceId = 2,
                itemId = 5,
                itemName = "S.S. 304 Executive Office Table (5ft x 2.5ft)",
                hsnCode = "9403",
                quantity = 2.0,
                unit = "Pcs",
                unitPrice = 13983.05,
                taxRate = 18.0,
                taxAmount = 5033.90,
                discountPercent = 0.0,
                totalAmount = 33000.0
            )
        )

        // Purchase Invoice from Jindal Stainless (Bill 1)
        val inv3 = Invoice(
            id = 3,
            invoiceNumber = "PUR-2026-001",
            invoiceType = InvoiceType.PURCHASE_INVOICE,
            partyId = 4,
            partyName = "Jindal Stainless Steel Stockists Ltd",
            partyPhone = "9811443322",
            partyGstin = "27AAACJ8877K1Z3",
            partyAddress = "Steel Market, Kalamboli, Navi Mumbai",
            date = now - 5 * dayMillis,
            dueDate = now + 25 * dayMillis,
            subTotal = 66101.69,
            totalTax = 11898.31,
            cgstAmount = 5949.15,
            sgstAmount = 5949.15,
            igstAmount = 0.0,
            discountAmount = 0.0,
            roundOff = 0.0,
            grandTotal = 78000.0,
            paidAmount = 78000.0,
            balanceDue = 0.0,
            paymentStatus = PaymentStatus.PAID,
            paymentMode = PaymentMode.BANK_TRANSFER,
            isInterState = false,
            notes = "Raw Material Batch: SS 304 2.0mm Coils & Sheets for Locker & Cabinet fabrication"
        )
        val items3 = listOf(
            InvoiceItem(
                id = 3,
                invoiceId = 3,
                itemId = 7,
                itemName = "SS 304 Sheet 2.0mm (2B Finish)",
                hsnCode = "72193320",
                quantity = 357.1,
                unit = "Kg",
                unitPrice = 185.0,
                taxRate = 18.0,
                taxAmount = 11898.31,
                discountPercent = 0.0,
                totalAmount = 78000.0
            )
        )

        // Sale Invoice 3: Second individual invoice for Kirloskar Brothers
        val inv4 = Invoice(
            id = 4,
            invoiceNumber = "SS-INV-2026-003",
            invoiceType = InvoiceType.SALE_INVOICE,
            partyId = 1,
            partyName = "Kirloskar Brothers Plant Project",
            partyPhone = "9822114455",
            partyGstin = "27AAACK1122D1Z8",
            partyAddress = "Kirloskar Complex, Kothrud, Pune",
            date = now - 1 * dayMillis,
            dueDate = now + 14 * dayMillis,
            subTotal = 32627.12,
            totalTax = 5872.88,
            cgstAmount = 2936.44,
            sgstAmount = 2936.44,
            igstAmount = 0.0,
            discountAmount = 0.0,
            roundOff = 0.0,
            grandTotal = 38500.0,
            paidAmount = 15000.0,
            balanceDue = 23500.0,
            paymentStatus = PaymentStatus.PARTIAL,
            paymentMode = PaymentMode.CHEQUE,
            isInterState = false,
            notes = "Supply of 1 unit S.S. 304 12-Door Industrial Locker for Shift B"
        )
        val items4 = listOf(
            InvoiceItem(
                id = 4,
                invoiceId = 4,
                itemId = 2,
                itemName = "S.S. 304 12-Door Gym / Factory Locker",
                hsnCode = "9403",
                quantity = 1.0,
                unit = "Pcs",
                unitPrice = 32627.12,
                taxRate = 18.0,
                taxAmount = 5872.88,
                discountPercent = 0.0,
                totalAmount = 38500.0
            )
        )

        // Purchase Invoice 2: Second individual invoice for Jindal Stainless with Photo Bill & 1-Month Reminder
        val inv5 = Invoice(
            id = 5,
            invoiceNumber = "BILL-JINDAL-9921",
            invoiceType = InvoiceType.PURCHASE_INVOICE,
            partyId = 4,
            partyName = "Jindal Stainless Steel Stockists Ltd",
            partyPhone = "9811443322",
            partyGstin = "27AAACJ8877K1Z3",
            partyAddress = "Steel Market, Kalamboli, Navi Mumbai",
            date = now - 2 * dayMillis,
            dueDate = now + 28 * dayMillis,
            subTotal = 38135.59,
            totalTax = 6864.41,
            cgstAmount = 3432.20,
            sgstAmount = 3432.20,
            igstAmount = 0.0,
            discountAmount = 0.0,
            roundOff = 0.0,
            grandTotal = 45000.0,
            paidAmount = 20000.0,
            balanceDue = 25000.0,
            paymentStatus = PaymentStatus.PARTIAL,
            paymentMode = PaymentMode.BANK_TRANSFER,
            isInterState = false,
            billPhotoUri = "sample_jindal_bill_preview",
            hasPaymentReminder = true,
            reminderDate = now + 28 * dayMillis,
            notes = "Scanned Supplier Bill: SS 304 Plates & laser grade sheets. Custom paid ₹20,000, balance ₹25,000 due in 1 month."
        )
        val items5 = listOf(
            InvoiceItem(
                id = 5,
                invoiceId = 5,
                itemId = 7,
                itemName = "SS 304 Sheet 2.0mm (2B Finish)",
                hsnCode = "72193320",
                quantity = 243.2,
                unit = "Kg",
                unitPrice = 185.0,
                taxRate = 18.0,
                taxAmount = 6864.41,
                discountPercent = 0.0,
                totalAmount = 45000.0
            )
        )

        return listOf(
            inv1 to items1,
            inv2 to items2,
            inv3 to items3,
            inv4 to items4,
            inv5 to items5
        )
    }

    val sampleExpenses = listOf(
        PaymentTransaction(
            id = 1,
            partyId = null,
            partyName = "MIDC Bhosari Industrial Power",
            invoiceId = null,
            invoiceNumber = "",
            type = "EXPENSE",
            amount = 14800.0,
            paymentMode = PaymentMode.BANK_TRANSFER,
            date = System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000L,
            referenceNo = "NEFT-MIDC-PWR",
            notes = "Industrial electricity for Sheet Bending, Shearing & Laser Cutting machine",
            expenseCategory = ExpenseCategory.ELECTRICITY_UTILITIES
        ),
        PaymentTransaction(
            id = 2,
            partyId = null,
            partyName = "Industrial Hardware & Locker Accessories",
            invoiceId = null,
            invoiceNumber = "",
            type = "EXPENSE",
            amount = 4500.0,
            paymentMode = PaymentMode.UPI,
            date = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L,
            referenceNo = "UPI-LOCK-771",
            notes = "50x S.S. Cam Locks & Master Keys for 6-door staff lockers",
            expenseCategory = ExpenseCategory.MAINTENANCE
        )
    )
}
