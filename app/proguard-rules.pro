
-keep class ru.topbun.** { *; }
-keep class com.youlovehamit.app.** { *; }

# 2. Необходимый минимум для Koin
-keep class io.insertkoin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}

# 3. Атрибуты для работы Generic-типов (обязательно для списков и JSON)
-keepattributes Signature,InnerClasses,EnclosingMethod,Exceptions,*Annotation*

# 4. Реклама (эти SDK ломаются без правил чаще всего)
-keep class com.applovin.** { *; }
-dontwarn com.applovin.**
-keep class com.yandex.mobile.ads.** { *; }
-keep class com.bigossp.** { *; }

# 5. Voyager (навигация)
-keep class cafe.adriel.voyager.** { *; }

# 6. Стандартные заглушки для ретрофита/gson (если появятся в проекте)
-dontwarn okio.**
-dontwarn javax.annotation.**

-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe