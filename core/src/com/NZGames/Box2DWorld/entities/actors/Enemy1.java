package com.NZGames.Box2DWorld.entities.actors;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/19/14.
 */
public class Enemy1  extends GenericActor {
    float FRAME_DURATION= 0.12f;
    float MAX_SPEED = 1;
    float FORWARD_FORCE = 1;
    float JUMP_FORCE = 4;

    public Enemy1(Body myBody, MainGame myGame, float width, float height) {

        //set the hp and mp and contact damage
        maxHitPoints = 10;
        hitPoints = 10;
        magicPoints = 10;
        maxMagicPoints = 10;
        contactDamage = 15;

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.body.setAwake(false);//start all enemies asleep. they will be wakened by the player when on screen
        this.game = myGame;

        //set the animation
        rightAnimation = new Animation(FRAME_DURATION, game.atlas.findRegions("Enemy1_Right1"));
        leftAnimation = new Animation(FRAME_DURATION, game.atlas.findRegions("Enemy1_Left1"));
        downFacingFrame = new TextureRegion(game.atlas.findRegion("Enemy1_Right1"));

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


        //add the hp bar
        TextureRegion hpBar =  new TextureRegion(myGame.atlas.findRegion("Enemy1HPBar"));
        hpBarImage = new Image(hpBar);
        hpBarImage.setSize(125,75);

        hpBarImage.setPosition(
                getX(),
                getY() - hpBarImage.getHeight()
        );

        maxHPImageWidth = hpBarImage.getWidth()/1.8f;//found this manually. ugh.

        //add the fill for the hp bar
        TextureRegion currentHPTexture =  new TextureRegion(myGame.atlas.findRegion("EnemyHPBar"));
        currentHPImage  = new Image (currentHPTexture);
        currentHPImage.setSize(
                maxHPImageWidth,
                hpBarImage.getHeight()/7
        );

        currentHPImage.setPosition(
                hpBarImage.getX() + hpBarImage.getWidth()/2.85f,
                hpBarImage.getY() + hpBarImage.getHeight()/2.85f
        );
    }


    public Group getGroup(){
        return graphicsGroup;
    }

    private void addMovement(){
        maxDist = 3;
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
        timeInterval += delta;
        dist = (body.getPosition().x - previousPositionX);

        //if it has gone farther than our max distance, or it tries for 5 seconds
        // (and is stuck for some reason), we reverse the direction
        if((Math.abs(dist) > maxDist) || (timeSpentTryingDirection >5)) {
            forwardForce *= -1;
            dist = 0;
            previousPositionX = body.getPosition().x;
            timeSpentTryingDirection = 0;

            //trigger that we should play the frame that faces down for turning
            playDownFrame = true;
        }

        //add a jump every 2 second
        if(timeInterval >2){
            //if the monster is moving slower than the max speed, then add force
            if (body.getLinearVelocity().y < MAX_SPEED) {
                //body.applyForceToCenter(forwardForce, 0, false);
                body.setLinearVelocity(
                        body.getLinearVelocity().x ,
                        body.getLinearVelocity().y+ JUMP_FORCE);
            }
            timeInterval=0;
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