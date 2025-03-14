package io.walletcards.android.domain.model

enum class PassType(val typeString: String) {
    BOARDING("BOARDING"),
    TICKET("TICKET"),
    COUPON("COUPON"),
    GENERIC("GENERIC"),
    STORECARD("STORECARD");

    companion object {
        fun fromString(type: String?): PassType {
            return entries.find { it.typeString == type } ?: GENERIC
        }
    }
}
