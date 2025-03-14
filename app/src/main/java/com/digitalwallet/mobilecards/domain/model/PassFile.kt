package com.digitalwallet.mobilecards.domain.model

import java.io.File

data class PassFile(
    val id: String?,
    val file: File,
    val directory: File,
    val background: PassImage? = null,
    val footer: PassImage? = null,
    val icon: PassImage? = null,
    val logo: PassImage? = null,
    val strip: PassImage? = null,
    val thumbnail: PassImage? = null,
    val pass: PassJson,
)

data class PassImage(
    val image: File? = null,
    val image2x: File? = null,
    val image3x: File? = null,
)

