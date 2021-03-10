package com.coffee.application;

import com.coffee.composition.Composition;
import com.coffee.exception.CoffeeException;
import com.coffee.machine.Command;

import java.util.concurrent.CountDownLatch;

public class Job implements Runnable {
    private final Composition composition;
    private final Command machine;
    private final CountDownLatch latch;

    public Job(Composition composition, Command machine, CountDownLatch latch) {
        this.composition = composition;
        this.machine = machine;
        this.latch = latch;
    }

    @Override
    public void run() {
        boolean jobSuccess = true;
        try {
            machine.prepareDrink(composition);
        } catch (CoffeeException exception) {
            System.out.println(exception.getMessage());
            jobSuccess = false;
        }

        if (jobSuccess)
            System.out.println(composition.getDrinkName() + " is prepared");

        latch.countDown();
    }
}
