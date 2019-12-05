-keepclassmembers class me.zpp0196.reflectx.** {*;}
-keepclassmembers class **.*$Proxy {*;}
-keep @me.zpp0196.reflectx.Keep class *
-keepclassmembers class * {
@me.zpp0196.reflectx.Keep *;
}