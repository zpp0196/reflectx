#-keep class me.zpp0196.reflectx.proxy.**
-keepclassmembers class me.zpp0196.reflectx.proxy.** {*;}
-keep class **.*$Proxy
-keep class **.*$Proxy {public <init>(java.lang.Object,java.lang.Class);}
-keep @me.zpp0196.reflectx.proxy.Source class *
