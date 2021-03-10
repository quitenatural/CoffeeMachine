package com.coffee.machine;

import com.coffee.composition.Composition;

import java.util.Map;

public interface Command {
    void prepareDrink(Composition drink);

    Map<String, Integer> refillIngredients(Map<String, Integer> refillIngredients);

    Map<String, Integer> ingredientsRunningLow();

    Map<String, Integer> availableIngredients();
}
