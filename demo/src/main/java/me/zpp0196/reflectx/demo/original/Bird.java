package me.zpp0196.reflectx.demo.original;

/**
 * @author zpp0196
 */
class Bird extends Animal implements IFlightable {
    private Wing wing;
    private String childName;

    public void setChildName(String childName) {
        super.name = childName;
        this.childName = childName;
    }

    public String getChildName() {
        this.childName = childName;
        return super.name;
    }

    @Override
    public void fly() {
        System.out.println(name + " is flying...");
    }
}
