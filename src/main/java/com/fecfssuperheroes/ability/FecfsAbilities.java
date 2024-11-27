package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.event.FecfsKeyInputHandler;

import java.util.ArrayList;

public class FecfsAbilities {
    //Class that puts all abilities in an array
    public static ArrayList<String> spiderManAbilities = new ArrayList<>();
    public static void setSpiderManAbilities() {
        spiderManAbilities.add(FecfsKeyInputHandler.keyAbilityOne + "Web-Swinging");
    }
}
