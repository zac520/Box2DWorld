package com.NZGames.Box2DWorld;

import com.NZGames.Box2DWorld.screens.LoadScreen;
import com.NZGames.Box2DWorld.screens.MenuScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gushikustudios.rube.loader.RubeSceneLoader;

public class MainGame extends Game {

	public static int SCREEN_WIDTH = 320*4;
    public static int SCREEN_HEIGHT = 240*4;
    public AssetManager assets;
    @Override
	public void create () {

        setScreen(new LoadScreen(this));
	}

}
