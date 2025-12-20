package com.govacode.designpatterns.creational.prototype.shallowcopy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 JDK Object clone() 方法的浅复制
 * 1. 实现 Cloneable 接口
 * 2. 覆盖 clone() 方法
 */
@Slf4j
@Data
public class Person implements Cloneable {

    private String name;

    private int age;

    private List<String> hobbies;

    private Car car;

    public Person(String name, int age, List<String> hobbies, Car car) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
        this.car = car;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                ", car=" + car +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        List<String> hobbies = new ArrayList<>();
        hobbies.add("basketball");

        Person original = new Person("tom", 30, hobbies, new Car("bmw", 200));
        Person clonedPerson = (Person) original.clone();

        // original: Person{name='tom', age=30, hobbies=[basketball], car=Car{brand='bmw', maxSpeed=200}}
        log.info("original: {}", original);
        // clonedPerson: Person{name='tom', age=30, hobbies=[basketball], car=Car{brand='bmw', maxSpeed=200}}
        log.info("clonedPerson: {}", clonedPerson);
        log.info("original == clonedPerson ? {}", original == clonedPerson); // false
        log.info("{}", original.getCar() == clonedPerson.getCar()); // true
        log.info("{}", original.getHobbies() == clonedPerson.getHobbies()); // true
        original.setName("jack");
        original.setCar(new Car("benz", 230));
        hobbies.add("music");
        original.setHobbies(hobbies);
        // original: Person{name='jack', age=30, hobbies=[basketball, music], car=Car{brand='benz', maxSpeed=230}}
        log.info("original: {}", original);
        // clonedPerson: Person{name='tom', age=30, hobbies=[basketball, music], car=Car{brand='bmw', maxSpeed=200}}
        log.info("clonedPerson: {}", clonedPerson);
    }
}
