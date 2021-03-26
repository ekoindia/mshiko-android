##-optimizationpasses 5
##-dontusemixedcaseclassnames
##-dontskipnonpubliclibraryclasses
##-dontpreverify
##-verbose
##-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
##
##-keep public class * extends android.app.Activity
##-keep public class * extends android.app.Application
##-keep public class * extends android.app.Service
##-keep public class * extends android.content.BroadcastReceiver
##-keep public class * extends android.content.ContentProvider
##-keep public class * extends android.app.backup.BackupAgentHelper
##-keep public class * extends android.preference.Preference
##-keep public class com.android.vending.licensing.ILicensingService
##-keep public class * extends android.support.v4.app.Fragment
##-keep public class * extends android.support.v4.app.DialogFragment
##
##-keepclasseswithmembernames class * {
##    native <methods>;
##}
##
##-keepclasseswithmembers class * {
##    public <init>(android.content.Context, android.util.AttributeSet);
##}
##
##-keepclasseswithmembers class * {
##    public <init>(android.content.Context, android.util.AttributeSet, int);
##}
##
##-keepclassmembers class * extends android.app.Activity {
##   public void *(android.view.View);
##}
##
##-keepclassmembers enum * {
##    public static **[] values();
##    public static ** valueOf(java.lang.String);
##}
##
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
#-keepclassmembers class **.R$* {
# public static <fields>;
#}
##
##
##
##For Yes bank
##
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }
## The support library contains references to newer platform versions.
## Don't warn about those in case this app is linking against an older
## platform version. We know about them, and they are safe.
#-dontwarn android.support.**
#
#-keepattributes Signature -keepattributes *Annotation*
#-keep public class com.google.android.gms.* { public *; }
#-dontwarn com.google.android.gms.**
#-keepclassmembers class fqcn.of.javascript.interface.for.webview { # public *; #}
#-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
##-for support lib
#-dontwarn android.support.**
#-dontwarn org.apache.**
#-keep class in.org.npci.** {*;}
#-keep class org.npci.upi.** {*;}
##-for ksoap and pull parser
#-dontwarn javax.xml.parsers.**
#-dontwarn org.w3c.dom.** -dontwarn org.kxml2.**
#-dontwarn org.xmlpull.v1.**
#-dontwarn javax.xml.parsers.DocumentBuilder.parse.**
#-keep class javax.xml.parsers.** { *; }
#-keep class org.w3c.dom.** { *; }
#-keep class org.kxml2.** { *; }
#-keep class org.xmlpull.** { *; }
#-keep class javax.xml.parsers.DocumentBuilder.parse.** { *; }
#-keep public class * extends android.support.v4.view.ActionProvider { public <init>(android.content.Context); }
#-keep interface android.support.v4.** { *; }
#-keep interface android.support.v7.** { *; }
#-keep class android.support.** { *; }
######################################################################
##REMOVE WARNINGS
#-dontwarn android.support.design.internal.**
#-dontwarn com.google.android.gms.**
#-dontwarn android.support.v4.**
## Enable proguard with Google libs
#-keep class com.google.** { *;}
#-dontwarn com.google.common.**
#-dontwarn com.google.ads.**
######################################################################## #support-v7
#-keep public class android.support.v7.widget.** { *; }
#-keep public class android.support.v7.internal.widget.** { *; }
#-keep public class android.support.v7.internal.view.menu.** { *; }
#-dontwarn org.**
#-dontwarn javax.xml.**
#################################################################### ##https://github.com/excilys/androidannotations/issues/1341
#-dontwarn org.androidannotations.api.rest.** -keep class org.apache.** { *; }
#-keep class java.net.** { *; }
## http://stackoverflow.com/questions/29679177/cardview-shadow-not-appearing-in-lollipop-after-obfuscate-with-proguard/29698051
#-keep class android.support.v7.widget.RoundRectDrawable { *; }
######################################################################## #support-v7
# -keep public class android.support.v7.widget.** { *; }
# -keep public class android.support.v7.internal.widget.** { *; }
# -keep public class android.support.v7.internal.view.menu.** { *; }
# -keep public class * extends android.support.v4.view.ActionProvider { public <init>(android.content.Context); }
## ####################################################################### #support-design
# -dontwarn android.support.design.**
# -keep class android.support.design.** { *; }
# -keep interface android.support.design.** { *; }
# -keep public class android.support.design.R$* { *; }
## ################################################# #CrashAnalytics
# -keep class com.crashlytics.** { *; }
# -dontwarn com.crashlytics.**
## ################################################# #Gson
# -keep class com.android.volley.** { *; }
# -keep class org.apache.commons.logging.**
# -keepattributes Signature
# # For using GSON @Expose annotation
# -keepattributes *Annotation*
# # Gson specific classes
# -keep class sun.misc.Unsafe { *; }
# -keep class com.google.gson.stream.** { *; }
# # Application classes that will be serialized/deserialized over Gson
# -keep class com.google.gson.examples.android.model.** { *; }
# # Prevent proguard from stripping interface information from TypeAdapterFactory,
# # JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
# -keep class * implements com.google.gson.TypeAdapterFactory
# -keep class * implements com.google.gson.JsonSerializer
# -keep class * implements com.google.gson.JsonDeserializer
# -keep class com.google.gson.stream.** { *; }
# # Application classes that will be serialized/deserialized over Gson
# -keep class com.mgs.sbiupi.common.data.models.** { *; }
# -dontwarn org.apache.http.** -dontwarn android.net.http.AndroidHttpClient
# -dontwarn com.google.android.gms.** -dontwarn com.android.volley.toolbox.**
# -keep class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *; }
# -keep class org.apache.http.** { *; }

# -dontwarn com.squareup.okhttp.**
# # Butterknife library
# # Retain generated class which implement Unbinder.
# -keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
#
# # Prevent obfuscation of types which use ButterKnife annotations since the simple name
# # is used to reflectively look up the generated ViewBinding.
# -keep class butterknife.*
# -keepclasseswithmembernames class * { @butterknife.* <methods>; }
# -keepclasseswithmembernames class * { @butterknife.* <fields>; }
##
##
# #EventBus
# -keepattributes *Annotation*
# -keepclassmembers class ** {
#     @org.greenrobot.eventbus.Subscribe <methods>;
# }
# -keep enum org.greenrobot.eventbus.ThreadMode { *; }
##
# # Only required if you use AsyncExecutor
## -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
##     <init>(java.lang.Throwable);
## }
##
##
## GreenDao rules
## Source: http://greendao-orm.com/documentation/technical-faq
##
#-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
#    public static java.lang.String TABLENAME;
#}
##-keep class **$Properties
##
##-keep class com.tozny.crypto.android.AesCbcWithIntegrity$PrngFixes$* { *; }
##
##-dontwarn me.everything.**
