package com.NZGames.Box2DWorld.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by zac520 on 7/17/14.
 */
public class MyInputProcessor extends Stage {

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
    @Override
         public boolean touchDown(int x, int y, int pointer, int button) {

//        if(x< Gdx.graphics.getWidth()/2) {
//            MyInput.setKey(MyInput.BUTTON2, true);
//        }
//        else{
//            MyInput.setKey(MyInput.BUTTON1, true);
//
//        }

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
//        MyInput.setKey(MyInput.BUTTON1, false);
//        MyInput.setKey(MyInput.BUTTON2, false);


        return true;
    }


}

