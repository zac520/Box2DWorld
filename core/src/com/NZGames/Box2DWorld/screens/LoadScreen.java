package com.NZGames.Box2DWorld.screens;

import com.NZGames.Box2DWorld.MainGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 8/22/14.
 */
public class LoadScreen implements Screen {
    MainGame game;


    TextureRegion christmasTree;
    TextureRegion ball;
    TextureRegion snow;
    Stage stage;
    BitmapFont font;
    OrthographicCamera camera;
    TextureAtlas atlas;

    public LoadScreen(MainGame myGame){
        game = myGame;

        //start up the asset manager
        game.assets = new AssetManager();

        game.assets.load("assets/textures/FirstLevel.txt", TextureAtlas.class);


        //the rest of this is just the loading image
//texture packer puts all the graphics in a single file with an "atlas"
        //to find their coordinates in the file. This locates them.
        atlas = new TextureAtlas(Gdx.files.internal("assets/textures/testPack.txt"));
        christmasTree=atlas.findRegion("tree");
        ball=atlas.findRegion("ball");
        snow=atlas.findRegion("snow");


        //create font
        font = new BitmapFont();

        // create viewport
        camera=new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        stage=new Stage();
        stage.getViewport().setCamera(camera);


        // our christmas tree
        Image ctree = new Image(christmasTree);
        ctree.setSize(296, 480); // scale the tree to the right size
        ctree.setPosition(-300, 0);
        ctree.addAction(moveTo(400 - 148, 0, 1f));
        ctree.setZIndex(0);
        stage.addActor(ctree);

        //the ornament that rotates in
        Image ballImage = new Image(ball);
        ballImage.setPosition(400 - 148 + 60, 170);

        ballImage.setOrigin(32, 32);
        ballImage.setColor(1, 1, 1, 0);
        ballImage.addAction(
                sequence(delay(1),
                        parallel(
                                fadeIn(1),
                                rotateBy(360, 1)),
                        delay(2f),
                        new Action() {
                            // custom action to switch to the menu screen
                            @Override
                            public boolean act(float delta) {
                                game.setScreen(new MenuScreen(game));
                                return false;
                            }
                        }));

        stage.addActor(ballImage);

        // create the snowflakes
        for (int i = 0; i < 10; i++) {
            spawnSnowflake();
        }

    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // let the stage act and draw
        stage.act(delta);
        stage.draw();

        // draw our text
        stage.getSpriteBatch().begin();
        font.draw(stage.getSpriteBatch(), "Loading the game!", 50, 80);
        stage.getSpriteBatch().end();


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
    public void spawnSnowflake() {
        final Image snowflake = new Image(snow);
        snowflake.setOrigin(64, 64);
        int x = (int) (Math.random() * 800);
        snowflake.setPosition(x, 480);
        snowflake.setScale((float) (Math.random() * 0.8f + 0.2f));
        snowflake.addAction(parallel(
                forever(rotateBy(360, (float) (Math.random() * 6))),
                sequence(moveTo(x, 0, (float) (Math.random() * 15)),
                        fadeOut((float) (Math.random() * 1)), new Action() { // we
                            // can
                            // define
                            // custom
                            // actions
                            // :)

                            @Override
                            public boolean act(float delta) {
                                snowflake.remove(); // delete this snowflake
                                spawnSnowflake(); // spawn a new snowflake
                                return false;
                            }
                        })));
        stage.addActor(snowflake);
    }


}
