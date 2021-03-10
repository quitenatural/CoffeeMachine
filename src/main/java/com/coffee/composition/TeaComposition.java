package com.coffee.composition;

import java.util.Map;

public class TeaComposition implements Composition {
    private final Map<String, Integer> teaComposition;

    public TeaComposition(Map<String, Integer> teaComposition) {
        this.teaComposition = teaComposition;
    }

    @Override
    public Map<String, Integer> getComposition() {
        return teaComposition;
    }

    @Override
    public String getDrinkName() {
        return "Tea";
    }
}
