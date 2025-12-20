package com.govacode.designpatterns.behavoioral.iterator;

/**
 * 迭代器接口
 *
 * @param <T>
 * @author gova
 */
public interface Iterator<T> {

    boolean hasNext();

    T next();
}
