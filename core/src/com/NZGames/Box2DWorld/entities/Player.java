package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/10/14.
 */
public class Player extends Image{
    protected Body body;
    public boolean  facingLeft = true;
    public boolean  isWalking = false;
    public static final float      HEIGHT = 40;
    public static final float      WIDTH = 30;
    public static final float       DAMP_EFFECT = 0.0f;
    float           stateTime = 0;
    int             crystalCount = 0;
    public static  int PLAYER_MAX_SPEED = 8;
    public static int FORWARD_FORCE = 32;//will be reset based on player weight
    public static float JUMPING_FORCE = 0.1f;//will be reset based on player weight
    public float height=40;
    public float width=30;
    public float worldHeight = 0;//we will multiply by Box2D constant once to save processing
    public float worldWidth = 0;
    Group graphicsGroup;
    Texture downArrow;
    boolean selected = false;
    Image downArrowImage;
    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;
    float RUNNING_FRAME_DURATION = 0.12f;


    TextureRegionDrawable myDrawable;
    public Player(Body body){
        this.body = body;

    }

    public Player(GameScreen gameScreen, Body body, float myWidth, float myHeight){

        super(new TextureRegion(gameScreen.atlas.findRegion("MainCharLeft")));

        this.body = body;
        this.width = myWidth;
        this.height = myHeight;
        this.worldHeight = myHeight * Box2DVars.PPM;
        this.worldWidth = myWidth * Box2DVars.PPM;

        //set the forward force to be multipled by player mass for consistency
        this.FORWARD_FORCE =  FORWARD_FORCE * (int) this.body.getMass();
        this.JUMPING_FORCE =  JUMPING_FORCE * (int) this.body.getMass();

        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, gameScreen.atlas.findRegions("MainCharLeft"));
        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, gameScreen.atlas.findRegions("MainCharRight"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(walkRightAnimation.getKeyFrame(this.getStateTime(), true));

        //get the size to match the body
        this.setSize(width * Box2DVars.PPM, height * Box2DVars.PPM);

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
    public void update(float delta) {
        stateTime += delta;
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
    @Override
    public void act(float delta) {

        //allow the movement, etc that is set on creation elsewhere to run
        super.act(delta);

        //update the time for this class
        this.update(delta);


        if(isWalking) {
            myDrawable.setRegion(facingLeft ? walkLeftAnimation.getKeyFrame(getStateTime(), true) : walkRightAnimation.getKeyFrame(getStateTime(), true));
        }
        else{
            myDrawable.setRegion(facingLeft ? walkLeftAnimation.getKeyFrame(0, true) : walkRightAnimation.getKeyFrame(0, true));
        }


        this.setDrawable(myDrawable);

        //update the image position to match the box2d position
        graphicsGroup.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth / 2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight / 2));



    }

    public Group getGroup(){
        return graphicsGroup;
    }
    public Body getBody(){
        return body;
    }
    public boolean isFacingLeft(){
        return facingLeft;
    }
    public boolean getIsWalking(){
        return isWalking;
    }

    public float getStateTime(){
        return stateTime;
    }
    public void collectCrystal(){
        this.crystalCount++;
    }
}