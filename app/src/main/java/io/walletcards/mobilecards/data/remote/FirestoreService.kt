package io.walletcards.mobilecards.data.remote

class FirestoreService {

    /*private val postsCollectionReference = Firebase.firestore
        .collection(if (AppFlavor.isDev()) FirebaseConstants.FIRESTORE_PATH_DEV else FirebaseConstants.FIRESTORE_PATH)

    private val passesCollectionReference = Firebase.firestore.collection(FirebaseConstants.PASSES_PATH)
    private val blockedPassesCollectionReference = Firebase.firestore.collection(FirebaseConstants.BLOCKED_PASS)
    private val blockedTeamCollectionReference = Firebase.firestore.collection(FirebaseConstants.BLOCKED_TEAM)
    private val firebaseConfigurationsCollectionReference = Firebase.firestore.collection(FirebaseConstants.CONFIGURATION)
    private val countryTiersCollectionReference = Firebase.firestore.collection(FirebaseConstants.COUNTRY_TIERS)
    private val countryTiersCollectionReferenceWithAds = Firebase.firestore.collection(FirebaseConstants.COUNTRY_TIERS_V5)
    private val paywallsCollectionReference = Firebase.firestore.collection(FirebaseConstants.PAYWALLS_CONFIGS)

    var firebaseConfigurations: FirebaseConfigurations? = null
    var firebaseDevice: FirebaseDevice? = null
    var notificationMessage: String? = null

    suspend fun init() {
        FirebaseAuth.getInstance().signInAnonymously().await()
        getFirebaseConfigurations()
    }

    suspend fun addDevice(device: FirebaseDevice): Boolean {
        return try {
            postsCollectionReference.document(device.publicToken)
                .set(device.toFirebaseJson())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getDevice(deviceId: String): FirebaseDevice? {
        val device = postsCollectionReference.document(deviceId).get().await()
        return if (device.exists()) {
            firebaseDevice = FirebaseDevice.fromJson(device.data as Map<String, Any>)
            firebaseDevice
        } else {
            null
        }
    }

    suspend fun isBlockedPassesByPassTypeIdentifier(passTypeIdentifier: String): Boolean {
        return try {
            val result = blockedPassesCollectionReference
                .whereEqualTo("PassTypeIdentifier", passTypeIdentifier)
                .get()
                .await()
            result.documents.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isBlockedPassesByTeamIdentifier(teamIdentifier: String): Boolean {
        return try {
            val result = blockedTeamCollectionReference
                .whereEqualTo("TeamIdentifier", teamIdentifier)
                .get()
                .await()
            result.documents.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFirebaseConfigurations(): FirebaseConfigurations? {
        val config = firebaseConfigurationsCollectionReference.document("default").get().await()
        return if (config.exists()) {
            firebaseConfigurations = FirebaseConfigurations.fromJson(config.data as Map<String, Any>)
            firebaseConfigurations
        } else {
            null
        }
    }

    suspend fun getTier(countryCode: String?, isAds: Boolean = false): String? {
        if (countryCode == null) return null
        return try {
            val collection = if (isAds) countryTiersCollectionReferenceWithAds else countryTiersCollectionReference
            val result = collection.whereArrayContains("Tier", countryCode).get().await()
            result.documents.firstOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getBanners(): List<FirebaseBanner> {
        val banners = mutableListOf<FirebaseBanner>()
        Firebase.firestore.collection("banners").orderBy("order").get().await().documents.forEach { doc ->
            val banner = FirebaseBanner.fromJson(doc.data as Map<String, Any>)
            if (banner.isActive) banners.add(banner)
        }
        return banners
    }

    suspend fun deleteDevice(deviceId: String) {
        postsCollectionReference.document(deviceId).delete().await()
    }*/
}
