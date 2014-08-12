package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by zac520 on 8/10/14.
 */
public class Player {
    protected Body body;
    public boolean  facingLeft = true;
    public boolean  isWalking = false;
    public static final float      HEIGHT = 40;
    public static final float      WIDTH = 30;
    public static final float       DAMP_EFFECT = 0.0f;
    float           stateTime = 0;
    int             crystalCount = 0;
    public static  int PLAYER_MAX_SPEED = 8;
    public static int FORWARD_FORCE = 32;
    public float height=40;
    public float width=30;
    public float worldHeight = 0;//we will multiply by Box2D constant once to save processing
    public float worldWidth = 0;
    public Player(Body body){
        this.body = body;

    }

    public Player(Body body, float myWidth, float myHeight){
        this.body = body;
        this.width = myWidth;
        this.height = myHeight;
        this.worldHeight = myHeight * Box2DVars.PPM;
        this.worldWidth = myWidth * Box2DVars.PPM;
    }
    public void update(float delta) {
        stateTime += delta;
    }

    public Body getBody(){
        return body;
    }
    public boolean isFacingLeft(){
        return facingLeft;
    }
    public boolean getIsWalking(){
        return isWalking;
    }

    public float getStateTime(){
        return stateTime;
    }
    public void collectCrystal(){
        this.crystalCount++;
    }
}