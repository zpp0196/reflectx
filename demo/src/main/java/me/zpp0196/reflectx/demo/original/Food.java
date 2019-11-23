package me.zpp0196.reflectx.demo.original;

/**
 * @author zpp0196
 */
class Food {
    String name;

    interface EatingListener {
        void onFinishedEating(String name);
    }
}
