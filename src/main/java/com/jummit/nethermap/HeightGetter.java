package com.jummit.nethermap;

public interface HeightGetter {
    int get(int heightMapHeight);

    HeightGetter PASSTHROUGH = x -> x;

    record Fixed(int height) implements HeightGetter {
        @Override
        public int get(int heightMapHeight) {
            return height;
        }
    }
}

