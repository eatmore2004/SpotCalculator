package com.andrii.positioncalculator;

import io.paperdb.Paper;

public class StorageManager {

    public static void savePosition(String var_name, Position position){
        Paper.book().write(var_name, position.getHash());
    }
    public static Position getPosition(String var_name){
        String hash = Paper.book().read(var_name);
        if (hash != null) return new Position(hash);
        else return new Position();
    }

}
