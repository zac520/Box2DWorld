package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/13/14.
 */
public class Spike extends Image{

    float SPIKE_FRAME_DURATION= 0.06f;
    private float stateTime;
    private Animation spikeAnimation;
    private MainGame game;
    private Texture tex;
    private GameScreen gameScreen;
    TextureRegionDrawable myDrawable;
    protected Body body;
    private boolean facingRight = true;

    public Spike(Body myBody, MainGame myGame, float width, float height) {

        //set the extended Image class to match the spike that GameScreen already made
        super(new TextureRegion(myGame.atlas.findRegion("Spikes")));

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.game = myGame;

        //set the animation
        spikeAnimation = new Animation(SPIKE_FRAME_DURATION, game.atlas.findRegions("Spikes"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(spikeAnimation.getKeyFrame(this.getStateTime(), true));

        //get the size to match the body
        this.setSize(width * Box2DVars.PPM, height * Box2DVars.PPM);

        this.setPosition(
                body.getPosition().x * Box2DVars.PPM - ((width*Box2DVars.PPM) /2),
                body.getPosition().y * Box2DVars.PPM - ((height*Box2DVars.PPM) /2) );

        addMovement();

    }

    private void addMovement(){
        this.addAction(
                forever(
                        sequence(
                                moveTo(body.getPosition().x  * Box2DVars.PPM + 50, body.getPosition().y * Box2DVars.PPM + 50, 1),
                                moveTo(body.getPosition().x  * Box2DVars.PPM + 100, body.getPosition().y * Box2DVars.PPM, 1),
                                moveTo(body.getPosition().x  * Box2DVars.PPM + 50, body.getPosition().y * Box2DVars.PPM - 50, 1),
                                moveTo(body.getPosition().x  * Box2DVars.PPM, body.getPosition().y * Box2DVars.PPM, 1)
                        )
                )
        );
    }

    public void update(float delta) {
        stateTime += delta;
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

        //update the box2dbodies
        if(body.isAwake()) {
            body.setTransform(
                    (this.getX() + (getWidth()/2)) / Box2DVars.PPM,
                    (this.getY() + (getHeight() /2)) / Box2DVars.PPM,
                    0);
        }
    }
}
