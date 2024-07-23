import 'dart:io'; // Import dart:io untuk menggunakan exit()

import 'package:flutter/material.dart';

class PasswordPage extends StatefulWidget {
  const PasswordPage({super.key});

  @override
  _PasswordPageState createState() => _PasswordPageState();
}

class _PasswordPageState extends State<PasswordPage> {
  final _passwordController = TextEditingController();
  final _correctPassword = "1234"; // Ganti dengan password yang diinginkan

  void _checkPassword() {
    if (_passwordController.text == _correctPassword) {
      // Tutup aplikasi setelah password benar
      exit(0); // Menutup aplikasi secara paksa
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Incorrect Password')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Enter Password'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField(
              controller: _passwordController,
              obscureText: true,
              decoration: InputDecoration(hintText: 'Password'),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _checkPassword,
              child: Text('Submit'),
            ),
          ],
        ),
      ),
    );
  }
}
