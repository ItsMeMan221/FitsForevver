package com.santiago.fitsforever;

public class ExcersiceType {
    String id, name;
    public ExcersiceType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
