import 'dart:io'; // Import dart:io untuk menggunakan SystemNavigator

import 'package:flutter/material.dart';
import 'package:kiosk_mode/kiosk_mode.dart';
import 'package:wakelock/wakelock.dart';
import 'screens/home_page.dart';
import 'screens/password_page.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Aktifkan wakelock untuk mencegah layar mati
  Wakelock.enable();
  
  // Masukkan aplikasi ke mode kiosk
  _enterKioskMode();

  runApp(const MyApp());
}

Future<void> _enterKioskMode() async {
  try {
    final mode = await getKioskMode();
    if (mode != KioskMode.enabled) {
      await startKioskMode();
    }
  } catch (e) {
    print('Error entering kiosk mode: $e');
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Kiosk Mode App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const HomePage(), // Gunakan HomePage sebagai halaman utama
      routes: {
        '/password': (context) => const PasswordPage(),
        
      },
    );
  }
}
