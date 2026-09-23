package com.tikkamasalla.reminderongoing

import android.app.Notification
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class OngoingHook : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "ReminderOngoing"
        private const val TARGET_PKG = "com.samsung.android.app.reminder"
        private const val SYSTEM_SERVER_PKG = "android"
        // Notification.FLAG_ONGOING value, kept as a literal so the build
        // never depends on framework stub generation for this constant.
        private const val FLAG_ONGOING = 0x00000002
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != SYSTEM_SERVER_PKG) {
            hookClientNotify(lpparam)
            return
        }
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.server.notification.NotificationManagerService",
                lpparam.classLoader,
                "enqueueNotificationInternal",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
                Int::class.javaPrimitiveType,
                Notification::class.java,
                Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        try {
                            val pkg = param.args[0] as? String ?: return
                            if (pkg != TARGET_PKG) return
                            val notification = param.args[6] as? Notification ?: return
                            notification.flags = notification.flags or FLAG_ONGOING
                            XposedBridge.log(
                                "$TAG: forced ongoing for $pkg id=${param.args[5]}",
                            )
                        } catch (t: Throwable) {
                            XposedBridge.log("$TAG error: ${t.message}")
                        }
                    }
                },
            )
            XposedBridge.log("$TAG: hook installed")
        } catch (t: Throwable) {
            XposedBridge.log("$TAG hook failed: ${t.message}")
        }
    }

    /**
     * Client-side path: hook NotificationManager.notify() inside the
     * Reminders app itself so the flag travels with the notification.
     * This is the path that works when system_server injection is
     * unavailable; it needs the Reminders app in the module scope.
     */
    private fun hookClientNotify(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != TARGET_PKG) return
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.NotificationManager",
                lpparam.classLoader,
                "notify",
                String::class.java,
                Int::class.javaPrimitiveType,
                Notification::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        try {
                            val notification = param.args[2] as? Notification ?: return
                            notification.flags = notification.flags or FLAG_ONGOING
                            XposedBridge.log("$TAG: flagged ongoing at source")
                        } catch (t: Throwable) {
                            XposedBridge.log("$TAG client error: ${t.message}")
                        }
                    }
                },
            )
            XposedBridge.log("$TAG: client hook installed")
        } catch (t: Throwable) {
            XposedBridge.log("$TAG client hook failed: ${t.message}")
        }
    }
}
