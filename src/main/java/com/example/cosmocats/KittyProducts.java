package com.example.cosmocats;

import com.example.annotation.RequiresKittyProductsFeature;

public class KittyProducts {
    @RequiresKittyProductsFeature
    public void getKittyProducts() {
        System.out.println("Here are your products. Hope you know why you need them.");
    }
}
