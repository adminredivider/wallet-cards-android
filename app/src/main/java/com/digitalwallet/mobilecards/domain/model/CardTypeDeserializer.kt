package com.digitalwallet.mobilecards.domain.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CardTypeDeserializer : JsonDeserializer<CardType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CardType {
        val value = json?.asString
        return CardType.values().find { it.name == value } ?: CardType.OTHER
    }
}