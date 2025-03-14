import 'dart:convert';
import 'dart:io';
import 'package:flutter/cupertino.dart';
import 'package:hive/hive.dart';
import 'credit_card.dart';
import 'package:flutter/services.dart';

import 'dart:convert';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:hive/hive.dart';
import 'credit_card.dart';

const platform = MethodChannel('com.example.channel');

Future<String> exportHiveToJson(String hiveFilePath) async {
  Hive.init(hiveFilePath);
  Hive.registerAdapter(CCardAdapter());
  Hive.registerAdapter(CardTypeAdapter());
  final box = await Hive.openBox<CCard>('creditCards');

  try {
    final cards = box.values.toList();
    final jsonOutput = jsonEncode(cards.map((card) => card.toJson()).toList());
    return jsonOutput;
  } catch (e) {
    print('An error occurred: $e');
    throw Exception('Failed to export data');
  } finally {
    await box.close();
  }
}

extension CCardToJson on CCard {
  Map<String, dynamic> toJson() {
    return {
      'cardNumber': cardNumber,
      'cardExpiry': cardExpiry,
      'cardHolderName': cardHolderName,
      'cvv': cvv,
      'bankName': bankName,
      'cardType': cardType.toString().split('.').last,
      'cardColor': cardColor,
    };
  }
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  platform.setMethodCallHandler((call) async {
    if (call.method == 'exportHiveToJson') {
      String hiveFilePath = call.arguments;
      try {
        final jsonResult = await exportHiveToJson(hiveFilePath);
        return jsonResult;
      } catch (e) {
        throw PlatformException(
          code: 'EXPORT_FAILED',
          details: "Failed to export data",
        );
      }
    }
    throw PlatformException(
      code: 'Unimplemented',
      details: "Method not implemented in Flutter",
    );
  });
}


