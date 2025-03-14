package io.walletcards.mobilecards.domain.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)