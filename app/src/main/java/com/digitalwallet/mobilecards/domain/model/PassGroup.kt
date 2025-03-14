package com.digitalwallet.mobilecards.domain.model

data class PassGroup(
    var uniqueId: String? = null,
    val passType: PassType = PassType.GENERIC,
    var passList: MutableList<PassFile> = mutableListOf()
)