package com.NZGames.Box2DWorld.entities.monster_drops;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.actors.GenericActor;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by zac520 on 9/6/14.
 */
public class HealthDrop  extends GenericMonsterDrop {
    float FRAME_DURATION= 0.12f;
    float MAX_SPEED = 1;
    float FORWARD_FORCE = 1;

    public HealthDrop(Vector2 bodyPosition, MainGame myGame) {


        //set the hp and mp and contact damage
        healthRestorePoints = 20;
        magicRestorePoints = 0;
        money = 0;

        //set the box2d body and the world it lives in
        //define platform body
        BodyDef bdef = new BodyDef();
        bdef.position.set(bodyPosition);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.linearVelocity.set(0,0);
        //create body
        body = myGame.scene.getWorld().createBody(bdef);


        //define Fixture
        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS / Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.restitution = 0.2f;//1= perfectly bouncy 0 = not at all bouncy
//        fdef.filter.categoryBits = Box2DVars.BIT_PICKUP;
        fdef.filter.maskBits = Box2DVars.BIT_PLAYER|Box2DVars.BIT_GROUND;//what it can collide with (bitwise operators)

        //create fixture
        body.createFixture(fdef).setUserData("pickup");

        //create sensor
        shape.setRadius((RADIUS + 2) / Box2DVars.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = Box2DVars.BIT_PICKUP;
        fdef.filter.maskBits = Box2DVars.BIT_PLAYER;
        fdef.isSensor = true;//make the foot go through ground for easier contact determining
        body.createFixture(fdef).setUserData("pickupSensor");

        body.setFixedRotation(false);
        body.setUserData(this);//make it so that the body can find this class
        shape.dispose();

        this.game = myGame;

        //set the animation
        animation = new Animation(FRAME_DURATION, game.atlas.findRegions("Heart1"));


        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(animation.getKeyFrame(this.getStateTime(), true));

        //get image the size to match the body
        this.setSize(RADIUS*2, RADIUS*2);
        this.setPosition(0, 0);

        //add movement for dynamic body
        //addMovement();

        //add this class to a graphics group so that we can append to it later
        graphicsGroup = new Group();
        graphicsGroup.addActor(this);



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



    }

    @Override
    public void act(float delta) {

        if (body.isAwake()) {

            //allow the movement, etc that is set on creation elsewhere to run
            super.act(delta);

            //update the time for this class
            this.update(delta);

            //change the drawable to the current frame
            myDrawable.setRegion(animation.getKeyFrame(this.getStateTime(), true));


            this.setDrawable(myDrawable);

            //update the image position to match the box2d position
//            this.setRotation(body.getAngle());
            graphicsGroup.setCenterPosition(
                    body.getPosition().x * Box2DVars.PPM - RADIUS,
                    body.getPosition().y * Box2DVars.PPM - RADIUS);


        }
    }

}
