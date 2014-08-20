package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.handlers.MyInput;
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
    protected GameScreen gameScreen;
    protected float stateTime;
    protected Body body;
    public boolean facingRight = false;

    /** Animation**/
    protected Animation rightAnimation;
    protected Animation leftAnimation;
    protected TextureRegionDrawable myDrawable;
    protected TextureRegion downFacingFrame; //used for turning animation

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

    public GenericActor(){

    }
    public void toggleSelected(){

        //toggle the selected enemy
        if(!selected) {
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
    public float getStateTime(){
        return stateTime;
    }
    public Body getBody(){
        return body;
    }



}
