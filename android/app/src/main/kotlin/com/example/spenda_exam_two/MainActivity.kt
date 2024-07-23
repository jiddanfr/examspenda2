package com.example.spenda_exam_two

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val REQUEST_CODE_SYSTEM_ALERT_WINDOW = 1
    private val REQUEST_CODE_WRITE_SETTINGS = 2
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mencegah layar tidur
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyAdminReceiver::class.java)

        // Cek apakah aplikasi sudah menjadi admin perangkat
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Perangkat ini perlu diatur ke mode kiosk untuk keperluan ujian.")
            startActivityForResult(intent, 1)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                // Minta izin untuk menampilkan jendela di atas aplikasi lain
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, REQUEST_CODE_SYSTEM_ALERT_WINDOW)
            } else {
                checkWriteSettingsPermission()
            }
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.example.spenda_exam_two/kiosk").setMethodCallHandler { call, result ->
            if (call.method == "exitKioskMode") {
                exitKioskMode()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun checkWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            // Minta izin untuk mengubah pengaturan sistem
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
        } else {
            startLockTaskMode()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                startLockTaskMode()
            } else {
                Log.d("KioskMode", "Failed to activate Device Admin")
            }
        } else if (requestCode == REQUEST_CODE_SYSTEM_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                checkWriteSettingsPermission()
            }
        } else if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                startLockTaskMode()
            }
        }
    }

    private fun startLockTaskMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (devicePolicyManager.isLockTaskPermitted(packageName)) {
                startLockTask()
                Log.d("KioskMode", "Lock Task Mode Started")
            } else {
                Log.d("KioskMode", "Lock Task Mode Not Permitted")
            }
        } else {
            Log.d("KioskMode", "Lock Task Mode Not Supported")
        }
    }

    private fun exitKioskMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopLockTask()
            Log.d("KioskMode", "Lock Task Mode Stopped")
        }
    }
}
