package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.NZGames.Box2DWorld.screens.MenuScreen;
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
public class Player extends GenericActor{
    public boolean  isWalking = false;
    int             crystalCount = 0;
    public static  int PLAYER_MAX_SPEED = 3;
    public int FORWARD_FORCE = 1;//will be reset based on player weight
    public float JUMPING_FORCE = 0.225f;//will be reset based on player weight
    public static  float RUNNING_FRAME_DURATION = 0.2f;



    public Animation spellAnimation; //I think we will put these in their own class later.

    public Player(MainGame myGame, Body body, float myWidth, float myHeight){

        //super(new TextureRegion(myGameScreen.atlas.findRegion("MainCharLeft")));

        this.body = body;
        this.worldHeight = myHeight * Box2DVars.PPM;
        this.worldWidth = myWidth * Box2DVars.PPM;

        this.game = myGame;

        //set the forward force to be multipled by player mass for consistency
        this.FORWARD_FORCE =  FORWARD_FORCE * (int) this.body.getMass();
        this.JUMPING_FORCE =  JUMPING_FORCE * (int) this.body.getMass();

        //set the hitPoints and magicPoints
        hitPoints = 100;
        magicPoints = 0;

        //load the animations
        leftAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("HeroNoSword_LV1"));
        rightAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("HeroNoSword_RV1"));
        spellAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("Fireball1"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(rightAnimation.getKeyFrame(this.getStateTime(), true));

        //get the size to match the body
        this.setSize(worldWidth, worldHeight);

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
                downArrow = new TextureRegion(game.atlas.findRegion("Arrowdownblue"));
                game.selectEnemy(genericActor);
                return true;
            }
        });

    }
    public void update(float delta) {
        stateTime += delta;
    }

    @Override
    public void act(float delta) {

        //allow the movement, etc that is set on creation elsewhere to run
        super.act(delta);

        //update the time for this class
        this.update(delta);

        //if player runs out of hp, we st
        if(hitPoints <=0){
            //TODO we need a death animation, and a game over screen. For now, we just go back to menu
            game.setScreen(new MenuScreen(game));
        }

        if(isWalking) {
            myDrawable.setRegion(facingRight ? rightAnimation.getKeyFrame(getStateTime(), true) : leftAnimation.getKeyFrame(getStateTime(), true));
        }
        else{
            myDrawable.setRegion(facingRight ? rightAnimation.getKeyFrame(0, true) : leftAnimation.getKeyFrame(0, true));
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
        return facingRight;
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