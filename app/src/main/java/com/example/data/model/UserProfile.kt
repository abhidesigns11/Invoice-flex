package com.example.data.model

/**
 * Per-user business profile, stored in Firestore at: users/{uid}
 * This is the source of truth collected during onboarding (the Q&A setup)
 * and is what makes each logged-in account's setup fully its own.
 */
data class UserProfile(
    val uid: String = "",
    val ownerName: String = "",
    val gender: String = "",
    val companyName: String = "",
    val logoUrl: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val gstin: String = "",
    val isGstEnabled: Boolean = false,
    val bankName: String = "",
    val accountHolderName: String = "",
    val accountNumber: String = "",
    val ifscCode: String = "",
    val branch: String = "",
    val upiId: String = "",
    val onboardingComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor required for Firestore's automatic deserialization.
    constructor() : this(uid = "")
}
