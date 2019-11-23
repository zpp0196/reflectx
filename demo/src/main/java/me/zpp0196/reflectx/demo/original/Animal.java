package me.zpp0196.reflectx.demo.original;

/**
 * @author zpp0196
 */
class Animal {
    public String name;

    public void eat(Food food) {
        System.out.println(name + " eat " + food.name);
    }

    public void say(String msg) {
        System.out.println(name + " said: " + msg);
    }
}
