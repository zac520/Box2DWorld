package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.MyInput;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private TextureRegion rightSideBackground;

    Skin skin;

    public UserInterface(MainGame myGame, GameScreen myGameScreen){


        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new TextureRegion(myGameScreen.atlas.findRegion("Controlpanelleftside")));
        //touchpadSkin.add("touchBackground", new Texture("assets/graphics/touchBackground.png"));
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
        rightSideBackground =  new TextureRegion(myGameScreen.atlas.findRegion("Controlpanelrightside"));

        Image rightSideBackgroundImage = new Image(rightSideBackground);
        rightSideBackgroundImage.setWidth(400);
        rightSideBackgroundImage.setHeight(200);
        rightSideBackgroundImage.setPosition(
                myGame.SCREEN_WIDTH - rightSideBackgroundImage.getWidth() ,
                rightSideBackgroundImage.getY());
        this.addActor(rightSideBackgroundImage);


        //create the jump button
        Button jumpButton = new Button(myGameScreen.skin, "default");
        //make it the right hand side of the screen
        jumpButton.setSize(70, 70);
        jumpButton.setPosition(myGame.SCREEN_WIDTH - jumpButton.getWidth() - 300,
                35);
        jumpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                MyInput.setKey(MyInput.BUTTON1, true);
            }
        });
        this.addActor(jumpButton);

        //create the magic button
        Button magicButton = new Button(myGameScreen.skin, "default");
        //make it the right hand side of the screen
        magicButton.setSize(70, 70);
        magicButton.setPosition((myGame.SCREEN_WIDTH - magicButton.getWidth()) - 225,
                100);
        magicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                MyInput.setKey(MyInput.BUTTON2, true);
            }
        });
        this.addActor(magicButton);

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
