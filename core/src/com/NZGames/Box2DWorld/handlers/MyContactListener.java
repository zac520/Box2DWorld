package com.NZGames.Box2DWorld.handlers;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.GenericActor;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Created by zac520 on 7/10/14.
 */
public class MyContactListener implements ContactListener {

    private boolean playerOnGround;
    private int numFootContacts =0;
    private GameScreen gameScreen;
    private boolean playerHitEnemy = false;
    private MainGame game;
    public MyContactListener(MainGame myGame){
        super();
        game = myGame;
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
                if (!game.bodiesToRemove.contains(fa.getBody(), true)) {
                    game.bodiesToRemove.add(fa.getBody());
                }
            }
        }

        if(fb.getUserData() != null && fb.getUserData().equals("crystal")){
            //remove crystal
            //since world is updating, we are going to queue the crystals
            //and remove them after the update for each step
            if(fa.getUserData() != null && !fa.getUserData().equals("awake")) {
                if (!game.bodiesToRemove.contains(fb.getBody(), true)) {
                    game.bodiesToRemove.add(fb.getBody());
                }
            }

        }


        //handle the enemy collision
        if(fa.getFilterData().categoryBits == Box2DVars.BIT_ENEMY){
            if(fb.getFilterData().categoryBits == Box2DVars.BIT_PLAYER){
                GenericActor myActor = (GenericActor) fb.getBody().getUserData();
                myActor.incurDamage(25);
            }
        }

        if(fb.getFilterData().categoryBits == Box2DVars.BIT_ENEMY){
            if(fa.getFilterData().categoryBits == Box2DVars.BIT_PLAYER){
                GenericActor myActor = (GenericActor) fa.getBody().getUserData();
                myActor.incurDamage(25);
            }
        }


        //wake up the box2d body
        if(fa.getUserData() != null && fa.getUserData().equals("awake")){
            fb.getBody().setAwake(true);
            fb.getBody().setSleepingAllowed(false);


        }
        if(fb.getUserData() != null && fb.getUserData().equals("awake")){
            fa.getBody().setAwake(true);
            fa.getBody().setSleepingAllowed(false);

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

        }
        if(fb.getUserData() != null && fb.getUserData().equals("awake")){
            fa.getBody().setSleepingAllowed(true);
            fa.getBody().setAwake(false);
        }


        //spikes
        if(fa.getUserData() != null && fa.getUserData().equals("spike")){

        }
        if(fb.getUserData() != null && fb.getUserData().equals("spike")){

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

}
