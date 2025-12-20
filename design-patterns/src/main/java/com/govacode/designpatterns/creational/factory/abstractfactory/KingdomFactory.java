package com.govacode.designpatterns.creational.factory.abstractfactory;

/**
 * To create a kingdom we need objects with common theme.
 *
 * @author gova
 */
public interface KingdomFactory {

    King createKing();

    Castle createCastle();

    Army createArmy();
}
