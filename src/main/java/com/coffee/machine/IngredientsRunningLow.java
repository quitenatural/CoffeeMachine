package com.coffee.machine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientsRunningLow {
    //TODO pass from application property
    public static final int THRESHOLD_QTY = 5;
    private final Map<String, Integer> ingredientsRunningLow;

    public IngredientsRunningLow() {
        this.ingredientsRunningLow = new ConcurrentHashMap<>();
    }

    // TODO Map.copyOf what if the map is empty?
    public Map<String, Integer> updateIngredientsRunningLow(Map<String, Integer> ingredientsInStock) {
        ingredientsRunningLow.clear();
        for (Map.Entry<String, Integer> ingredientEntry : ingredientsInStock.entrySet()) {
            Integer currentQty = ingredientEntry.getValue();

            if (currentQty <= THRESHOLD_QTY) {
                ingredientsRunningLow.put(ingredientEntry.getKey(), ingredientEntry.getValue());
            }
        }
        return Map.copyOf(ingredientsRunningLow);
    }
}
