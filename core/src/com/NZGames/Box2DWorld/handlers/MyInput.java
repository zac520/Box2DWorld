package com.NZGames.Box2DWorld.handlers;

/**
 * Created by zac520 on 7/10/14.
 */
public class MyInput {
    public static boolean [] keys;
    public static boolean [] pkeys;

    public static final int NUM_KEYS = 5;
    public static final int JUMP = 0;
    public static final int MAGIC = 1;
    public static final int MOVE_RIGHT = 2;
    public static final int MOVE_LEFT = 3;
    public static final int SWORD = 4;


    static{
        keys = new boolean [NUM_KEYS];
        pkeys = new boolean [NUM_KEYS];
    }

    public static void update (){
        for (int i = 0; i < NUM_KEYS; i++){
            pkeys[i] = keys[i];
        }
    }

    public static void setKey(int i, boolean b){
        keys[i] = b;
    }

    public static boolean isDown(int i){
        return keys[i];
    }

    public static boolean isPressed(int i){
        return keys[i] && !pkeys[i];
    }

    public static void resetKeys(){
        for (int i = 0; i < NUM_KEYS; i++){
            pkeys[i] = false;
            keys[i] = false;
        }
    }
}
