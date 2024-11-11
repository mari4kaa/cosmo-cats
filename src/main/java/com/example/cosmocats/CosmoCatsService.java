package com.example.cosmocats;

import com.example.annotation.RequiresCosmoCatsFeature;

public class CosmoCatsService {
    @RequiresCosmoCatsFeature
    public void getCosmoCats() {
        System.out.println("Here are your cosmo cats. Treat them goodly");
    }
}
