package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/22/14.
 */
public class Enemy2 extends GenericActor {
    float FRAME_DURATION= 0.12f;
    float MAX_SPEED = 3;
    float FORWARD_FORCE = 1;
    public Enemy2(Body myBody, GameScreen myGameScreen, float width, float height) {

        //set the hp and mp and contact damage
        hitPoints = 10;
        magicPoints = 0;
        contactDamage = 20;

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.body.setAwake(false);//start all enemies asleep. they will be wakened by the player when on screen
        this.gameScreen = myGameScreen;

        //set the animation
        rightAnimation = new Animation(FRAME_DURATION, myGameScreen.atlas.findRegions("Enemy2_RV_1"));
        leftAnimation = new Animation(FRAME_DURATION, myGameScreen.atlas.findRegions("Enemy2_LV_1"));
        downFacingFrame = new TextureRegion(myGameScreen.atlas.findRegion("Enemy2_LV_1"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(rightAnimation.getKeyFrame(this.getStateTime(), true));

        //get the size to match the body
        this.setSize(width * Box2DVars.PPM, height * Box2DVars.PPM);

        //set the world dimensions so we only multiply vars once
        worldWidth = width*Box2DVars.PPM;
        worldHeight = height*Box2DVars.PPM;

        //add movement for dynamic body
        addMovement();

        //add this class to a graphics group so that we can append to it later
        graphicsGroup = new Group();
        graphicsGroup.addActor(this);
        graphicsGroup.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth / 2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight / 2));


        //make the player a button for the user to select for targeting
        genericActor = this;
        this.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downArrow = new TextureRegion(gameScreen.atlas.findRegion("Arrowdowngreen"));
                gameScreen.selectEnemy(genericActor);

                return true;
            }
        });
    }



    public Group getGroup(){
        return graphicsGroup;
    }

    private void addMovement(){
        maxDist = 1;
        dist = 0;

        //give it the initial velocity
        body.setLinearVelocity(1,0);
        dir = body.getLinearVelocity();

        //set the forward force
        forwardForce = body.getMass() * 5;

        //set the previous position
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
                //body.applyForceToCenter(forwardForce, 0, false);
                body.setLinearVelocity(
                        body.getLinearVelocity().x + FORWARD_FORCE *0.1f,
                        body.getLinearVelocity().y);
            }
        }
        else {
            facingRight = false;
            if (body.getLinearVelocity().x > -MAX_SPEED) {
                //body.applyForceToCenter(forwardForce, 0, false);
                body.setLinearVelocity(
                        body.getLinearVelocity().x - FORWARD_FORCE *0.1f,
                        body.getLinearVelocity().y);
            }
        }
    }

    @Override
    public void act(float delta) {

        if (body.isAwake()) {
            //allow the movement, etc that is set on creation elsewhere to run
            super.act(delta);

            if(haveExtraAnimation){

                animationDurationRemaining -= delta;
                if(animationDurationRemaining >0) {
                    extraAnimationCurrentFrame = new Image(extraAnimation.getKeyFrame(this.getStateTime(), true));
                    extraAnimationCurrentFrame.addAction(
                            sequence(
                                    fadeOut(0.2f)
                            )
                    );
                    extraAnimationCurrentFrame.setPosition(
                            body.getPosition().x +worldWidth/2 -extraAnimationCurrentFrame.getWidth()/2,
                            body.getPosition().y + worldHeight/2 - extraAnimationCurrentFrame.getHeight()/2
                    );
                    graphicsGroup.addActor(extraAnimationCurrentFrame);
                }
                else{
                    haveExtraAnimation = false;
                }
            }


            //update the time for this class
            this.update(delta);

            //change the drawable to the current frame
            //this is fine for now, but we really need a turning animation, and to play it for FRAME_DURATION * number of
            // frames in that animation
            if (playDownFrame) {
                myDrawable.setRegion(downFacingFrame);
                if (timeSpentTryingDirection > FRAME_DURATION) {
                    playDownFrame = false;
                }
            } else if (facingRight) {
                myDrawable.setRegion(rightAnimation.getKeyFrame(this.getStateTime(), true));
            } else {
                myDrawable.setRegion(leftAnimation.getKeyFrame(this.getStateTime(), true));
            }

            this.setDrawable(myDrawable);

            //update the image position to match the box2d position
            graphicsGroup.setPosition(
                    body.getPosition().x * Box2DVars.PPM - (worldWidth / 2),
                    body.getPosition().y * Box2DVars.PPM - (worldHeight / 2));

        }
    }
}
