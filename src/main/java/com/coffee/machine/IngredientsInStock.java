package com.coffee.machine;

import com.coffee.composition.Composition;
import com.coffee.exception.CoffeeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientsInStock {
    private final Map<String, Integer> ingredientsInStock;

    //TODO do deep cloning here
    public IngredientsInStock(Map<String, Integer> ingredientsInStock) {
        this.ingredientsInStock = new ConcurrentHashMap<>(ingredientsInStock);
    }

    // Prepare drink
    public void updateIngredientsInStock(Composition drink) {
        // Precheck if ingredients quantity is sufficient to prepare drink else throw exception
        precheckIngredientsInStock(drink);

        // deduct the ingredient quantity by updating the Map
        Map<String, Integer> drinkComposition = drink.getComposition();
        for (Map.Entry<String, Integer> drinkCompositionEntries : drinkComposition.entrySet()) {
            String ingredient = drinkCompositionEntries.getKey();
            if (ingredientsInStock.containsKey(ingredient)) {
                Integer availableIngredientQty = ingredientsInStock.get(ingredient);
                Integer requestedIngredientQty = drinkCompositionEntries.getValue();
                if (availableIngredientQty >= requestedIngredientQty) {
                    availableIngredientQty = availableIngredientQty - requestedIngredientQty;
                    ingredientsInStock.put(ingredient, availableIngredientQty);
                } else {
                    throw new CoffeeException(drink.getDrinkName() + "cannot be prepared because " + ingredient + " is not sufficient");
                }
            } else {
                throw new CoffeeException(drink.getDrinkName() + " cannot be prepared because " + ingredient + " is not available");
            }
        }
    }

    public Map<String, Integer> refillIngredientsInStock(Map<String, Integer> refillIngredients) {
        for (Map.Entry<String, Integer> refillIngredientEntry : refillIngredients.entrySet()) {
            if (ingredientsInStock.containsKey(refillIngredientEntry.getKey())) {
                Integer existingQty = ingredientsInStock.get(refillIngredientEntry.getKey());
                ingredientsInStock.put(refillIngredientEntry.getKey(), existingQty + refillIngredientEntry.getValue());
            }
        }

        return Map.copyOf(ingredientsInStock);
    }

    public Map<String, Integer> getIngredientsInStock() {
        return Map.copyOf(ingredientsInStock);
    }

    private void precheckIngredientsInStock(Composition drink) {
        Map<String, Integer> drinkComposition = drink.getComposition();

        for (Map.Entry<String, Integer> drinkCompositionEntries : drinkComposition.entrySet()) {
            String ingredient = drinkCompositionEntries.getKey();
            if (ingredientsInStock.containsKey(ingredient)) {
                Integer availableIngredientQty = ingredientsInStock.get(ingredient);
                Integer requestedIngredientQty = drinkCompositionEntries.getValue();
                if (requestedIngredientQty > availableIngredientQty) {
                    throw new CoffeeException(drink.getDrinkName() + "cannot be prepared because " + ingredient + " is not sufficient");
                }
            } else {
                throw new CoffeeException(drink.getDrinkName() + " cannot be prepared because " + ingredient + " is not available");
            }
        }
    }

}
