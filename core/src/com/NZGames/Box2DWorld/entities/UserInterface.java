package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.MyInput;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
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

    /** graphics stuff**/
    public Image playerHPMP;
    private TextureRegion playerHpBar;
    public Image currentHP;
    private TextureRegion currentHPTexture;
    public Group graphicsGroup;
    public float maxPlayerVitalsWidth=0;

    Skin skin;

    public UserInterface(MainGame myGame, GameScreen myGameScreen){

        graphicsGroup = new Group();

        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new TextureRegion(myGame.atlas.findRegion("Controlpanelleftside")));
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
        graphicsGroup.addActor(touchpad);

        //create the background for the right side buttons
        rightSideBackground =  new TextureRegion(myGame.atlas.findRegion("Controlpanelrightside"));

        Image rightSideBackgroundImage = new Image(rightSideBackground);
        rightSideBackgroundImage.setWidth(400);
        rightSideBackgroundImage.setHeight(200);
        rightSideBackgroundImage.setPosition(
                myGame.SCREEN_WIDTH - rightSideBackgroundImage.getWidth() ,
                rightSideBackgroundImage.getY());
        graphicsGroup.addActor(rightSideBackgroundImage);


        //create the jump button
        Button jumpButton = new Button(myGame.skin, "default");
        //make it the right hand side of the screen
        jumpButton.setSize(70, 70);
        jumpButton.setPosition(myGame.SCREEN_WIDTH - jumpButton.getWidth() - 300,
                35);
        jumpButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ) {
                //jump
                MyInput.setKey(MyInput.BUTTON1, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not jump, cancel the attempt
                MyInput.setKey(MyInput.BUTTON1, false);
            }

        });
        graphicsGroup.addActor(jumpButton);

        //create the magic button
        Button magicButton = new Button(myGame.skin, "default");
        //make it the right hand side of the screen
        magicButton.setSize(70, 70);
        magicButton.setPosition((myGame.SCREEN_WIDTH - magicButton.getWidth()) - 225,
                100);
        magicButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ) {
                //cast spell
                MyInput.setKey(MyInput.BUTTON2, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not cast it before, then cancel the attempt
                MyInput.setKey(MyInput.BUTTON2, false);
            }
        });
        graphicsGroup.addActor(magicButton);


        //add the hp bar
        playerHpBar =  new TextureRegion(myGame.atlas.findRegion("HeroHPMPInterface"));
        playerHPMP = new Image(playerHpBar);
        playerHPMP.setSize(300,125);
        playerHPMP.setPosition(myGame.SCREEN_WIDTH/4,10);
        maxPlayerVitalsWidth = playerHPMP.getWidth()/1.8f;//found this manually. ugh.
        graphicsGroup.addActor(playerHPMP);

            //add the fill for the hp bar
        currentHPTexture =  new TextureRegion(myGame.atlas.findRegion("HeroHPFillBar"));
        currentHP = new Image (currentHPTexture);
        currentHP.setSize(
                maxPlayerVitalsWidth,
                playerHPMP.getHeight()/7
        );
//        currentHP.setPosition(//this is perfect for mp
//                playerHPMP.getX() + playerHPMP.getWidth()/2.9f,
//                playerHPMP.getY() + playerHPMP.getHeight()/2.9f
//        );
        currentHP.setPosition(
                playerHPMP.getX() + playerHPMP.getWidth()/2.85f,
                playerHPMP.getY() + playerHPMP.getHeight()/2f
        );
        graphicsGroup.addActor(currentHP);

        //add the graphics group to the stage
        this.addActor(graphicsGroup);

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
