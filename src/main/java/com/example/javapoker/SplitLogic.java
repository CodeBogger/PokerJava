package com.example.javapoker;

import javafx.geometry.Side;

import java.util.List;

public class SplitLogic {
    public SplitLogic(List<Player> players, int pot) {
        if(SidePot.sidePotExist) pot -= SidePot.sidePot;
    }
}
