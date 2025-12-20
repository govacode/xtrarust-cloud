package com.govacode.designpatterns.behavoioral.iterator.list;

import com.govacode.designpatterns.behavoioral.iterator.Iterator;

import java.util.ArrayList;
import java.util.List;

/**
 * 百宝箱
 *
 * @author gova
 */
public class TreasureChest {

    private final List<Item> items;

    /**
     * Constructor.
     */
    public TreasureChest() {
        items = List.of(
                new Item(ItemType.POTION, "Potion of courage"),
                new Item(ItemType.RING, "Ring of shadows"),
                new Item(ItemType.POTION, "Potion of wisdom"),
                new Item(ItemType.POTION, "Potion of blood"),
                new Item(ItemType.WEAPON, "Sword of silver +1"),
                new Item(ItemType.POTION, "Potion of rust"),
                new Item(ItemType.POTION, "Potion of healing"),
                new Item(ItemType.RING, "Ring of armor"),
                new Item(ItemType.WEAPON, "Steel halberd"),
                new Item(ItemType.WEAPON, "Dagger of poison"));
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public Iterator<Item> iterator(ItemType type) {
        return new TreasureChestIterator(items, type);
    }
}
