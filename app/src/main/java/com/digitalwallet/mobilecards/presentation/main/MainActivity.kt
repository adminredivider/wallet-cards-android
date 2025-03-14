package com.digitalwallet.mobilecards.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.embedding.engine.dart.DartExecutor
//import io.flutter.plugin.common.MethodChannel
import com.digitalwallet.mobilecards.databinding.ActivityMainBinding
import com.digitalwallet.mobilecards.domain.model.Card
import com.digitalwallet.mobilecards.domain.model.CardType
import com.digitalwallet.mobilecards.domain.model.CardTypeDeserializer
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        val databaseFile = File("${filesDir.parent}/app_flutter/creditcards.hive")
        Timber.tag("MainActivity").d("Database file exists: ${databaseFile.exists()}")
//        if (databaseFile.exists()) {
//            val flutterEngine = FlutterEngine(this)
//            flutterEngine.dartExecutor.executeDartEntrypoint(
//                DartExecutor.DartEntrypoint.createDefault()
//            )
//
//            val methodChannel = MethodChannel(
//                flutterEngine.dartExecutor.binaryMessenger,
//                "com.example.channel"
//            )
//            val hivePath = "${filesDir.parent}/app_flutter"
//            methodChannel.invokeMethod("exportHiveToJson", hivePath, object : MethodChannel.Result {
//                override fun success(result: Any?) {
//                    val jsonResult = result as String
//                    val gson = GsonBuilder()
//                        .registerTypeAdapter(CardType::class.java, CardTypeDeserializer())
//                        .create()
//                    val cardListType = object : TypeToken<List<Card>>() {}.type
//                    val cards: List<Card> = gson.fromJson(jsonResult, cardListType)
//                    Timber.tag("MainActivity").d("listOfCard: $cards")
//                    for (card in cards) {
//                        mainViewModel.action(
//                            MainScreenAction.AddCreditCardAction(card)
//                        )
//                    }
//
//                    if (databaseFile.exists()) {
//                        databaseFile.delete()
//                        Timber.tag("Flutter").d("Database deleted after export.")
//                    }
//                    Timber.tag("Flutter").d("JSON Result from Flutter: $jsonResult")
//                }
//
//                override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
//                    Timber.tag("Flutter").e("Error: $errorCode, $errorMessage")
//                }
//
//                override fun notImplemented() {
//                    Timber.tag("Flutter").e("Method not implemented in Flutter")
//                }
//            })
//        }
    }
}