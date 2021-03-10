package com.coffee.application;

import com.coffee.composition.TeaComposition;
import com.coffee.machine.CoffeeMachine;
import com.coffee.machine.Command;
import com.coffee.machine.IngredientsInStock;
import com.coffee.machine.IngredientsRunningLow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // Fill Available Stock
        Map<String, Integer> availableStock = new HashMap<>();
        availableStock.put("hot_water", 600);
        availableStock.put("hot_milk", 500);
        availableStock.put("ginger_syrup", 100);
        availableStock.put("sugar_syrup", 100);
        availableStock.put("tea_leaves_syrup", 100);
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

        // Initialise the machine (warm-up)
        int outlets = 3;
        ExecutorService pool = Executors.newFixedThreadPool(outlets);
        Queue<Job> orderQueue = new LinkedList<>();
        //TODO latch count will depend on queue size
        CountDownLatch latch = new CountDownLatch(outlets);
        orderQueue.add(new Job(new TeaComposition(composition), coffeeMachine, latch));
        orderQueue.add(new Job(new TeaComposition(composition), coffeeMachine, latch));
        orderQueue.add(new Job(new TeaComposition(composition), coffeeMachine, latch));

        while (orderQueue.isEmpty() == false) {
            pool.execute(orderQueue.poll());
        }

        // The main program waits for 3 jobs to finish
        latch.await();

        pool.shutdown();
    }
}
