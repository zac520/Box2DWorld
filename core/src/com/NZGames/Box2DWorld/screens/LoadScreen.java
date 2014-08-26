package com.NZGames.Box2DWorld.screens;

import com.NZGames.Box2DWorld.MainGame;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by zac520 on 8/22/14.
 */
public class LoadScreen implements Screen {
    MainGame game;
    public LoadScreen(MainGame myGame){
        game = myGame;

        //start up the asset manager
        game.assets = new AssetManager();

        game.assets.load("assets/textures/FirstLevel.txt", TextureAtlas.class);



    }

    @Override
    public void render(float delta) {
        if(game.assets.update()){
            // all the assets are loaded, time to load the screen!
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
