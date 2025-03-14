package io.walletcards.android.domain.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)