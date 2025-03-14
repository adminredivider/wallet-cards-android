package io.walletcards.mobilecards.domain.model


data class CardDesignModel(
    val name : String? = null,
    val isFree : Boolean,
    val price : String? = null,
    val thumbnailResId : Int
)


