package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.MyInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by zac520 on 8/17/14.
 */
public class UserInterface extends Stage {

    /**Touchpad Stuff **/
    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    /** UI buttons **/
    private Texture rightSideBackground;

    Skin skin;

    public UserInterface(MainGame myGame){

        skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));

        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("assets/graphics/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("assets/graphics/touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(15, 15, 200, 200);
        this.addActor(touchpad);

        //create the background for the right side buttons
        rightSideBackground = new Texture(Gdx.files.internal("assets/graphics/touchKnob.png"));
        Image rightSideBackgroundImage = new Image(rightSideBackground);
        rightSideBackgroundImage.setSize(900,500);
        rightSideBackgroundImage.setPosition(
                myGame.SCREEN_WIDTH - rightSideBackgroundImage.getWidth()/2 ,
                -rightSideBackgroundImage.getHeight()/2);
        this.addActor(rightSideBackgroundImage);


        //create the jump button
        Button jumpButton = new Button(skin, "default");
        //make it the right hand side of the screen
        jumpButton.setSize(100, 75);
        jumpButton.setPosition(myGame.SCREEN_WIDTH - jumpButton.getWidth() - 300,
                10);
        jumpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                MyInput.setKey(MyInput.BUTTON1, true);
            }
        });
        this.addActor(jumpButton);

    }
    public Touchpad getTouchpad(){
      return touchpad;
    }


    public boolean keyDown(int k){
        if(k == Input.Keys.Z){
            MyInput.setKey(MyInput.BUTTON1, true);
        }

        if(k == Input.Keys.X){
            MyInput.setKey(MyInput.BUTTON2, true);

        }

        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.BUTTON3, true);

        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.BUTTON4, true);

        }
        return true;
    }

    public boolean keyUp(int k){
        if(k == Input.Keys.Z){
            MyInput.setKey(MyInput.BUTTON1, false);
        }

        if(k == Input.Keys.X){
            MyInput.setKey(MyInput.BUTTON2, false);

        }
        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.BUTTON3, false);

        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.BUTTON4, false);

        }
        return true;
    }

}
