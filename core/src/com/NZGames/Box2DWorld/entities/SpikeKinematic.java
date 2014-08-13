package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/13/14.
 */
public class SpikeKinematic extends Image{

    float SPIKE_FRAME_DURATION= 0.06f;
    private float stateTime;
    private Animation spikeAnimation;
    private Texture tex;
    private GameScreen gameScreen;
    TextureRegionDrawable myDrawable;
    protected Body body;
    private boolean facingRight = true;
    private float worldWidth;
    private float worldHeight;

    Vector2 pos = new Vector2();
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;

    public SpikeKinematic(Body myBody, GameScreen myGameScreen, float width, float height) {

        //set the extended Image class to match the spike that GameScreen already made
        super(new TextureRegion(myGameScreen.atlas.findRegion("Spikes")));

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.gameScreen = myGameScreen;

        //set the animation
        spikeAnimation = new Animation(SPIKE_FRAME_DURATION, myGameScreen.atlas.findRegions("Spikes"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(spikeAnimation.getKeyFrame(this.getStateTime(), true));

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
        //http://www.badlogicgames.com/wordpress/?p=2017
        maxDist = 1;
        dist = 0;
        dir=body.getLinearVelocity();
    }

    public void update(float delta) {
        stateTime += delta;
        dist += dir.len() * delta;


        //if it has gone farther than our max distance, we reverse the direction
        if(dist > maxDist) {
            dir.scl(-1,-1);//reverse the direction
            dist = 0;
        }

        body.setLinearVelocity(dir);
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
        myDrawable.setRegion(spikeAnimation.getKeyFrame(this.getStateTime(), true));
        this.setDrawable(myDrawable);

        //update the position to match the box2d position
        this.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth /2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight /2) );
    }
}
