package com.coffee.machine;

import com.coffee.composition.Composition;

import java.util.Map;

public class CoffeeMachine implements Command {
    private final IngredientsInStock ingredientsInStock;
    private final IngredientsRunningLow ingredientsRunningLow;

    public CoffeeMachine(IngredientsInStock ingredientsInStock, IngredientsRunningLow ingredientsRunningLow) {
        this.ingredientsInStock = ingredientsInStock;
        this.ingredientsRunningLow = ingredientsRunningLow;
    }

    @Override
    public void prepareDrink(Composition drink) {
        // Not introducing synchronized block in favour of higher concurrency
        ingredientsInStock.updateIngredientsInStock(drink);
        Map<String, Integer> inStock = ingredientsInStock.getIngredientsInStock();

        ingredientsRunningLow.updateIngredientsRunningLow(inStock);
    }

    @Override
    public Map<String, Integer> refillIngredients(Map<String, Integer> refillIngredients) {
        Map<String, Integer> refillIngredientsInStock = ingredientsInStock.refillIngredientsInStock(refillIngredients);

        Map<String, Integer> ingredientsInStock = this.ingredientsInStock.getIngredientsInStock();
        // passing again the whole ingredientsInStock for full scan
        // as user may forget to refill the necessary ingredient that is having low qty
        ingredientsRunningLow.updateIngredientsRunningLow(ingredientsInStock);

        return refillIngredientsInStock;
    }

    @Override
    public Map<String, Integer> ingredientsRunningLow() {
        Map<String, Integer> availableIngredientsStock = ingredientsInStock.getIngredientsInStock();
        return ingredientsRunningLow.updateIngredientsRunningLow(availableIngredientsStock);
    }

    @Override
    public Map<String, Integer> availableIngredients() {
        return ingredientsInStock.getIngredientsInStock();
    }
}
