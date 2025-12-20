package com.govacode.designpatterns.behavoioral.iterator.list;

import com.govacode.designpatterns.behavoioral.iterator.Iterator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 百宝箱迭代器实现（JDK中迭代器多为内部类 此类声明为内部类更合理）
 *
 * @author gova
 */
public class TreasureChestIterator implements Iterator<Item> {

    private final List<Item> items;

    private int cursor;

    public TreasureChestIterator(List<Item> items, ItemType type) {
        this.items = type == ItemType.ANY ? items : items.stream().filter(e -> e.getType() == type).collect(Collectors.toList());
    }

    @Override
    public boolean hasNext() {
        return cursor < items.size();
    }

    @Override
    public Item next() {
        if (cursor >= items.size()) {
            throw new NoSuchElementException();
        }
        return items.get(cursor++);
    }
}
