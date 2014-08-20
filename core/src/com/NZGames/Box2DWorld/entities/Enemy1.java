package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/19/14.
 */
public class Enemy1  extends Image {
    float SPIKE_FRAME_DURATION= 0.12f;
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
    Button enemyButton;
    float previousPositionX;
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;
    float forwardForce = 0;
    float timeSpentTryingDirection = 0;
    boolean playDownFrame = false;
    Texture downArrow;
    boolean selected = false;
    Image downArrowImage;
    Group graphicsGroup;//used to link in the arrow later
    boolean isInView = false;

    TextureRegion downFacingFrame; //used for turning animation
    public Enemy1(Body myBody, GameScreen myGameScreen, float width, float height) {

        //set the extended Image class to match the spike that GameScreen already made
        super(new TextureRegion(myGameScreen.atlas.findRegion("Spikes")));

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.body.setAwake(false);//start all enemies asleep. they will be wakened by the player when on screen
        this.gameScreen = myGameScreen;

        //set the animation
        //temporarily, we are using the player as our animation
        //TODO, update this with the new enemy Russel is making
        rightAnimation = new Animation(SPIKE_FRAME_DURATION, myGameScreen.atlas.findRegions("Enemy1_Right1"));
        leftAnimation = new Animation(SPIKE_FRAME_DURATION, myGameScreen.atlas.findRegions("Enemy1_Left1"));
        downFacingFrame = new TextureRegion(myGameScreen.atlas.findRegion("Enemy1_Right1"));

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
        this.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("clicked enemy");
                selectEnemy();
                return true;
            }
        });
    }


    public Group getGroup(){
        return graphicsGroup;
    }
    private void selectEnemy(){
        if(!selected) {
            downArrow = new Texture(Gdx.files.internal("assets/graphics/down_arrow.png"));
            downArrowImage = new Image(downArrow);
            downArrowImage.setSize(75, 75);
            downArrowImage.setPosition(getX(), getY() + worldHeight);

            //make the arrow move up and down
            downArrowImage.addAction(forever(
                    sequence(
                            moveTo(
                                    getX(),
                                    getY()+ worldHeight +25,
                                    0.5f
                            ),
                            moveTo(
                                    getX(),
                                    getY()+ worldHeight,
                                    0.5f
                            )
                    )));


            graphicsGroup.addActor(downArrowImage);

            selected = true;
        }
        else{
            graphicsGroup.removeActor(downArrowImage);
            selected = false;
        }
    }

    public void unselectEnemy(){
        graphicsGroup.removeActor(downArrowImage);
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
        //TODO figure out a method to show it is on screen. The body auto-sleeps, and we need it to not be awake when
        //off screen. Possibly, we can make a custom method that the contact listener looks for by finding this class
        //through the body
        if(body.isAwake()){
            isInView = true;
        }
        else{
            isInView = false;
        }
        if (isInView) {

            //allow the movement, etc that is set on creation elsewhere to run
            super.act(delta);

            //update the time for this class
            this.update(delta);

            //change the drawable to the current frame
            //this is fine for now, but we really need a turning animation, and to play it for FRAME_DURATION * number of
            // frames in that animation
            if (playDownFrame) {
                myDrawable.setRegion(downFacingFrame);
                if (timeSpentTryingDirection > SPIKE_FRAME_DURATION) {
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