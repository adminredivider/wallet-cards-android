package com.digitalwallet.mobilecards.domain.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)