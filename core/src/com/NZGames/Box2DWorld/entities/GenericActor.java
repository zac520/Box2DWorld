package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.AnimatedImage;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.handlers.MyInput;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 8/13/14.
 */
public class GenericActor extends Image  {

    /** Select enemy variables**/
    public boolean selected = false;
    Group graphicsGroup;//used to link in the arrow later
    TextureRegion downArrow;
    Image downArrowImage;

    /** How GenericEnemy relates to world**/
    protected float worldWidth;
    protected float worldHeight;
    protected float stateTime;
    protected Body body;
    protected Array<Fixture> actorFixtures;
    public boolean facingRight = false;
    protected MainGame game;

    /** Animation**/
    protected Animation rightAnimation;
    protected Animation leftAnimation;
    protected TextureRegionDrawable myDrawable;
    protected TextureRegion downFacingFrame; //used for turning animation
    protected TextureRegion damage;
    public Image currentHPImage;
    public Image hpBarImage;

    /**Extra Animation (such as spell damage)**/
    protected AnimatedImage extraAnimation;
    protected Image extraAnimationCurrentFrame; //used for spell animations
    protected boolean haveExtraAnimation = false;
    protected float animationDurationRemaining = 0;
    protected Label label;

    /** Movement **/
    float previousPositionX;
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;
    float forwardForce = 0;
    float timeSpentTryingDirection = 0;
    boolean playDownFrame = false;

    /** Reference to self**/
    GenericActor genericActor;

    /** Character Attributes **/
    protected int hitPoints;
    protected int maxHitPoints;
    protected int magicPoints;
    protected int maxMagicPoints;
    protected int contactDamage; //damage player gets when he contacts enemy
    public float percentHitPointsRemaining;
    protected float maxHPImageWidth;

    public GenericActor(){

    }
    public void toggleSelected(){

        //toggle the selected enemy
        if(!selected) {

            //put on the health bar
            graphicsGroup.addActor(currentHPImage);
            graphicsGroup.addActor(hpBarImage);


            //make the bouncing arrow
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
            graphicsGroup.removeActor(currentHPImage);
            graphicsGroup.removeActor(hpBarImage);

            selected = false;
        }
    }
    public float getStateTime(){
        return stateTime;
    }
    public Body getBody(){
        return body;
    }

    public void incurDamage(int hp){
        if(hitPoints>0) {
            //make the hp label
            final Label label = new Label(String.valueOf(hp), game.skin, "default-font", Color.CYAN);
            label.setFontScale(3);
            label.setPosition(
                    getX() - 25,
                    getY() + worldHeight
            );
            //make the hp float up and then disappear
            label.addAction(
                    sequence(
                            moveTo(
                                    getX(),
                                    getY() + worldHeight + 50,
                                    2
                            ),
                            fadeOut(1),
                            new Action() {
                                @Override
                                public boolean act(float delta) {
                                    label.remove();
                                    return false;
                                }
                            }
                    )
            );
            graphicsGroup.addActor(label);

            //subtract the hp, and if player dies, then remove
            hitPoints -= hp;

            //update the percentage of hit and magic points left
            percentHitPointsRemaining =(float) hitPoints / maxHitPoints;

            //if player, update the userInterface with this new hp percent
            if(String.valueOf(body.getUserData()).compareTo("Player")==0) {
                //make the player contact invincible
                actorFixtures = body.getFixtureList();
                for(int x = 0; x< actorFixtures.size; x++){
                    Filter myFilter = actorFixtures.get(x).getFilterData();
                    short bits = myFilter.maskBits;
                    bits &= ~Box2DVars.BIT_ENEMY;
                    myFilter.maskBits=bits;
                    actorFixtures.get(x).setFilterData(
                            myFilter
                    );
                }

                game.userInterfaceStage.currentHP.setWidth(game.userInterfaceStage.maxPlayerVitalsWidth * percentHitPointsRemaining);
            }

            //or it's a monster
            else{
                float newWidth =  maxHPImageWidth * percentHitPointsRemaining;
                if(newWidth<0){
                    newWidth = 0 ;
                }

                currentHPImage.setWidth(
                        newWidth
                );
            }


            if (hitPoints <= 0) {
                //remove the box2d body
                game.bodiesToRemove.add(this.getBody());

                //remove the graphic
                graphicsGroup.addAction(
                        sequence(
                                fadeOut(1),
                                new Action() {
                                    @Override
                                    public boolean act(float delta) {

                                        graphicsGroup.remove();
                                        return false;
                                    }
                                }
                        )
                );
                System.out.println("Actor has died");
            }

            //player didn't die, so animate the hit, then once it is done make the player no longer contact invincible
            else {
                if (String.valueOf(body.getUserData()).compareTo("Player")==0) {

                    //reverse the momentum for the hit
                    body.setLinearVelocity(
                            (body.getLinearVelocity().x > 0) ? -4 : 4,
                            (body.getLinearVelocity().y >0) ? -4 : 4
                    );

                    //now add a flashing graphic
                    graphicsGroup.addAction(
                            sequence(
                                    repeat(5,
                                            sequence(
                                                    fadeOut(0.1f),
                                                    fadeIn(0.1f)
                                            )

                                    ),


                                    new Action() {
                                        @Override
                                        public boolean act(float delta) {

                                            //make the player able to be hit again
                                            actorFixtures = body.getFixtureList();
                                            for (int x = 0; x < actorFixtures.size; x++) {
                                                Filter myFilter = actorFixtures.get(x).getFilterData();
                                                short bits = myFilter.maskBits;
                                                bits |= Box2DVars.BIT_ENEMY;
                                                myFilter.maskBits = bits;
                                                actorFixtures.get(x).setFilterData(
                                                        myFilter
                                                );
                                            }
                                            System.out.println("Player can be harmed again!");

                                            return true;
                                        }
                                    }
                            )


                    );
                }
            }
        }
    }

    public void incurDamage(int hp, Animation damageAnimation, float animationDuration){

        if(hitPoints>0) {

            //set up the animation our new way
            final AnimatedImage extraAnimation = new AnimatedImage(damageAnimation);
            extraAnimation.setSize(50,50);
            extraAnimation.setCenterPosition(
                    this.getCenterX(),
                    this.getCenterY()
            );
            extraAnimation.addAction(
                    sequence(
                            delay(1),
                            fadeOut(1),
                            new Action() {
                                @Override
                                public boolean act(float delta) {
                                    extraAnimation.remove();
                                    return false;
                                }
                            }));

            graphicsGroup.addActor(extraAnimation);

            incurDamage(hp);
        }
    }


}
