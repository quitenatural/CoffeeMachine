package com.coffee.composition;

import java.util.Map;

public class CoffeeComposition implements Composition {
    private final Map<String, Integer> coffeeComposition;

    public CoffeeComposition(Map<String, Integer> coffeeComposition) {
        this.coffeeComposition = coffeeComposition;
    }

    @Override
    public String getDrinkName() {
        return "Coffee";
    }

    @Override
    public Map<String, Integer> getComposition() {
        return coffeeComposition;
    }
}
