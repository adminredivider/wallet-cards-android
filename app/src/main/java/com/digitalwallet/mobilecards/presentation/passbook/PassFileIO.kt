package com.digitalwallet.mobilecards.presentation.passbook

import android.content.Context
import com.digitalwallet.mobilecards.domain.model.PassFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.annotation.Single
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.ZipInputStream

@Single
class PassFileIO(private val context: Context) {

    private var passDir: File? = null
    private var previewPassDir: File? = null
    private val passDirName = "app_flutter/passes"
    private val previewPassDirName = "preview_passes"

    private fun generatePassId(): String {
        return UUID.randomUUID().toString()
    }

    private fun createPassesDir(name: String): File {
        val dir = File(context.filesDir.parent, name)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw IOException("Failed to create directory: ${dir.path}")
            }
        }
        return dir
    }

    private fun getPassesDir(): File {
        if (passDir == null) {
            passDir = createPassesDir(passDirName)
        }
        return passDir!!
    }

    private fun getPreviewPassesDir(): File {
        if (previewPassDir == null) {
            previewPassDir = createPassesDir(previewPassDirName)
        }
        return previewPassDir!!
    }

    private fun createPass(passId: String, isPreview: Boolean = false): File {
        val passesDir = if (isPreview) getPreviewPassesDir() else getPassesDir()
        return File(passesDir, "$passId.pkpass").apply {
            if (!exists() && !createNewFile()) {
                throw IOException("Failed to create pass file: ${this.path}")
            }
        }
    }

    private fun unpackPass(passPath: String) {
        val passFile = File(passPath)
        val passDirectory = File(passFile.parent, passFile.nameWithoutExtension)

        if (!passFile.exists()) {
            throw FileNotFoundException("Pass file not found at $passPath")
        }
        if (passDirectory.exists()) return

        passDirectory.mkdirs()
        ZipInputStream(passFile.inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val outputFile = File(passDirectory, entry.name)
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.outputStream().use { output ->
                        zip.copyTo(output)
                    }
                }
                entry = zip.nextEntry
            }
        }
    }

    fun saveFromPath(
        externalPassFile: File,
        onSuccess: (PassFile) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val passId = generatePassId()
            val passesDir = getPassesDir()

            val newFileHash = getFileHash(externalPassFile)

            passesDir.listFiles()?.forEach { existingFile ->
                if (existingFile.isFile) {
                    val existingFileHash = getFileHash(existingFile)
                    if (newFileHash == existingFileHash) {
                        onError(IllegalArgumentException("This file has already been saved."))
                        return
                    }
                }
            }

            if (externalPassFile.exists()) {
                val destinationFile = File(passesDir, "$passId.pkpass")
                externalPassFile.copyTo(destinationFile, overwrite = true)

                val unpackedDir = File(destinationFile.parent, destinationFile.nameWithoutExtension)
                unpackPass(destinationFile.path)

                val passFile = PassParser(passId, unpackedDir, destinationFile).parse(unpackedDir)
                Timber.tag("passFile").e("passFile :: $passFile")
                onSuccess(passFile)
                return
            }

            onError(FileNotFoundException("Unable to fetch pass file at specified path."))
        } catch (e: Exception) {
            onError(e)
        }
    }

    suspend fun saveFromUrl(
        url: String,
        onSuccess: (PassFile?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val dio = OkHttpClient()
        val passId = generatePassId()
        val passesDir = getPassesDir()
        val tempDir = File(passesDir, "temp")
        if (!tempDir.exists()) tempDir.mkdirs()
        val tempFile = File(tempDir, "temp_downloaded.pkpass")
        val passDir = File(passesDir, passId)

        val requestBuilder = Request.Builder().url(url)
        try {
            withContext(Dispatchers.IO) {
                dio.newCall(requestBuilder.build()).execute().use { response ->
                    if (response.isSuccessful) {
                        tempFile.outputStream().use { output ->
                            response.body?.byteStream()?.copyTo(output)
                        }
                        val newFileHash = getFileHash(tempFile)

                        passesDir.listFiles()?.forEach { existingFile ->
                            if (existingFile.isFile) {
                                val existingFileHash = getFileHash(existingFile)
                                if (newFileHash == existingFileHash) {
                                    tempFile.delete()
                                    onSuccess(null)
                                    return@withContext
                                }
                            }
                        }

                        val savedFile = File(passesDir, "$passId.pkpass")
                        tempFile.renameTo(savedFile)
                        unpackPass(savedFile.path)
                        tempDir.deleteRecursively()

                        val passFile = PassParser(passId, savedFile, passDir).parse(passDir)
                        onSuccess(passFile)
                    } else {
                        tempDir.deleteRecursively()
                        onError(IOException("Unable to download pass file from specified URL"))
                    }
                }
            }
        } catch (e: Exception) {
            tempDir.deleteRecursively()
            onError(e)
        }
    }


    fun getFileHash(file: File): String {
        if (!file.isFile) {
            throw IllegalArgumentException("The provided path is not a file: ${file.path}")
        }

        val digest = MessageDigest.getInstance("SHA-256")
        ZipInputStream(file.inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == "pass.json") {
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (zip.read(buffer).also { read = it } != -1) {
                        digest.update(buffer, 0, read)
                    }
                    break
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }


    fun getAllSaved(): List<PassFile> {
        val parsedPasses = mutableListOf<PassFile>()
        val passesDir = getPassesDir()

        passesDir.listFiles()?.filter { it.extension == "pkpass" }?.forEach { passFile ->
            val passId = passFile.nameWithoutExtension
            val passDir = File(passFile.parent, passFile.nameWithoutExtension)

            try {
                unpackPass(passFile.path)
                val parsedPassFile = PassParser(passId, passDir, passFile).parse(passDir)
                parsedPasses.add(parsedPassFile)
            } catch (e: Exception) {
                delete(passDir, passFile)
            }
        }
        return parsedPasses
    }

    fun delete(passDirectory: File, passFile: File) {
        val passbookFile = File("${passFile}.pkpass")
        passbookFile.delete()
        passDirectory.deleteRecursively()
    }
}

