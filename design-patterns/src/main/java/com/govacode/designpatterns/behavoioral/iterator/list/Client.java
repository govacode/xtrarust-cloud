package com.govacode.designpatterns.behavoioral.iterator.list;

import com.govacode.designpatterns.behavoioral.iterator.Iterator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    public static void main(String[] args) {
        TreasureChest treasureChest = new TreasureChest();
        Iterator<Item> iterator = treasureChest.iterator(ItemType.POTION);
        while (iterator.hasNext()) {
            log.info("{}", iterator.next());
        }
    }
}
