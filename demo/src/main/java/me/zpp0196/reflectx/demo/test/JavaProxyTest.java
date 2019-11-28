package me.zpp0196.reflectx.demo.test;

import androidx.annotation.Keep;

import org.junit.Test;

import java.util.List;

import me.zpp0196.reflectx.demo.proxy.IAnimal;
import me.zpp0196.reflectx.demo.proxy.IBird;
import me.zpp0196.reflectx.demo.proxy.IFlightableProxy;
import me.zpp0196.reflectx.demo.proxy.IFood;
import me.zpp0196.reflectx.demo.proxy.IPerson;
import me.zpp0196.reflectx.proxy.ProxyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author zpp0196
 */
public class JavaProxyTest {

    private static final String TOM = "Tom";
    private static final String JERRY = "Jerry";
    private static final String FLAPPY_BIRD = "Flappy Bird";

    public static void main(String[] args) {
        sTestAll();
    }

    public static void sTestAll() {
        JavaProxyTest test = new JavaProxyTest();
        test.testAll();
    }

    @Test
    public void testAll() {
        testGetterAndSetter();
        testCallMethod();
        testUpdateOriginal();
    }

    @Test
    public void testCompiler() {
        IAnimal animal = ProxyFactory.create(IAnimal.class);
        animal.setName(TOM);
        assertEquals(TOM, animal.getName());
    }

    @Test
    public void testGetterAndSetter() {
        IAnimal animal = ProxyFactory.create(IAnimal.class);
        animal.setName(TOM);
        String name = animal.getName();
        assertEquals(TOM, name);
        name = animal.name(JERRY).name();
        assertEquals(JERRY, name);

        IBird bird = ProxyFactory.create(IBird.class);
        bird.name(FLAPPY_BIRD);
        assertEquals(FLAPPY_BIRD, bird.name());
        bird.setName(FLAPPY_BIRD);
        assertEquals(bird.getName(), bird.getChildName());
    }

    @Test
    public void testCallMethod() {
        IAnimal cat = ProxyFactory.create(IAnimal.class);
        cat.setName(TOM);
        cat.say("hello");
        IAnimal jerry = ProxyFactory.create(IAnimal.class);
        jerry.setName(JERRY);

        IFood fish = ProxyFactory.create(IFood.class);
        fish.name("fish");
        IPerson person = ProxyFactory.create(IPerson.class);
        //noinspection Convert2Lambda
        person.feed(cat, fish, new IFood.EatingListener() {
            @Override
            @Keep
            public void onFinishedEating(String name) {
                System.out.println(name + " eating finished");
            }
        });
        person.addPet(cat).addPet(jerry);
        List pets = person.getPets();
        /*List<IAnimal> pets = person.getPets2();
        for (IAnimal pet : pets) {
            System.out.println("pet:" + pet.getName());
        }*/
        for (Object pet : pets) {
            IAnimal animal = ProxyFactory.proxyObject(IAnimal.class, pet);
            System.out.println("pet: " + animal.getName());
        }

        IFlightableProxy bird = ProxyFactory.create(IBird.class);
        bird.as(IAnimal.class).setName(FLAPPY_BIRD);
        bird.fly();
    }

    @Test
    public void testUpdateOriginal() {
        IAnimal tom = ProxyFactory.create(IAnimal.class);
        IAnimal jerry = ProxyFactory.create(IAnimal.class);
        tom.setName(TOM);
        assertEquals(TOM, tom.name());
        jerry.setName(JERRY);
        assertEquals(JERRY, jerry.name());
        tom.set(jerry);
        assertEquals(JERRY, tom.name());
        tom.set(null);
        assertNull(tom.get());
    }
}
