package com.example.aloftbudgeter;

import java.io.Serializable;

class Category implements Serializable {
    private String name;

    public Category(String name) {
        this.setName(name);
    }

    String getName() { return name; }

    private void setName(String name) { this.name = name; }
}
