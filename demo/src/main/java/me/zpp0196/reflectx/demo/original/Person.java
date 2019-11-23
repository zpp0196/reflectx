package me.zpp0196.reflectx.demo.original;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zpp0196
 */
class Person {

    private List<Animal> pets = new ArrayList<>();

    public void feed(Animal animal, Food food, Food.EatingListener listener) {
        animal.eat(food);
        listener.onFinishedEating(animal.name);
    }

    public void addPet(Animal animal) {
        pets.add(animal);
    }

    public List<Animal> getPets() {
        return pets;
    }
}
