package com.NZGames.Box2DWorld.screens;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.*;
import com.NZGames.Box2DWorld.handlers.*;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;

import java.io.File;
import java.util.Map;

/**
 * Created by zac520 on 8/10/14.
 */
public class GameScreen implements Screen{

    /**background stuff**/
    Texture [][] myLevel;

    /** debug options **/
    private boolean debug = false;
    private boolean useJoystick = true;
    private BitmapFont font;

    private float accelx;//used to determine forward acceleration


    /** Game setup **/
    public MainGame game;
    public Stage stage;
    UserInterface userInterfaceStage;
    public Skin skin;
    private SpriteBatch batch;
    private World world;
    private MyContactListener cl;
    OrthographicCamera camera;
    OrthographicCamera box2DCam;
    OrthographicCamera userInterfaceCam;
    Box2DDebugRenderer box2DRenderer;
    RubeScene scene;
    OrthographicCamera backgroundCamera;
    Stage backgroundStage;
    TextureAtlas atlas;

    /** Textures **/
    private TextureRegion currentPlayerFrame;
    private Texture ground;
    private Texture background;

    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private static final float RUNNING_FRAME_DURATION = 0.06f;

    AssetManager manager;
    private Vector2 playerPreviousPosition;
    private Player player;



    /**box2d variables**/
    private float accum = 0f;
    private final float step = 1f / 60f;
    private final float maxAccum = 1f / 20f;


    public GameScreen(MainGame pGame){
        game = pGame;
        batch = new SpriteBatch();
        player = game.player;
        scene = game.scene;

        //set up the main camera
        camera=new OrthographicCamera();
        camera.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);

        //set the stage to the stage loaded in AssetManagement and put into game
        stage= game.stage;
        stage.getViewport().setCamera(camera);
        cl = new MyContactListener(game);
        scene.getWorld().setContactListener(cl);

        //set up the background camera
        backgroundCamera=new OrthographicCamera();
        backgroundCamera.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        backgroundStage= game.backgroundStage;
        backgroundStage.getViewport().setCamera(backgroundCamera);

        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, MainGame.SCREEN_WIDTH / Box2DVars.PPM, MainGame.SCREEN_HEIGHT / Box2DVars.PPM);

//        //set up bodiesToRemove for later
//        bodiesToRemove = new Array<Body>();

        //get the atlas that was loaded by AssetManagement and placed into game
        atlas = game.assets.get("assets/textures/FirstLevel.txt", TextureAtlas.class);


//        //set up the ground for later
//        ground = new Texture(Gdx.files.internal("assets/maps/AlphascreenLevel1_2.png"));
//        background = new Texture(Gdx.files.internal("assets/maps/BackgroundLevel1_1.png"));

        //set up the font
        font = new BitmapFont();


        //set up the UI cam with its own separate stage
        userInterfaceCam=new OrthographicCamera();
        userInterfaceCam.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        userInterfaceStage=new UserInterface(game, this);
        userInterfaceStage.getViewport().setCamera(userInterfaceCam);

        //createPlatform();
        //loadWorld();

        //add in the input processing
        MyInput.resetKeys();
        //need a multiplexor so that the user can touch the level, or the user interface
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(userInterfaceStage);
        Gdx.input.setInputProcessor(multiplexer);

        //start updating the box2dworld
        //updateBox2dWorld();


    }


    public void update(float delta){
        //(step, accuracy of collisions (6 or 8 steps recommended), accuracy
        //of setting bodies after collision (2 recommended))
        //world.step(delta, 6, 2);
//        scene.getWorld().step(delta,6,2);

        accum += delta;
        accum = Math.min(accum, maxAccum);
        while (accum > step) {
            scene.getWorld().step(step,6,2);
            accum -= step;
        }
        scene.getWorld().step(accum,6,2);
        accum = 0;

        //remove enemies if necessary
        for(int i = 0; i< game.bodiesToRemove.size; i++){
            Body b = game.bodiesToRemove.get(i);
            scene.getWorld().destroyBody(b);
            //player.collectCrystal(); this sort of thing will be used to give the player experience later
        }
        game.bodiesToRemove.clear();


        playerPreviousPosition = player.getBody().getPosition();
        //update the player
        handleInput();
        player.update(delta);


        //render the background
        backgroundStage.act(delta);
        backgroundStage.draw();

        //render the stage
        stage.act(delta);
        stage.draw();


        //render the UI stage
        userInterfaceStage.act(delta);
        userInterfaceStage.draw();
    }

    @Override
    public void render(float delta) {

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update the world
        update(delta);

        //render the player
        batch.begin();
        batch.setProjectionMatrix(camera.combined);


        batch.end();

        //set camera to follow player
        camera.position.set(
                player.getBody().getPosition().x * Box2DVars.PPM,
                player.getBody().getPosition().y * Box2DVars.PPM,
                0
        );
        camera.update();


        //move the background on an offset from the player
        backgroundCamera.position.set(
                camera.position.x + ((camera.position.x - playerPreviousPosition.x) *0.1f) ,
                camera.position.y + ((camera.position.y - playerPreviousPosition.y) *0.1f),
                0
        );
        backgroundCamera.update();


        //draw user interface
        batch.setProjectionMatrix(userInterfaceCam.combined);


        //show frames per second
        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 20, game.SCREEN_HEIGHT -30);
        batch.end();

        //draw box2d world
        if(debug) {

            box2DCam.position.set(
                    player.getBody().getPosition().x,
                    player.getBody().getPosition().y,
                    0
            );
            box2DCam.update();
            box2DRenderer.render(scene.getWorld(), box2DCam.combined);


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
    public void handleInput() {




        //using the joystick
        if (useJoystick) {
//            //if the player is overextending the joystick, move the joystick to accomodate in either direction
//            if(userInterfaceStage.getTouchpad().getKnobPercentX()>0.9){
//                userInterfaceStage.getTouchpad().setPosition(userInterfaceStage.getTouchpad().getX() + 10f, userInterfaceStage.getTouchpad().getY());
//            }
//            else if(userInterfaceStage.getTouchpad().getKnobPercentX()< - 0.9){
//                userInterfaceStage.getTouchpad().setPosition(userInterfaceStage.getTouchpad().getX() - 10f, userInterfaceStage.getTouchpad().getY());
//            }
//            //reset if the player drops the touchpad... maybe we shouldn't? Need user tests.
//            if(userInterfaceStage.getTouchpad().isTouched()==false){
//                userInterfaceStage.getTouchpad().setPosition(15, 15);
//            }

            //set acceleration in x direction to 10x the percent of the push (we are just using on/off anyways)
            accelx = userInterfaceStage.getTouchpad().getKnobPercentX() * 10;
        }
        else{
            //handle accelerometer input
            accelx = Gdx.input.getAccelerometerY();
        }


        if((accelx >1) || (MyInput.isDown(MyInput.BUTTON3))) {
            if(player.getBody().getLinearVelocity().x < player.PLAYER_MAX_SPEED) {
               player.getBody().setLinearVelocity(
                        player.getBody().getLinearVelocity().x + player.FORWARD_FORCE *0.01f,
                        player.getBody().getLinearVelocity().y);

                player.facingRight = true;

            }
        }
        else if ((accelx <-1) || (MyInput.isDown(MyInput.BUTTON4))) {
            if(player.getBody().getLinearVelocity().x > -player.PLAYER_MAX_SPEED) {
                //player.getBody().applyForceToCenter(-player.FORWARD_FORCE, 0, true);
                player.getBody().setLinearVelocity(
                        player.getBody().getLinearVelocity().x -player.FORWARD_FORCE *0.01f,
                        player.getBody().getLinearVelocity().y);

                player.facingRight = false;

            }
        }


        //playerJump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            //System.out.println("pressed Z");
            if (cl.isPlayerOnGround()) {
                //force is in newtons
                player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.JUMPING_FORCE);
                //player.getBody().applyForceToCenter(0, 175, true);
                MyInput.setKey(MyInput.BUTTON1, false);

            }
        }



        //inflict damage and play animation of the spell
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            //TODO this is a bit hacky. We have an animation loaded into the player class, that runs then destroys the character
            //we will probably want the character to respawn later, but for now this is what we are doing
            if(game.selectedEnemy != null) {
                game.selectedEnemy.incurDamage(3, player.spellAnimation, 1);
            }

            MyInput.setKey(MyInput.BUTTON2, false);

        }



        //if player is on the ground and moving left or right, then set walking to true
        if((cl.isPlayerOnGround()) && Math.abs(player.getBody().getLinearVelocity().x)>0.1){
            player.isWalking = true;
        }
        else{
            player.isWalking = false;
        }


    }



}
