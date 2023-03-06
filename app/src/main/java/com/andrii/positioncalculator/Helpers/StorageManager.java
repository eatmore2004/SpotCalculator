package com.andrii.positioncalculator.Helpers;

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

    public static void saveVariable(String var_name, boolean var){
        Paper.book().write(var_name, var ? "1" : "0");
    }
    public static void saveVariable(String var_name, String var){
        Paper.book().write(var_name, var);
    }
    public static boolean getBoolVariable(String var_name){
        String data = Paper.book().read(var_name);
        if (data != null) return data.equals("1");
        return false;
    }
    public static String getStringVariable(String var_name){
        String data = Paper.book().read(var_name);
        return (data == null)? "" : data;
    }

}
