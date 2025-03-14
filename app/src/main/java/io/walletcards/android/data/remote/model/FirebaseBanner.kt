package io.walletcards.android.data.remote.model

data class FirebaseBanner(
    val description: String? = null,
    val buttonText: String? = null,
    val imageUrl: String? = null,
    val link: String? = null,
    val isActive: Boolean? = null,
    val order: Int? = null
) {
    fun copyWith(
        description: String? = this.description,
        buttonText: String? = this.buttonText,
        imageUrl: String? = this.imageUrl,
        link: String? = this.link,
        isActive: Boolean? = this.isActive,
        order: Int? = this.order
    ): FirebaseBanner {
        return FirebaseBanner(description, buttonText, imageUrl, link, isActive, order)
    }

    companion object {
        fun fromJson(map: Map<String, Any?>): FirebaseBanner {
            return FirebaseBanner(
                description = map["description"] as? String,
                buttonText = map["buttonText"] as? String,
                imageUrl = map["imageUrl"] as? String,
                link = map["link"] as? String,
                isActive = map["isActive"] as? Boolean,
                order = (map["order"] as? Number)?.toInt()
            )
        }
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "description" to description,
            "buttonText" to buttonText,
            "imageUrl" to imageUrl,
            "link" to link,
            "isActive" to isActive,
            "order" to order
        )
    }
}
