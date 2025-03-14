package io.walletcards.android.data.local.preferences

import android.content.Context
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class Preferences(context: Context) : KoinComponent {

    private val preferencesManager = PreferencesManager.create(context)

    fun writeString(key: String, value: String) = preferencesManager.set(key, value)

    fun writeBoolean(key: String, value: Boolean) = preferencesManager.set(key, value)

    fun readString(key: String, default: String? = ""): String =
        preferencesManager.get(key, default) ?: ""

    fun readBoolean(key: String): Boolean = preferencesManager.get(key, false) ?: false

    fun clear() = preferencesManager.clear()

    object Key {
        const val IS_TUTORIAL_WATCHED = "is_tutorial_watched"
    }
}
