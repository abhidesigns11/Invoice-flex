package com.example.data.db

data class HsnRecord(
    val code: String,
    val chapter: String = "Iron and steel",
    val description: String,
    val gstRate: Double = 18.0,
    val type: String = "Goods", // Goods or Services
    val whenToUse: String = "", // Practical scenario guidance for fabricators
    val keywords: String = "",
    val isStainlessSteelSpecific: Boolean = true
)

object HsnDatabase {
    val hsnList = listOf(
        // --- 9403: Furniture & Lockers / Cabinets / Tables ---
        HsnRecord(
            code = "9403",
            chapter = "Furniture and parts thereof",
            description = "Other furniture and parts thereof - S.S. Lockers, Storage Cabinets, Office Tables, Workstations, Almirahs & Industrial Racks",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Selling fabricated stainless steel finished products like S.S. Lockers (staff/gym), Storage Cabinets (solid/glass door), Executive Office Tables, Cleanroom Workstations & Pharma Almirahs.",
            keywords = "locker, cabinet, office table, table, desk, almirah, cupboard, rack, workstation, stainless steel furniture, gym locker, staff locker, pharma cabinet, 9403, 940320"
        ),
        HsnRecord(
            code = "940320",
            chapter = "Furniture and parts thereof",
            description = "Other Metal Furniture - S.S. Staff Lockers (6/12 Door), Industrial Office Desks & Tool Cabinets",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Specifying exact metal furniture code on tax invoices for SS lockers, desks and industrial storage units. Standard GST rate is 18%.",
            keywords = "940320, 94032000, metal furniture, ss locker, ss cabinet, ss office table, filing cabinet"
        ),
        HsnRecord(
            code = "9402",
            chapter = "Medical, surgical, dental furniture",
            description = "Medical, surgical, dental or veterinary furniture - S.S. Hospital Examination Tables, Pharma Trolleys & Cleanroom Furniture",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Supplying customized stainless steel furniture to hospitals, clinics, cleanrooms, and pharmaceutical labs (e.g. SS scrub sinks, instrument trolleys, operation theatre tables).",
            keywords = "hospital table, cleanroom furniture, pharma trolley, medical cabinet, scrub sink, 9402"
        ),

        // --- 9988: Fabrication & Job Work Services ---
        HsnRecord(
            code = "9988",
            chapter = "Job Work & Manufacturing Services",
            description = "Manufacturing services on physical inputs owned by others - S.S. Fabrication Job Work, Laser Cutting, CNC Bending & Argon TIG Welding",
            gstRate = 18.0,
            type = "Services",
            whenToUse = "USE THIS WHEN: Billing customer purely for labor, machining, laser cutting, CNC sheet bending, Argon TIG welding, buffing, or custom fabrication when raw material is provided by customer.",
            keywords = "job work, laser cutting, welding, bending, cutting, fabrication service, cnc bending, labor, argon welding, buffing, polishing, powder coating, 9988, 998873"
        ),
        HsnRecord(
            code = "998873",
            chapter = "Job Work & Manufacturing Services",
            description = "Metal fabrication services, welding, laser cutting and machining services on steel and stainless steel",
            gstRate = 18.0,
            type = "Services",
            whenToUse = "USE THIS WHEN: Issuing Tax Invoice for stainless steel sheet metal processing, shearing, laser profile cutting, and assembly job work (18% GST).",
            keywords = "998873, metal fabrication, laser cutting service, welding job, bending charges"
        ),

        // --- 7308: Structural Steel & Railings ---
        HsnRecord(
            code = "7308",
            chapter = "Articles of iron or steel",
            description = "Structures and parts of structures of iron or steel - S.S. Railings, Balcony Glass Spigots, Canopy Frames, Gates & Structural Frameworks",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Fabricating and supplying stainless steel architectural structures, heavy staircase railings, glass balustrades with SS 304/316 spigots, entrance canopies, and industrial platforms.",
            keywords = "railing, balustrade, spigot, canopy, gate, structural frame, staircase railing, architectural ss, glass railing, 7308, 730890"
        ),
        HsnRecord(
            code = "730890",
            chapter = "Articles of iron or steel",
            description = "Other structures and parts of structures of iron/steel - Custom fabricated S.S. structures, panels, frames and pillars",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Billing custom architectural stainless steel structural works, facade cladding frames, industrial sheds, and support framework.",
            keywords = "730890, 73089090, ss structure, frame, support pillar, bracket structure"
        ),

        // --- 7326: Other Articles of Stainless Steel ---
        HsnRecord(
            code = "7326",
            chapter = "Articles of iron or steel",
            description = "Other articles of iron or steel - S.S. Custom Brackets, Enclosures, Trays, Drain Channels, Clamps, Laser Cut Components & Handrails",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Billing miscellaneous finished stainless steel fabricated items like SS electrical enclosures, drainage trenches/gratings, custom mounting brackets, tool trays, and SS hardware.",
            keywords = "enclosure, bracket, tray, drain channel, clamp, custom article, sheet metal box, laser cut part, 7326, 732690"
        ),
        HsnRecord(
            code = "73269099",
            chapter = "Articles of iron or steel",
            description = "All other articles of stainless steel, forged or punched or fabricated not elsewhere specified",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: General fabricated stainless steel items (baskets, hooks, flanges, laser cut signs, machine guards) that do not have a dedicated specific HSN code.",
            keywords = "73269099, general ss article, custom fabrication, machine guard, ss basket"
        ),

        // --- 7309 & 7310: SS Storage Tanks & Vessels ---
        HsnRecord(
            code = "7309",
            chapter = "Articles of iron or steel",
            description = "Reservoirs, tanks, vats and similar containers of iron or steel for any material (capacity > 300 Litres)",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Fabricating large industrial S.S. 304/316 water storage tanks, chemical vessels, dairy vats, and mixing reactors with capacity exceeding 300 Litres.",
            keywords = "tank, 500l tank, 1000l tank, water tank, chemical tank, reactor, mixing vessel, vat, 7309"
        ),
        HsnRecord(
            code = "7310",
            chapter = "Articles of iron or steel",
            description = "Tanks, casks, drums, cans, boxes and similar containers of iron or steel (capacity < 300 Litres)",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Fabricating compact stainless steel tanks, milk cans, drum containers, solvent drums, and portable liquid storage units under 300 Litres.",
            keywords = "small tank, drum, milk can, solvent container, box container, 7310, 731029"
        ),

        // --- 7306: SS Pipes & Tubes ---
        HsnRecord(
            code = "7306",
            chapter = "Articles of iron or steel",
            description = "Other tubes, pipes and hollow profiles - S.S. 304/316 Round Pipes, Square Tubes, Slotted Railing Pipes & Rectangular Box Sections",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Supplying or trading stainless steel welded tubes, ornamental pipes, slotted railing tubes, and hollow square sections by meter or per kg.",
            keywords = "pipe, tube, hollow section, square tube, round pipe, slotted pipe, ss pipe, 7306, 730640"
        ),

        // --- 7219 / 7220: Raw Stainless Steel Sheets & Plates ---
        HsnRecord(
            code = "7219",
            chapter = "Iron and steel",
            description = "Flat-Rolled Products Of Stainless Steel (Width >= 600 mm) - S.S. 304, 316, 202 Sheets & Plates (Cold Rolled / Hot Rolled 2B/No.4/Mirror Finish)",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Buying or selling raw stainless steel sheets, coils, and plates (2B finish, Matt finish, Mirror finish, Hairline) charged per Kilogram (Kg).",
            keywords = "sheet, plate, coil, ss sheet, ss 304 sheet, 316 sheet, 202 sheet, 2b finish, cold rolled sheet, 7219, 721934"
        ),
        HsnRecord(
            code = "72193490",
            chapter = "Iron and steel",
            description = "Cold-rolled S.S. Sheets of thickness 0.5 mm to 1.0 mm (20 Gauge / 22 Gauge)",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Invoicing thin gauge stainless steel sheets used for locker doors, cabinet bodies, and cladding panels.",
            keywords = "72193490, thin sheet, 20 gauge, 22 gauge, locker sheet"
        ),
        HsnRecord(
            code = "72193320",
            chapter = "Iron and steel",
            description = "Cold-rolled S.S. 304/316 Sheets of thickness 1 mm to 3 mm (16 Gauge / 18 Gauge) - Austenitic Type",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Invoicing standard 16G (1.5mm) & 18G (1.2mm) SS 304 sheets for heavy office table tops, almirah frames, and tank shells.",
            keywords = "72193320, 16 gauge, 18 gauge, ss 304 sheet, table top sheet"
        ),
        HsnRecord(
            code = "7220",
            chapter = "Iron and steel",
            description = "Flat-rolled products of stainless steel, of a width of less than 600 mm - S.S. Strips & Slit Coils",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Invoicing narrow SS strips, edge banding trims, and slit coils used for fabrication.",
            keywords = "ss strip, narrow sheet, slit coil, 7220"
        ),

        // --- 8428 / 8431: Material Handling & Conveyors ---
        HsnRecord(
            code = "8428",
            chapter = "Machinery and mechanical appliances",
            description = "Lifting, handling, loading or unloading machinery - S.S. Roller Conveyors, Belt Conveyors & Cleanroom Material Handling Systems",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Fabricating stainless steel motorized or gravity roller conveyors, pharma belt conveyors, and lifting platforms for food/pharma plants.",
            keywords = "conveyor, roller conveyor, material handling, lifting table, pharma conveyor, 8428"
        ),
        HsnRecord(
            code = "8419",
            chapter = "Machinery and mechanical appliances",
            description = "Machinery and plant for treatment of materials by a process involving heating or cooling - S.S. Jacketed Vessels, Autoclaves & Heat Exchangers",
            gstRate = 18.0,
            type = "Goods",
            whenToUse = "USE THIS WHEN: Fabricating industrial S.S. jacketed heating/cooling vessels, pasteurizers, and chemical reactor tanks with internal coils.",
            keywords = "jacketed tank, reactor, heating vessel, heat exchanger, autoclave, 8419"
        )
    )

    fun search(query: String): List<HsnRecord> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return hsnList

        return hsnList.filter { record ->
            record.code.lowercase().contains(q) ||
            record.description.lowercase().contains(q) ||
            record.whenToUse.lowercase().contains(q) ||
            record.keywords.lowercase().contains(q) ||
            record.chapter.lowercase().contains(q)
        }
    }
}
