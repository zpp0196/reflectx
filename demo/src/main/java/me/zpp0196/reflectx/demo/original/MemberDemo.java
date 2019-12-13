package me.zpp0196.reflectx.demo.original;

public class MemberDemo {

    private static final String TAG = "ReflectDemo";
    private String tag = getClass().getName();

    public MemberDemo() {
    }

    public MemberDemo(int i) {
    }

    private void fun() {

    }

    static int add(int a, int b) {
        return a + b;
    }
}
