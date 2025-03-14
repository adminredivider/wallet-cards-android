// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'credit_card.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class CCardAdapter extends TypeAdapter<CCard> {
  @override
  final int typeId = 0;

  @override
  CCard read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return CCard(
      cardNumber: fields[0] as String,
      cardExpiry: fields[1] as String,
      cardHolderName: fields[2] as String,
      cvv: fields[3] as String,
      bankName: fields[4] as String,
      cardType: fields[5] as CardType,
      cardColor: fields[6] as String,
    );
  }

  @override
  void write(BinaryWriter writer, CCard obj) {
    writer
      ..writeByte(7)
      ..writeByte(0)
      ..write(obj.cardNumber)
      ..writeByte(1)
      ..write(obj.cardExpiry)
      ..writeByte(2)
      ..write(obj.cardHolderName)
      ..writeByte(3)
      ..write(obj.cvv)
      ..writeByte(4)
      ..write(obj.bankName)
      ..writeByte(5)
      ..write(obj.cardType)
      ..writeByte(6)
      ..write(obj.cardColor);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is CCardAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}

class CardTypeAdapter extends TypeAdapter<CardType> {
  @override
  final int typeId = 1;

  @override
  CardType read(BinaryReader reader) {
    switch (reader.readByte()) {
      case 0:
        return CardType.americanExpress;
      case 1:
        return CardType.dinersClub;
      case 2:
        return CardType.discover;
      case 3:
        return CardType.jcb;
      case 4:
        return CardType.masterCard;
      case 5:
        return CardType.maestro;
      case 6:
        return CardType.rupay;
      case 7:
        return CardType.visa;
      case 8:
        return CardType.elo;
      case 9:
        return CardType.other;
      default:
        return CardType.americanExpress;
    }
  }

  @override
  void write(BinaryWriter writer, CardType obj) {
    switch (obj) {
      case CardType.americanExpress:
        writer.writeByte(0);
        break;
      case CardType.dinersClub:
        writer.writeByte(1);
        break;
      case CardType.discover:
        writer.writeByte(2);
        break;
      case CardType.jcb:
        writer.writeByte(3);
        break;
      case CardType.masterCard:
        writer.writeByte(4);
        break;
      case CardType.maestro:
        writer.writeByte(5);
        break;
      case CardType.rupay:
        writer.writeByte(6);
        break;
      case CardType.visa:
        writer.writeByte(7);
        break;
      case CardType.elo:
        writer.writeByte(8);
        break;
      case CardType.other:
        writer.writeByte(9);
        break;
    }
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is CardTypeAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
