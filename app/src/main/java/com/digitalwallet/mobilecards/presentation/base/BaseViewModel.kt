package com.digitalwallet.mobilecards.presentation.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {
    protected val context: Context by inject()

    inline fun launch(
        noinline error: ((Throwable) -> Unit)? = null,
        noinline finally: (() -> Unit)? = null,
        crossinline success: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                success()
            } catch (e: Exception) {
                error?.invoke(e)

            } finally {
                finally?.invoke()
            }
        }
    }
}