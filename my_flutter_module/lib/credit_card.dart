import 'package:hive/hive.dart';
part 'credit_card.g.dart';

@HiveType(typeId: 0)
class CCard {
  @HiveField(0)
  String cardNumber;
  @HiveField(1)
  String cardExpiry;
  @HiveField(2)
  String cardHolderName;
  @HiveField(3)
  String cvv;
  @HiveField(4)
  String bankName;
  @HiveField(5)
  CardType cardType;
  @HiveField(6)
  String cardColor;

  CCard({
    required this.cardNumber,
    required this.cardExpiry,
    required this.cardHolderName,
    required this.cvv,
    required this.bankName,
    required this.cardType,
    required this.cardColor,
  });

  CCard copyWith({
    String? cardNumber,
    String? cardExpiry,
    String? cardHolderName,
    String? cvv,
    String? bankName,
    CardType? cardType,
    String? cardColor,
  }) {
    return CCard(
      cardNumber: cardNumber ?? this.cardNumber,
      cardExpiry: cardExpiry ?? this.cardExpiry,
      cardHolderName: cardHolderName ?? this.cardHolderName,
      cvv: cvv ?? this.cvv,
      bankName: bankName ?? this.bankName,
      cardType: cardType ?? this.cardType,
      cardColor: cardColor ?? this.cardColor,
    );
  }
}

@HiveType(typeId: 1)
enum CardType {
  @HiveField(0)
  americanExpress,
  @HiveField(1)
  dinersClub,
  @HiveField(2)
  discover,
  @HiveField(3)
  jcb,
  @HiveField(4)
  masterCard,
  @HiveField(5)
  maestro,
  @HiveField(6)
  rupay,
  @HiveField(7)
  visa,
  @HiveField(8)
  elo,
  @HiveField(9)
  other
}
