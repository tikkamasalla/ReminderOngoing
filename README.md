# ReminderOngoing

An LSPosed module that forces Samsung Reminders notifications to stay
ongoing (unswipeable).

## Why

On Android 14+, users can dismiss notifications that apps marked ongoing —
unless the app also holds the dismissal exemption. Samsung Reminders posts
plain notifications with neither, so tools that only flip the exemption
(e.g. NotiFixer) have nothing to preserve. This module sets `FLAG_ONGOING`
on Reminders notifications, completing the pair: flag + exemption together
make them stick.

## How it works

Two paths, whichever the framework delivers:

1. **Client path (primary):** hooks `NotificationManager.notify()` inside
   the Reminders app process and ORs in `FLAG_ONGOING` before the
   notification leaves the app.
2. **System path (fallback):** hooks
   `NotificationManagerService.enqueueNotificationInternal()` in
   `system_server` and flags matching posts there.

Use with the `SYSTEM_EXEMPT_FROM_DISMISSIBLE_NOTIFICATIONS` appop allowed
for the Reminders app (e.g. via NotiFixer) — the flag alone is not enough
on Android 14+.

## Requirements

- Android 9+
- LSPosed framework (ZygiskNext, Magisk Zygisk, etc.)
- Samsung Reminders (`com.samsung.android.app.reminder`)

## Install

1. Install the APK from [Releases](../../releases).
2. Enable **ReminderOngoing** in LSPosed.
3. Scope it to the **Reminders** app (and System Framework for the
   fallback path).
4. Restart the affected processes (force-stop Reminders; reboot or
   soft-restart for the system path).

## Build

JDK 21, Android SDK with platform 36:

```sh
./gradlew :app:assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`.

## License

MIT — see [LICENSE](LICENSE).
