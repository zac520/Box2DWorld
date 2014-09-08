package com.NZGames.Box2DWorld.handlers;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.actors.GenericActor;
import com.NZGames.Box2DWorld.entities.monster_drops.GenericMonsterDrop;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.physics.box2d.*;

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


        //player jump
        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            playerOnGround = true;
            numFootContacts ++;
        }

        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            playerOnGround = true;
            numFootContacts ++;
        }

        //sword slash
        if(fa.getFilterData().categoryBits == Box2DVars.BIT_SWORD){
            if(fb.getBody().getUserData() instanceof  GenericActor) {
                GenericActor myActor = (GenericActor) fb.getBody().getUserData();
                //myActor.incurDamage(2);
                myActor.getHitByPhysicalAttack(4);
            }
        }

        if(fb.getFilterData().categoryBits == Box2DVars.BIT_SWORD){
            if(fb.getBody().getUserData() instanceof  GenericActor) {
                GenericActor myActor = (GenericActor) fa.getBody().getUserData();
                //myActor.incurDamage(2);
                myActor.getHitByPhysicalAttack(4);
            }
        }

        //crystal collecting
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

                //player collides with enemy
                if((fa.getBody().getUserData() instanceof  GenericActor) && (fb.getBody().getUserData() instanceof  GenericActor) ) {
                    GenericActor myActor = (GenericActor) fb.getBody().getUserData();
                    GenericActor myEnemy = (GenericActor) fa.getBody().getUserData();
                    if (myEnemy.canCauseDamage) {
                        myActor.getHitByPhysicalAttack(myEnemy.getContactDamage());
                    }
                }
                else{//the player hit spikes or something
                    GenericActor myActor = (GenericActor) fb.getBody().getUserData();
                    myActor.getHitByPhysicalAttack(25);
                }
            }

        }

        if(fb.getFilterData().categoryBits == Box2DVars.BIT_ENEMY){
            if(fa.getFilterData().categoryBits == Box2DVars.BIT_PLAYER){
                if((fa.getBody().getUserData() instanceof  GenericActor) && (fb.getBody().getUserData() instanceof  GenericActor) ) {
                    GenericActor myActor = (GenericActor) fa.getBody().getUserData();
                    GenericActor myEnemy = (GenericActor) fb.getBody().getUserData();
                    if (myEnemy.canCauseDamage) {
                        myActor.getHitByPhysicalAttack(myEnemy.getContactDamage());
                    }
                }
                else{//the player hit spikes or something
                    GenericActor myActor = (GenericActor) fa.getBody().getUserData();
                    myActor.getHitByPhysicalAttack(25);
                }
            }
        }

        //handle the monster pickups
        if(fa.getFilterData().categoryBits == Box2DVars.BIT_PICKUP){
            if(fb.getFilterData().categoryBits == Box2DVars.BIT_PLAYER){
                if(fa.getBody().getUserData() instanceof  GenericMonsterDrop) {

                    //get the pickup and player classes
                    GenericMonsterDrop myPickup = (GenericMonsterDrop) fa.getBody().getUserData();
                    GenericActor myPlayer = (GenericActor) fb.getBody().getUserData();

                    //apply the pickups
                    myPlayer.receivePickup(
                            myPickup.getHealthRestorePoints(),
                            myPickup.getMagicRestorePoints(),
                            myPickup.getMoney()
                    );

                    //destroy the pickup
                    myPickup.destroyMonsterDrop();
                }


            }
        }

        if(fb.getFilterData().categoryBits == Box2DVars.BIT_PICKUP){
            if(fa.getFilterData().categoryBits == Box2DVars.BIT_PLAYER){
                if(fb.getBody().getUserData() instanceof  GenericMonsterDrop) {
                    GenericMonsterDrop myPickup = (GenericMonsterDrop) fb.getBody().getUserData();
                    GenericActor myPlayer = (GenericActor) fa.getBody().getUserData();

                    //apply the pickups
                    myPlayer.receivePickup(
                            myPickup.getHealthRestorePoints(),
                            myPickup.getMagicRestorePoints(),
                            myPickup.getMoney()
                    );

                    //destroy the pickup
                    myPickup.destroyMonsterDrop();
                }
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
