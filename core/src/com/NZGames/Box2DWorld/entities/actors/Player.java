package com.NZGames.Box2DWorld.entities.actors;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.actors.GenericActor;
import com.NZGames.Box2DWorld.entities.spells.Fireflower;
import com.NZGames.Box2DWorld.handlers.AnimatedImage;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.screens.MenuScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/10/14.
 */
public class Player extends GenericActor {
    public boolean  isWalking = false;
    int             crystalCount = 0;
    public static  int PLAYER_MAX_SPEED = 3;
    public int FORWARD_FORCE = 1;//will be reset based on player weight
    public float JUMPING_FORCE = 9;//will be reset based on player weight
    public static  float RUNNING_FRAME_DURATION = 0.2f;

    private Animation swordSlashAnimationRight;
    private Animation swordSlashAnimationLeft;
    boolean isSlashingSword = false;
    Fixture swordSlashRight;
    Fixture swordSlashLeft;
    Filter enemiesHitable;
    Filter enemiesNotHitable;

    public Animation spellAnimation; //I think we will put these in their own class later.

    public Player(MainGame myGame, Body body, float myWidth, float myHeight){

        //super(new TextureRegion(myGameScreen.atlas.findRegion("MainCharLeft")));

        this.body = body;
        this.worldHeight = myHeight * Box2DVars.PPM;
        this.worldWidth = myWidth * Box2DVars.PPM;

        this.game = myGame;

        //set the forward force to be multipled by player mass for consistency
        this.FORWARD_FORCE =  FORWARD_FORCE * (int) this.body.getMass();
        //this.JUMPING_FORCE =  JUMPING_FORCE * (int) this.body.getMass();

        //set the hitPoints and magicPoints
        maxHitPoints = 300;
        hitPoints = 300;
        magicPoints = 10;
        maxMagicPoints = 10;

        //load the animations
        leftAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("HerowSword_LV1"));
        rightAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("HerowSword_RV1"));
        swordSlashAnimationLeft = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("hero_slashing_left"));
        swordSlashAnimationRight = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("hero_slashing_right"));


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

        //set the color of the down arrow
        downArrow = new TextureRegion(game.atlas.findRegion("Arrowdownblue"));


        //add the hp bar
        TextureRegion playerHpBar =  new TextureRegion(myGame.atlas.findRegion("HeroHPMPInterface"));
        hpBarImage = new Image(playerHpBar);
        hpBarImage.setSize(125, 75);
        hpBarImage.setCenterPosition(
                getCenterX(),
                getY() - hpBarImage.getHeight()
        );
        maxHPImageWidth = hpBarImage.getWidth()/1.8f;//found this manually. ugh.

        //add the fill for the hp bar
        TextureRegion currentHPTexture =  new TextureRegion(myGame.atlas.findRegion("HeroHPFillBar"));
        currentHPImage = new Image (currentHPTexture);
        currentHPImage.setSize(
                maxHPImageWidth,
                hpBarImage.getHeight() / 7
        );

        currentHPImage.setPosition(
                hpBarImage.getX() + hpBarImage.getWidth() / 2.85f,
                hpBarImage.getY() + hpBarImage.getHeight() / 2f
        );

        //set the current spell (will be switchable when we add that to UI
        currentSpell = new Fireflower(game);

        //set the filter for future use
        short myBits = Box2DVars.BIT_ENEMY;
        enemiesHitable = new Filter();
        enemiesHitable.categoryBits= Box2DVars.BIT_SWORD;
        enemiesHitable.maskBits = myBits;

        enemiesNotHitable = new Filter();
        short myBits2 =0;
        enemiesNotHitable.maskBits = myBits2;
        enemiesNotHitable.categoryBits= Box2DVars.BIT_SWORD;
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

        //walking left or right normally
        if(!isSlashingSword) {
            if (isWalking) {
                myDrawable.setRegion(facingRight ? rightAnimation.getKeyFrame(getStateTime(), true) : leftAnimation.getKeyFrame(getStateTime(), true));
            } else {
                myDrawable.setRegion(facingRight ? rightAnimation.getKeyFrame(0, true) : leftAnimation.getKeyFrame(0, true));
            }
        }
        else{//the player is slashing sword, so use that animation instead
            myDrawable.setRegion(facingRight ? swordSlashAnimationRight.getKeyFrame(getStateTime(), true) : swordSlashAnimationLeft.getKeyFrame(getStateTime(), true));
        }


        this.setDrawable(myDrawable);

        //update the image position to match the box2d position
        graphicsGroup.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth / 2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight / 2));



    }

    public void slashSword(){


        //iterate through the fixtures, turning them on
        Array<Fixture> myFixtures = body.getFixtureList();
        for (int x = 0; x < myFixtures.size; x++){
            if(myFixtures.get(x).getUserData().equals("swordSlashRight")){
                myFixtures.get(x).setFilterData(enemiesHitable);
            }
            else if(myFixtures.get(x).getUserData().equals("swordSlashLeft")){
                myFixtures.get(x).setFilterData(enemiesHitable);
            }
        }



        //start the animation
        swordSlashAnimationRight.setPlayMode(Animation.PlayMode.NORMAL);
        final AnimatedImage swordslash = (facingRight) ? new AnimatedImage(swordSlashAnimationRight) : new AnimatedImage(swordSlashAnimationLeft);
        swordslash.setSize(
                getWidth()*4,
                getHeight()
        );
        swordslash.setCenterPosition(
                getCenterX(),
                getCenterY()
        );
        swordslash.addAction(
                sequence(
                        delay(0.2f),

                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                swordslash.remove();
                                isSlashingSword=false;
                                //iterate through the fixtures, turning them off
                                Array<Fixture> myFixtures = body.getFixtureList();
                                for (int x = 0; x < myFixtures.size; x++){
                                    if(myFixtures.get(x).getUserData().equals("swordSlashRight")){
                                        myFixtures.get(x).setFilterData(enemiesNotHitable);
                                    }
                                    else if(myFixtures.get(x).getUserData().equals("swordSlashLeft")){
                                        myFixtures.get(x).setFilterData(enemiesNotHitable);
                                    }
                                }

                                return false;
                            }
                        }));
        graphicsGroup.addActor(swordslash);
        isSlashingSword = true;
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