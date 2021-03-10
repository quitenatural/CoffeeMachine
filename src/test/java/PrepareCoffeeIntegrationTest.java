import com.coffee.application.Job;
import com.coffee.composition.CoffeeComposition;
import com.coffee.composition.TeaComposition;
import com.coffee.exception.CoffeeException;
import com.coffee.machine.CoffeeMachine;
import com.coffee.machine.Command;
import com.coffee.machine.IngredientsInStock;
import com.coffee.machine.IngredientsRunningLow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrepareCoffeeIntegrationTest {

    @Test
    public void shouldPrepareDrinkAsPerGivenComposition() throws InterruptedException {
        // Fill Available Stock
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("hot_water", 600);
        availableStock.put("hot_milk", 500);
        availableStock.put("ginger_syrup", 100);
        availableStock.put("sugar_syrup", 100);
        availableStock.put("tea_leaves_syrup", 100);
        availableStock.put("coffee_powder", 100);
        IngredientsInStock ingredientsInStock = new IngredientsInStock(availableStock);

        // Create CoffeeMachine
        Command coffeeMachine = new CoffeeMachine(ingredientsInStock, new IngredientsRunningLow());

        // Tea Composition
        Map<String, Integer> composition = new HashMap<>();
        composition.put("hot_water", 200);
        composition.put("hot_milk", 100);
        composition.put("ginger_syrup", 10);
        composition.put("sugar_syrup", 10);
        composition.put("tea_leaves_syrup", 30);

        // Coffee Composition
        Map<String, Integer> coffeeComposition = new HashMap<>();
        coffeeComposition.put("hot_water", 200);
        coffeeComposition.put("hot_milk", 100);
        coffeeComposition.put("sugar_syrup", 20);
        coffeeComposition.put("coffee_powder", 30);

        // Initialise the machine (warm-up)
        int outlets = 3;
        ExecutorService pool = Executors.newFixedThreadPool(outlets);
        Queue<Job> orderQueue = new LinkedList<>();
        //TODO latch count will depend on queue size
        CountDownLatch latch = new CountDownLatch(outlets);
        orderQueue.add(new Job(new TeaComposition(composition), coffeeMachine, latch));
        orderQueue.add(new Job(new TeaComposition(composition), coffeeMachine, latch));
        orderQueue.add(new Job(new CoffeeComposition(coffeeComposition), coffeeMachine, latch));

        while (orderQueue.isEmpty() == false) {
            pool.execute(orderQueue.poll());
        }

        // The main program waits for 3 jobs to finish
        latch.await();

        pool.shutdown();

        Map<String, Integer> expectedQty = Map.of(
                "hot_milk", 200, "ginger_syrup", 80, "sugar_syrup", 60,
                "tea_leaves_syrup", 40, "hot_water", 0, "coffee_powder", 70);
        Assertions.assertEquals(expectedQty, coffeeMachine.availableIngredients());
    }

    @Test
    public void shouldThrowCoffeeExceptionWhenIngredientNotInStock() {
        // Fill Available Stock
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("hot_water", 100);
        availableStock.put("hot_milk", 500);
        availableStock.put("ginger_syrup", 100);
        IngredientsInStock ingredientsInStock = new IngredientsInStock(availableStock);

        // Create CoffeeMachine
        Command coffeeMachine = new CoffeeMachine(ingredientsInStock, new IngredientsRunningLow());

        // Tea Composition
        Map<String, Integer> composition = new HashMap<>();
        composition.put("hot_water", 200);
        composition.put("hot_milk", 100);
        composition.put("tea_leaves_syrup", 30);

        Assertions.assertThrows(CoffeeException.class,
                () -> coffeeMachine.prepareDrink(new TeaComposition(composition)));
    }

    @Test
    public void shouldThrowCoffeeExceptionWhenIngredientQuantityIsInSufficient() {
        // Fill Available Stock
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("hot_water", 50);
        availableStock.put("hot_milk", 500);
        availableStock.put("ginger_syrup", 100);
        IngredientsInStock ingredientsInStock = new IngredientsInStock(availableStock);

        // Create CoffeeMachine
        Command coffeeMachine = new CoffeeMachine(ingredientsInStock, new IngredientsRunningLow());

        // Tea Composition
        Map<String, Integer> composition = new HashMap<>();
        composition.put("hot_water", 200);
        composition.put("hot_milk", 100);
        composition.put("tea_leaves_syrup", 30);

        Assertions.assertThrows(CoffeeException.class,
                () -> coffeeMachine.prepareDrink(new TeaComposition(composition)));
    }

    @Test
    public void shouldGetIngredientsRunningLow() {
        // Fill Available Stock
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("hot_water", 5);
        availableStock.put("hot_milk", 500);
        availableStock.put("ginger_syrup", 100);
        availableStock.put("sugar_syrup", 4);
        availableStock.put("tea_leaves_syrup", 100);
        IngredientsInStock ingredientsInStock = new IngredientsInStock(availableStock);

        // Create CoffeeMachine
        Command coffeeMachine = new CoffeeMachine(ingredientsInStock, new IngredientsRunningLow());

        Map<String, Integer> ingredientsRunningLow = coffeeMachine.ingredientsRunningLow();

        Assertions.assertEquals(Map.of("hot_water", 5, "sugar_syrup", 4), ingredientsRunningLow);
    }

    @Test
    public void shouldRefillIngredients() {
        // Fill Available Stock
        Map<String, Integer> availableIngredientsStock = new HashMap<>();
        availableIngredientsStock.put("hot_water", 5);
        availableIngredientsStock.put("sugar_syrup", 4);
        availableIngredientsStock.put("tea_leaves_syrup", 100);
        IngredientsInStock ingredientsInStock = new IngredientsInStock(availableIngredientsStock);

        // Create CoffeeMachine
        Command coffeeMachine = new CoffeeMachine(ingredientsInStock, new IngredientsRunningLow());

        // Refill Ingredients
        Map<String, Integer> refillIngredients = new ConcurrentHashMap<>();
        refillIngredients.put("hot_water", 95);
        refillIngredients.put("sugar_syrup", 96);
        Map<String, Integer> actualQty = coffeeMachine.refillIngredients(refillIngredients);

        Map<String, Integer> expectedQty = Map.of(
                "hot_water", 100,
                "sugar_syrup", 100,
                "tea_leaves_syrup", 100);
        Assertions.assertEquals(
                expectedQty, actualQty
        );
    }

    @Test
    public void shouldFetchAvailableIngredients() {
        // Fill Available Stock
        Map<String, Integer> availableIngredientsStock = new HashMap<>();
        availableIngredientsStock.put("hot_water", 5);
        availableIngredientsStock.put("sugar_syrup", 4);
        availableIngredientsStock.put("tea_leaves_syrup", 100);
        IngredientsInStock ingredientsInStock = new IngredientsInStock(availableIngredientsStock);

        // Create CoffeeMachine
        Command coffeeMachine = new CoffeeMachine(ingredientsInStock, new IngredientsRunningLow());

        Map<String, Integer> inStock = coffeeMachine.availableIngredients();

        Map<String, Integer> expectedQty = Map.of(
                "hot_water", 5,
                "sugar_syrup", 4,
                "tea_leaves_syrup", 100);
        Assertions.assertEquals(
                expectedQty, inStock
        );
    }
}

