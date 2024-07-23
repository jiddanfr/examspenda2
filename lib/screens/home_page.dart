import 'package:flutter/material.dart';
import 'package:wakelock/wakelock.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    Wakelock.enable();
  }

  @override
  void dispose() {
    Wakelock.disable();
    super.dispose();
  }

  void _navigateToPasswordPage() {
    Navigator.of(context).pushNamed('/password');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Kiosk Mode App'),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: _navigateToPasswordPage,
          child: Text('Exit Kiosk Mode'),
        ),
      ),
    );
  }
}
