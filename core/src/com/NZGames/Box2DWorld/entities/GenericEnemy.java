package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by zac520 on 8/13/14.
 */
public class GenericEnemy extends Image {
    float SPIKE_FRAME_DURATION= 0.06f;
    float MAX_SPEED = 8;
    private float FORWARD_VELOCITY = 200;
    private float stateTime;
    private Animation rightAnimation;
    private Animation leftAnimation;
    private Texture tex;
    private GameScreen gameScreen;
    TextureRegionDrawable myDrawable;
    protected Body body;
    private boolean facingRight = false;
    private float worldWidth;
    private float worldHeight;

    float previousPositionX;
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;
    float forwardForce = 0;
    float timeSpentTryingDirection = 0;
    boolean playDownFrame = false;

    TextureRegion downFacingFrame; //used for turning animation
    public GenericEnemy(Body myBody, GameScreen myGameScreen, float width, float height) {

        //set the extended Image class to match the spike that GameScreen already made
        super(new TextureRegion(myGameScreen.atlas.findRegion("Spikes")));

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.gameScreen = myGameScreen;

        //set the animation
        //temporarily, we are using the player as our animation
        //TODO, update this with the new enemy Russel is making
        rightAnimation = new Animation(SPIKE_FRAME_DURATION, myGameScreen.atlas.findRegions("MainCharRight"));
        leftAnimation = new Animation(SPIKE_FRAME_DURATION, myGameScreen.atlas.findRegions("MainCharLeft"));
        downFacingFrame = new TextureRegion(myGameScreen.atlas.findRegion("MainCharDown"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(rightAnimation.getKeyFrame(this.getStateTime(), true));

        //get the size to match the body
        this.setSize(width * Box2DVars.PPM, height * Box2DVars.PPM);

        //set the world dimensions so we only multiply vars once
        worldWidth = width*Box2DVars.PPM;
        worldHeight = height*Box2DVars.PPM;

        //set the position
        this.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth /2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight /2) );

        //add movement for kinematic body
        addMovement();

    }

    private void addMovement(){
        maxDist = 1;
        dist = 0;

        //give it the initial velocity
        body.setLinearVelocity(1,0);
        dir = body.getLinearVelocity();

        //set the forward force
        forwardForce = body.getMass() * 5;

        //set the previous t
        previousPositionX = body.getPosition().x;
    }

    public void update(float delta) {
        //update statetime for animation
        stateTime += delta;
        timeSpentTryingDirection += delta;
        dist = (body.getPosition().x - previousPositionX);

        //if it has gone farther than our max distance, or it tries for 3 seconds
        // (and is stuck for some reason), we reverse the direction
        if((Math.abs(dist) > maxDist) || (timeSpentTryingDirection >3)) {
            forwardForce *= -1;
            dist = 0;
            previousPositionX = body.getPosition().x;
            timeSpentTryingDirection = 0;

            //trigger that we should play the frame that faces down for turning
            playDownFrame = true;
        }



        if(forwardForce >0) {
            facingRight = true;

            //if the monster is moving slower than the max speed, then add force
            if (body.getLinearVelocity().x < MAX_SPEED) {
                body.applyForceToCenter(forwardForce, 0, false);
            }
        }
        else {
            facingRight = false;
            if (body.getLinearVelocity().x > -MAX_SPEED) {
                body.applyForceToCenter(forwardForce, 0, false);
            }
        }

    }
    public float getStateTime(){
        return stateTime;
    }
    public Body getBody(){
        return body;
    }
    @Override
    public void act(float delta) {

        //allow the movement, etc that is set on creation elsewhere to run
        super.act(delta);

        //update the time for this class
        this.update(delta);

        //change the drawable to the current frame
        //this is fine for now, but we really need a turning animation, and to play it for FRAME_DURATION * number of
        // frames in that animation
        if(playDownFrame){
            myDrawable.setRegion(downFacingFrame);
            if(timeSpentTryingDirection > SPIKE_FRAME_DURATION) {
                playDownFrame = false;
            }
        }

        else if(facingRight) {
            myDrawable.setRegion(rightAnimation.getKeyFrame(this.getStateTime(), true));
        }
        else{
            myDrawable.setRegion(leftAnimation.getKeyFrame(this.getStateTime(), true));
        }

        this.setDrawable(myDrawable);

        //update the position to match the box2d position
        this.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth /2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight /2) );
    }
}
