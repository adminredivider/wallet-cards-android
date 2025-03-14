@file:Suppress("Unchecked_Cast")

package com.digitalwallet.mobilecards.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
//import com.digitalwallet.mobilecards.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class PreferencesManager(context: Context) {
    companion object {
        fun create(context: Context) = PreferencesManager(context)

        private const val PREF_NAME = "preferences"
        private const val ENCRYPTED_PREF_NAME = "preferences-encrypted"
        private const val MODE = Context.MODE_PRIVATE
    }

    private val preferences =
//        if (BuildConfig.DEBUG) {
//        context.getSharedPreferences(PREF_NAME, MODE)
//    } else {
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREF_NAME,
            getMasterKey(context),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
//    }

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private inline fun SharedPreferences.edit(
        operation: (SharedPreferences.Editor) -> Unit,
    ) = edit().apply(operation).apply()

    fun set(key: String, value: Any?) {
        preferences.edit {
            when (value) {
                is String -> it.putString(key, value)
                is Int -> it.putInt(key, value)
                is Long -> it.putLong(key, value)
                is Float -> it.putFloat(key, value)
                is Boolean -> it.putBoolean(key, value)
            }
        }
    }

    fun <T> get(key: String, default: T?): T? = preferences.run {
        when (default) {
            is Boolean -> getBoolean(key, default)
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Long -> getLong(key, default)
            is Float -> getFloat(key, default)
            else -> run {
                Timber.e("Type $default is not supported")
                null
            }
        } as? T
    }

    suspend fun <T> observeAsState(
        key: String,
        default: T?,
        scope: CoroutineScope,
    ): StateFlow<T?> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, updatedKey ->
                if (key == updatedKey) {
                    val newValue = get<T>(key, default)
                    trySend(newValue)
                }
            }
            preferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }.stateIn(scope)

    fun remove(key: String) = preferences.edit { it.remove(key) }

    fun contains(key: String): Boolean = preferences.contains(key)

    fun clear() = preferences.edit { it.clear() }
}
