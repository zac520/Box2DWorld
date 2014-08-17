package com.NZGames.Box2DWorld.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Created by zac520 on 7/10/14.
 */
public class MyContactListener implements ContactListener {

    private boolean playerOnGround;
    private int numFootContacts =0;
    private Array<Body> bodiesToRemove;
    private boolean playerHitEnemy = false;
    public MyContactListener(){
        super();
        bodiesToRemove = new Array<Body>();
    }

    Fixture fa;
    Fixture fb;
    //called when two fixtures begin to collide
    public void beginContact (Contact c){
        //System.out.println("Begin Contact");
        fa = c.getFixtureA();
        fb = c.getFixtureB();


        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            playerOnGround = true;
            numFootContacts ++;
        }

        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            playerOnGround = true;
            numFootContacts ++;
        }



        if(fa.getUserData() != null && fa.getUserData().equals("crystal")){
            //remove crystal
            //since world is updating, we are going to queue the crystals
            //and remove them after the update for each step
            //System.out.println("fa is crystal");

            //if we have an intersection, and the intersection is NOT the "awake world", then it must be the player
            if(fb.getUserData() != null && !fb.getUserData().equals("awake")) {
                if (!bodiesToRemove.contains(fa.getBody(), true)) {
                    bodiesToRemove.add(fa.getBody());
                }
            }
        }

        if(fb.getUserData() != null && fb.getUserData().equals("crystal")){
            //remove crystal
            //since world is updating, we are going to queue the crystals
            //and remove them after the update for each step
            if(fa.getUserData() != null && !fa.getUserData().equals("awake")) {
                if (!bodiesToRemove.contains(fb.getBody(), true)) {
                    bodiesToRemove.add(fb.getBody());
                }
            }

        }


        //handle the spike collision
        if(fa.getUserData() != null && fa.getUserData().equals("spike")){


            //if we have an intersection, and the intersection is NOT the "awake world", then it must be the player
            if(fb.getUserData() != null && !fb.getUserData().equals("awake")) {
                playerHitEnemy = true;
            }

        }
        if(fb.getUserData() != null && fb.getUserData().equals("spike")){

            if(fa.getUserData() != null && !fa.getUserData().equals("awake")) {
                playerHitEnemy = true;
            }

        }


        //wake up the box2d body
        if(fa.getUserData() != null && fa.getUserData().equals("awake")){

            fb.getBody().setAwake(true);
        }
        if(fb.getUserData() != null && fb.getUserData().equals("awake")){
            fa.getBody().setAwake(true);
        }
    }

    //called when two fixtures no longer collide
    public void endContact (Contact c) {
        //System.out.println("End Contact");
        fa = c.getFixtureA();
        fb = c.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            //System.out.println("fa is foot");
            playerOnGround = false;
            numFootContacts --;

        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            //System.out.println("fb is foot");
            playerOnGround = false;
            numFootContacts --;
        }

        //sleep objects
        if(fa.getUserData() != null && fa.getUserData().equals("awake")){

            fb.getBody().setAwake(false);

        }
        if(fb.getUserData() != null && fb.getUserData().equals("awake")){

            fa.getBody().setAwake(false);
        }


        //spikes
        if(fa.getUserData() != null && fa.getUserData().equals("spike")){
            System.out.print("Collision occured1");
            fb.getBody().setAwake(false);

        }
        if(fb.getUserData() != null && fb.getUserData().equals("spike")){
            System.out.print("Collision occured1");
            fa.getBody().setAwake(false);
        }

    }

    //collision detection
    //collision handling
    public void preSolve (Contact c, Manifold m) {}

    //whatever happens after
    public void postSolve (Contact c, ContactImpulse ci) {}

    public boolean isPlayerOnGround(){
        //if there is at least one, then player is on ground. Not sure why that is better than the bool method
        return numFootContacts >0;
    }

    public boolean didPlayerHitEnemy(){
        //if there is at least one, then player is on ground. Not sure why that is better than the bool method
        return playerHitEnemy;
    }
    public Array getBodiesToRemove(){
        return bodiesToRemove;
    }
}
