package io.walletcards.android.data.remote.model

data class FirebaseDevice(
    var apiLevel: String? = null,
    var codeName: String? = null,
    var deviceBrand: String? = null,
    var deviceLanguage: String? = null,
    var deviceModel: String? = null,
    var privateToken: String? = null,
    var publicToken: String? = null,
    var countryCode: String? = null,
    var isPhysicalDevice: String? = null,
    var androidVersion: String? = null,
    var hardware: String? = null,
    var deviceScreenSize: String? = null,
    var screenSizeInches: String? = null,
    var aspectRatio: String? = null,
    var getDeviceFontScale: String? = null,
    var installStatus: Boolean? = null,
    var hasMadePurchase: Boolean? = null,
    var createAt: String? = null,
    var lastUpdated: String? = null,
    var paywallId: String? = null,
    var currencyCode: String? = null,
    var price: String? = null,
    var ip: String? = null
) {

    fun copyWith(
        apiLevel: String? = this.apiLevel,
        codeName: String? = this.codeName,
        deviceBrand: String? = this.deviceBrand,
        deviceLanguage: String? = this.deviceLanguage,
        deviceModel: String? = this.deviceModel,
        privateToken: String? = this.privateToken,
        publicToken: String? = this.publicToken,
        countryCode: String? = this.countryCode,
        isPhysicalDevice: String? = this.isPhysicalDevice,
        androidVersion: String? = this.androidVersion,
        hardware: String? = this.hardware,
        deviceScreenSize: String? = this.deviceScreenSize,
        screenSizeInches: String? = this.screenSizeInches,
        aspectRatio: String? = this.aspectRatio,
        getDeviceFontScale: String? = this.getDeviceFontScale,
        installStatus: Boolean? = this.installStatus,
        hasMadePurchase: Boolean? = this.hasMadePurchase,
        createAt: String? = this.createAt,
        lastUpdated: String? = this.lastUpdated,
        paywallId: String? = this.paywallId,
        currencyCode: String? = this.currencyCode,
        price: String? = this.price,
        ip: String? = this.ip
    ): FirebaseDevice {
        return FirebaseDevice(
            apiLevel, codeName, deviceBrand, deviceLanguage, deviceModel, privateToken, publicToken,
            countryCode, isPhysicalDevice, androidVersion, hardware, deviceScreenSize, screenSizeInches,
            aspectRatio, getDeviceFontScale, installStatus, hasMadePurchase, createAt, lastUpdated,
            paywallId, currencyCode, price, ip
        )
    }

    override fun toString(): String {
        return "FirebaseDevice(apiLevel=$apiLevel, codeName=$codeName, deviceBrand=$deviceBrand, " +
                "deviceLanguage=$deviceLanguage, deviceModel=$deviceModel, privateToken=$privateToken, " +
                "publicToken=$publicToken, countryCode=$countryCode, isPhysicalDevice=$isPhysicalDevice, " +
                "androidVersion=$androidVersion, hardware=$hardware, deviceScreenSize=$deviceScreenSize, " +
                "screenSizeInches=$screenSizeInches, aspectRatio=$aspectRatio, " +
                "getDeviceFontScale=$getDeviceFontScale, installStatus=$installStatus, " +
                "hasMadePurchase=$hasMadePurchase, createAt=$createAt, lastUpdated=$lastUpdated, " +
                "paywallId=$paywallId, currencyCode=$currencyCode, price=$price, ip=$ip)"
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "apiLevel" to apiLevel,
            "codeName" to codeName,
            "deviceBrand" to deviceBrand,
            "deviceLanguage" to deviceLanguage,
            "deviceModel" to deviceModel,
            "privateToken" to privateToken,
            "publicToken" to publicToken,
            "countryCode" to countryCode,
            "isPhysicalDevice" to isPhysicalDevice,
            "androidVersion" to androidVersion,
            "hardware" to hardware,
            "deviceScreenSize" to deviceScreenSize,
            "screenSizeInches" to screenSizeInches,
            "aspectRatio" to aspectRatio,
            "getDeviceFontScale" to getDeviceFontScale,
            "installStatus" to installStatus,
            "hasMadePurchase" to hasMadePurchase,
            "createAt" to createAt,
            "lastUpdated" to lastUpdated,
            "paywallId" to paywallId,
            "currencyCode" to currencyCode,
            "price" to price,
            "ip" to ip
        )
    }

    fun toFirebaseJson(): Map<String, Any?> {
        return mapOf(
            "apiLevel" to apiLevel,
            "codeName" to codeName,
            "deviceBrand" to deviceBrand,
            "deviceLanguage" to deviceLanguage,
            "deviceModel" to deviceModel,
            "privateToken" to privateToken,
            "publicToken" to publicToken,
            "installStatus" to installStatus,
            "hasMadePurchase" to hasMadePurchase,
            "createAt" to createAt,
            "lastUpdated" to lastUpdated
        )
    }

    companion object {
        fun fromJson(map: Map<String, Any?>): FirebaseDevice {
            return FirebaseDevice(
                apiLevel = map["apiLevel"] as? String,
                codeName = map["codeName"] as? String,
                deviceBrand = map["deviceBrand"] as? String,
                deviceLanguage = map["deviceLanguage"] as? String,
                deviceModel = map["deviceModel"] as? String,
                privateToken = map["privateToken"] as? String,
                publicToken = map["publicToken"] as? String,
                installStatus = map["installStatus"] as? Boolean,
                hasMadePurchase = map["hasMadePurchase"] as? Boolean,
                createAt = (map["createAt"] as? String),
                lastUpdated = (map["lastUpdated"] as? String)
            )
        }
    }
}
