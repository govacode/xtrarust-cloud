package com.govacode.designpatterns.creational.prototype.deepcopy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class Person implements Serializable {

    private static final long serialVersionUID = 8692918372936646800L;

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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<String> hobbies = new ArrayList<>();
        hobbies.add("basketball");

        Person original = new Person("tom", 30, hobbies, new Car("bmw", 200));
        Person clonedPerson = DeepCopyUtil.copy(original);
        // org.apache.commons.lang3.SerializationUtils 深复制
        SerializationUtils.clone(original);

        // original: Person{name='tom', age=30, hobbies=[basketball], car=Car{brand='bmw', maxSpeed=200}}
        log.info("original: {}", original);
        // clonedPerson: Person{name='tom', age=30, hobbies=[basketball], car=Car{brand='bmw', maxSpeed=200}}
        log.info("clonedPerson: {}", clonedPerson);
        log.info("original == clonedPerson ? {}", original == clonedPerson); // false
        log.info("{}", original.getCar() == clonedPerson.getCar()); // false
        log.info("{}", original.getHobbies() == clonedPerson.getHobbies()); // false
        original.setName("jack");
        original.setCar(new Car("benz", 230));
        hobbies.add("music");
        original.setHobbies(hobbies);

        // original: Person{name='jack', age=30, hobbies=[basketball, music], car=Car{brand='benz', maxSpeed=230}}
        log.info("original: {}", original);
        // clonedPerson: Person{name='tom', age=30, hobbies=[basketball], car=Car{brand='bmw', maxSpeed=200}}
        log.info("clonedPerson: {}", clonedPerson);

        //Spring BeanUtils#copyProperties()为浅复制
    }
}
