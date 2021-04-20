package com.wooordhunt;

public class Pair<T1, T2> {
    public Pair(T1 engValue, T2 ruValue) {
        this.engValue = engValue;
        this.ruValue = ruValue;
    }

    public T1 getEngValue() {
        return engValue;
    }

    public T2 getRuValue() {
        return ruValue;
    }

    private final T1 engValue;
    private final T2 ruValue;
}
