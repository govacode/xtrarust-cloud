package com.govacode.designpatterns.creational.builder.v2;

import java.io.Serializable;

public interface IBuilder<T> extends Serializable {

    T build();
}
