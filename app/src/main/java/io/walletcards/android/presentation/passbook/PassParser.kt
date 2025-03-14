package io.walletcards.android.presentation.passbook

import com.google.gson.Gson
import io.walletcards.android.domain.model.PassFile
import io.walletcards.android.domain.model.PassImage
import io.walletcards.android.domain.model.PassJson
import timber.log.Timber
import java.io.File

class PassParser(
    private val passId: String,
    private val passFile: File,
    private val unpackedPassDirectory: File
) {

    private val gson = Gson()

    private fun parsePassJson(unpackedPassDirectory: File): PassJson {
        val passJsonFile = File(unpackedPassDirectory, "pass.json")

        if (!passJsonFile.exists()) {
            throw Exception("Pass file is bad! Not find pass.json in pass file! Path: ${passJsonFile.path}")
        }

        val passJson = passJsonFile.readText()
        Timber.tag("parsePassJson").e("parsePassJson :: $passJson")
        val formatedData = gson.fromJson(passJson, PassJson::class.java)
        Timber.tag("formatedData").e("formatedPassJsonData :: $formatedData")
        return formatedData
    }

    private fun parsePKPassJson(): PassJson {
        if (!passFile.exists()) {
            throw Exception("Pass file is bad! Not find pass.json in pass file!")
        }

        val passJson = passFile.readText()
        return gson.fromJson(passJson, PassJson::class.java)
    }

    private fun getImage(name: String): PassImage {
        val image = File("${passFile.path}/$name.png")
        val image2x = File("${passFile.path}/$name@2x.png")
        val image3x = File("${passFile.path}/$name@3x.png")

        return PassImage(
            image = if (image.exists()) image else null,
            image2x = if (image2x.exists()) image2x else null,
            image3x = if (image3x.exists()) image3x else null
        )
    }

    fun parse(unpackedPassDirectory: File): PassFile {
        val passJson = parsePassJson(unpackedPassDirectory)
        Timber.tag("passJson").e("passJson :: $passJson")

        val logo = getImage("logo")
        val background = getImage("background")
        val footer = getImage("footer")
        val strip = getImage("strip")
        val icon = getImage("icon")
        val thumbnail = getImage("thumbnail")

        return PassFile(
            id = passId,
            file = passFile,
            directory = unpackedPassDirectory,
            logo = logo,
            background = background,
            footer = footer,
            strip = strip,
            icon = icon,
            thumbnail = thumbnail,
            pass = passJson
        )
    }

    fun parsePkPass(): PassFile {
        val passJson = parsePKPassJson()

        val logo = getImage("logo")
        val background = getImage("background")
        val footer = getImage("footer")
        val strip = getImage("strip")
        val icon = getImage("icon")
        val thumbnail = getImage("thumbnail")

        return PassFile(
            id = passId,
            file = passFile,
            directory = unpackedPassDirectory,
            pass = passJson,
            logo = logo,
            background = background,
            footer = footer,
            strip = strip,
            icon = icon,
            thumbnail = thumbnail
        )
    }
}

