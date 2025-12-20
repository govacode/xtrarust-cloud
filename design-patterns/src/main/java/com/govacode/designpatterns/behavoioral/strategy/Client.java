package com.govacode.designpatterns.behavoioral.strategy;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Client {

    public static void main(String[] args) {
        DragonSlayer dragonSlayer = new DragonSlayer(new MeleeStrategy());
        dragonSlayer.goToBattle();

        dragonSlayer.changeStrategy(new ProjectileStrategy());
        dragonSlayer.goToBattle();

        dragonSlayer.changeStrategy(new SpellStrategy());
        dragonSlayer.goToBattle();

        // JDK Comparator策略接口 Arrays可以认为是strategy context
        Integer[] arr = new Integer[]{10, 39, 26, -5};
        Arrays.sort(arr, Integer::compare);
        log.info("arr: {}", Arrays.toString(arr));
        // Spring Security AuthenticationProvider接口 AuthenticationManager
    }
}
