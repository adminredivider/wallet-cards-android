package com.digitalwallet.mobilecards.domain.model

data class PassJson(
    val description: String? = null,
    val formatVersion: Int? = null,
    val organizationName: String? = null,
    val passTypeIdentifier: String? = null,
    val serialNumber: String? = null,
    val teamIdentifier: String? = null,
    val webServiceURL: String? = null,

    val authenticationToken: String? = null,
    val barcode: Barcode? = null,
    val barcodes: List<Barcode>? = null,
    val backgroundColor: String? = null,
    val foregroundColor: String? = null,
    val labelColor: String? = null,
    val groupingIdentifier: String? = null,
    val logoText: String? = null,
    val suppressStripShine: Boolean? = null,
    val storeCard: PassStructureDictionary? = null,
    val generic: PassStructureDictionary? = null,
    val eventTicket: PassStructureDictionary? = null,
    val coupon: PassStructureDictionary? = null,
    val boardingPass: PassStructureDictionary? = null,
    val locations: List<Location>? = null,
    val beacons: List<Beacon>? = null,
    val maxDistance: Int? = null,
    val relevantDate: String? = null,
    val associatedStoreIdentifiers: List<Int>? = null,
    val appLaunchURL: String? = null,
    val expirationDate: String? = null,
    val voided: Boolean? = null,
    val sharingProhibited: Boolean? = null
)

data class Barcode(
    val altText: String? = null,
    val format: String? = null,
    val message: String? = null,

    val messageEncoding: String? = null
)

data class Location(
    val altitude: Double? = null,
    val latitude: Double,
    val longitude: Double,
    val relevantText: String? = null
)

data class Beacon(
    val minor: Int? = null,
    val major: Int? = null,
    val proximityUUID: String? = null,
    val relevantText: String? = null
)

data class PassStructureDictionary(
    val auxiliaryFields: List<Fields>? = null,
    var headerFields: List<Fields>? = null,
    val secondaryFields: List<Fields>? = null,
    val backFields: List<Fields>? = null,
    val primaryFields: List<Fields>? = null,
    val transitType: String? = null
)

data class Fields(
    val key: String? = null,
    val label: String? = null,
    val value: String? = null,
    val currencyCode: String? = null,
    val changeMessage: String? = null,
    val textAlignment: String? = null
)
