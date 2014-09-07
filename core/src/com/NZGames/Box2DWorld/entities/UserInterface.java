package com.NZGames.Box2DWorld.entities;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.MyInput;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
    private Group rightButtonsGroup;

    Skin skin;

    public UserInterface(MainGame myGame, GameScreen myGameScreen){

        skin = new Skin();//new skin for us to set up
        skin.addRegions(myGame.atlas);


        graphicsGroup = new Group();
        rightButtonsGroup = new Group();

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

        //create the sword button
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("swordattackbutton");//up and down are the same for this image
        textButtonStyle.down = skin.getDrawable("Selectedswordattackbutton");
        //create the sword button
        Button swordButton = new Button(textButtonStyle);
        //make it the right hand side of the screen
        swordButton.setPosition(0,0);//start at the left and we will build off of this
        swordButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ) {
                //cast spell
                MyInput.setKey(MyInput.SWORD, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not cast it before, then cancel the attempt
                MyInput.setKey(MyInput.SWORD, false);
            }
        });
        rightButtonsGroup.addActor(swordButton);


        //create the magic button
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("magicattackbutton");//up and down are the same for this image
        textButtonStyle.down = skin.getDrawable("selectedmagicattackbutton");
        Button magicButton = new Button(textButtonStyle);
        //make it the right hand side of the screen
        magicButton.setPosition(swordButton.getWidth(),0);//start just after the sword button
        magicButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ) {
                //cast spell
                MyInput.setKey(MyInput.MAGIC, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not cast it before, then cancel the attempt
                MyInput.setKey(MyInput.MAGIC, false);
            }
        });
        rightButtonsGroup.addActor(magicButton);
        //create the jump button
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("Jumpbutton");//up and down are the same for this image
        textButtonStyle.down = skin.getDrawable("SelectedJumpbutton");
        Button jumpButton = new Button(textButtonStyle);

        //we will place the center of it halfway between the two other buttons, and lift it halfway up to line up with 0
        jumpButton.setCenterPosition(
                (swordButton.getWidth() + magicButton.getWidth()) /2,
                jumpButton.getHeight()/2);
        jumpButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ) {
                //jump
                MyInput.setKey(MyInput.JUMP, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not jump, cancel the attempt
                MyInput.setKey(MyInput.JUMP, false);
            }

        });
        rightButtonsGroup.addActor(jumpButton);

        //position the buttons
        rightButtonsGroup.setScale(
                0.75f,
                0.75f
        );
        rightButtonsGroup.setPosition(
                myGame.SCREEN_WIDTH/2+150,
                0
        );



        graphicsGroup.addActor(rightButtonsGroup);
/*


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
                MyInput.setKey(MyInput.JUMP, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not jump, cancel the attempt
                MyInput.setKey(MyInput.JUMP, false);
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
                MyInput.setKey(MyInput.MAGIC, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not cast it before, then cancel the attempt
                MyInput.setKey(MyInput.MAGIC, false);
            }
        });
        graphicsGroup.addActor(magicButton);

        //create the sword button
        Button swordButton = new Button(myGame.skin, "default");
        //make it the right hand side of the screen
        swordButton.setSize(70, 70);
        swordButton.setPosition((myGame.SCREEN_WIDTH - magicButton.getWidth()) - 75,
                75);
        swordButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ) {
                //cast spell
                MyInput.setKey(MyInput.SWORD, true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button ) {
                //if we could not cast it before, then cancel the attempt
                MyInput.setKey(MyInput.SWORD, false);
            }
        });
        graphicsGroup.addActor(swordButton);
*/



        //add the hp bar
        playerHpBar =  new TextureRegion(myGame.atlas.findRegion("HeroHPMPInterface"));
        playerHPMP = new Image(playerHpBar);
        playerHPMP.setSize(300,125);
        playerHPMP.setPosition(myGame.SCREEN_WIDTH / 4, 10);
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
            MyInput.setKey(MyInput.JUMP, true);
        }

        if(k == Input.Keys.X){
            MyInput.setKey(MyInput.MAGIC, true);

        }
        if(k == Input.Keys.C){
            MyInput.setKey(MyInput.SWORD, true);

        }
        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.MOVE_RIGHT, true);

        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.MOVE_LEFT, true);

        }
        return true;
    }

    public boolean keyUp(int k){
        if(k == Input.Keys.Z){
            MyInput.setKey(MyInput.JUMP, false);
        }

        if(k == Input.Keys.X){
            MyInput.setKey(MyInput.MAGIC, false);

        }
        if(k == Input.Keys.C){
            MyInput.setKey(MyInput.SWORD, false);

        }
        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.MOVE_RIGHT, false);

        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.MOVE_LEFT, false);

        }
        return true;
    }

}
