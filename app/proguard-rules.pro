# Keep WebView / native interfaces out of release mappings where possible.
# No third-party SDKs are used; the web app is the source of truth.
-keep class com.pkdiv.pomodoro.** { *; }
