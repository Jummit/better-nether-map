package com.jummit.nethermap;

public class FixedHeight implements Height {

    int height;

    @Override
    public int get() {
        return height;
    }

    public FixedHeight(int height) {
        this.height = height;
    }
}

