package com.NZGames.Box2DWorld.screens;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.*;
import com.NZGames.Box2DWorld.handlers.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;

import java.util.Map;

/**
 * Created by zac520 on 8/10/14.
 */
public class GameScreen implements Screen{
    /** debug options **/
    private boolean debug = true;
    private boolean useJoystick = true;

    MainGame game;
    Stage stage;
    UserInterface userInterfaceStage;
    public Skin skin;
    private SpriteBatch batch;
    private World world;
    private MyContactListener cl;

    OrthographicCamera camera;
    OrthographicCamera box2DCam;
    OrthographicCamera userInterfaceCam;
    Box2DDebugRenderer box2DRenderer;

    /** Textures **/
    private TextureRegion currentPlayerFrame;
    private TextureRegion ground;
    public TextureAtlas atlas;
    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private static final float RUNNING_FRAME_DURATION = 0.06f;


    Player player;

    private float accelx;

    /**Touchpad Stuff **/
    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    RubeScene scene;
    public GameScreen(MainGame pGame){
        game = pGame;
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));

        //set up the main camera
        camera=new OrthographicCamera();
        camera.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        stage=new Stage();
        stage.getViewport().setCamera(camera);


        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, MainGame.SCREEN_WIDTH / Box2DVars.PPM, MainGame.SCREEN_HEIGHT / Box2DVars.PPM);

        //set up the UI cam with its own separate stage
        userInterfaceCam=new OrthographicCamera();
        userInterfaceCam.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        userInterfaceStage=new UserInterface(game);
        userInterfaceStage.getViewport().setCamera(userInterfaceCam);




        atlas = new TextureAtlas(Gdx.files.internal("assets/textures/TestLevel.txt"));
//        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("MainCharLeft"));
//        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("MainCharRight"));

        //set up the ground for later
        ground = new TextureRegion(atlas.findRegion("WhiteLevel"));

        //createPlatform();
        loadWorld();

        //add in the input processing
        MyInput.resetKeys();
        //need a multiplexor so that the user can touch the level, or the user interface
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(userInterfaceStage);
        Gdx.input.setInputProcessor(multiplexer);




    }

    public void update(float delta){
        //(step, accuracy of collisions (6 or 8 steps recommended), accuracy
        //of setting bodies after collision (2 recommended))
        //world.step(delta, 6, 2);
        scene.step();

        handleInput();

        //update the player
        player.update(delta);

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
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update the world
        update(delta);

        //render the player
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        //if player is not walking, we just give the first frame of facing right or left. Otherwise, we cycle through
//        if(player.getIsWalking()) {
//            currentPlayerFrame = player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(player.getStateTime(), true) : walkRightAnimation.getKeyFrame(player.getStateTime(), true);
//        }
//        else{
//            currentPlayerFrame = player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(0,true) : walkRightAnimation.getKeyFrame(0,true);
//        }
//        batch.draw(currentPlayerFrame,
//                player.getBody().getPosition().x * Box2DVars.PPM - player.worldWidth /2,
//                player.getBody().getPosition().y * Box2DVars.PPM -player.worldHeight/2,
//                player.worldWidth,
//                player.worldHeight);

        batch.end();

        //set camera to follow player
        camera.position.set(
                player.getBody().getPosition().x * Box2DVars.PPM,
                player.getBody().getPosition().y * Box2DVars.PPM,
                0
        );
        camera.update();

        //draw user interface
        batch.setProjectionMatrix(userInterfaceCam.combined);

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
            //if the player is overextending the joystick, move the joystick to accomodate in either direction
            if(userInterfaceStage.getTouchpad().getKnobPercentX()>0.9){
                userInterfaceStage.getTouchpad().setPosition(userInterfaceStage.getTouchpad().getX() + 10f, userInterfaceStage.getTouchpad().getY());
            }
            else if(userInterfaceStage.getTouchpad().getKnobPercentX()< - 0.9){
                userInterfaceStage.getTouchpad().setPosition(userInterfaceStage.getTouchpad().getX() - 10f, userInterfaceStage.getTouchpad().getY());
            }
            //reset if the player drops the touchpad... maybe we shouldn't? Need user tests.
            if(userInterfaceStage.getTouchpad().isTouched()==false){
                userInterfaceStage.getTouchpad().setPosition(15, 15);
            }

            //set acceleration in x direction to 10x the percent of the push (we are just using on/off anyways)
            accelx = userInterfaceStage.getTouchpad().getKnobPercentX() * 10;
        }
        else{
            //handle accelerometer input
            accelx = Gdx.input.getAccelerometerY();
        }


        if(accelx >1){
            if(player.getBody().getLinearVelocity().x < Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(Player.FORWARD_FORCE, 0, true);
            }
        }
        else if (accelx < -1){
            if(player.getBody().getLinearVelocity().x > -Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(-Player.FORWARD_FORCE, 0, true);
            }
        }


        //set player direction
        if(accelx !=0) {
            if (accelx < 0) {
                player.facingLeft = true;

            } else {
                player.facingLeft = false;

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



        //switch block color
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            //System.out.println("hold X");
            MyInput.setKey(MyInput.BUTTON2, false);

        }

        if (MyInput.isDown(MyInput.BUTTON3)) {
            if(player.getBody().getLinearVelocity().x < Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(Player.FORWARD_FORCE, 0, true);
//                player.getBody().setLinearVelocity(
//                        player.getBody().getLinearVelocity().x +Player.FORWARD_FORCE *0.1f,
//                        player.getBody().getLinearVelocity().y);
            }
            player.facingLeft = false;

        }

        if (MyInput.isDown(MyInput.BUTTON4)) {
            if(player.getBody().getLinearVelocity().x > -Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(-Player.FORWARD_FORCE, 0, true);
//                player.getBody().setLinearVelocity(
//                        player.getBody().getLinearVelocity().x -Player.FORWARD_FORCE *0.1f,
//                        player.getBody().getLinearVelocity().y);
            }
            player.facingLeft = true;
        }

        //if player is on the ground and moving left or right, then set walking to true
        if((cl.isPlayerOnGround()) && Math.abs(player.getBody().getLinearVelocity().x)>0.1){
            player.isWalking = true;
        }
        else{
            player.isWalking = false;
        }


    }

    public void loadWorld(){

        //TODO figure out exactly what it is that causes the error upon import. I had a clue at the end of the night
        //I copy-pasted the main character into the level and scaled him to fit the new level. At certain sizes, he
        //does not work. I would have to see how they are calculating area, but I suspect it is not correct.
        //I know that a value of 0.0069 for the area of a square will break it, but 0.054 will not. The polygon,
        //though larger, breaks it also. Not sure why, but maybe we can just scale everything always to accomodate?
        //load the char body

        //load the scene file
        RubeSceneLoader loader = new RubeSceneLoader();
        scene = loader.addScene(Gdx.files.internal("assets/textures/TestLevelSpikes.json"));

        //attach a contact listener
        cl = new MyContactListener();
        scene.getWorld().setContactListener(cl);

        //get all of the bodies that we just loaded in (will have their names as UserData)
        Array<Body> myBodies = scene.getBodies();
        String myString;
        Map customInfo;
        Array<Fixture> bodyFixtures;
        int y = 0;
        //iterate through, and assign each body to its appropriate class
        for(int x = 0; x< myBodies.size; x++){
            myString = (String) myBodies.get(x).getUserData();

            //create the player
            if(myString.compareTo("player")==0){

                //find the fixture with the same name as above
                bodyFixtures = myBodies.get(x).getFixtureList();
                for (y = 0; y< bodyFixtures.size; y++){
                    if(String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("player")==0){
                        break;
                    }
                }
                //get the custom info associated with that fixture (height and width)
                customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                player = new Player(this, myBodies.get(x), (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                myBodies.get(x).setUserData(player);//make it so we can find it by asking for player

                //create a box around the player to wake sleeping box2d objects
                PolygonShape shape = new PolygonShape();
                FixtureDef fdef = new FixtureDef();
                shape.setAsBox(MainGame.SCREEN_WIDTH/Box2DVars.PPM, MainGame.SCREEN_HEIGHT /Box2DVars.PPM);
                fdef.shape = shape;
                fdef.filter.categoryBits = -1;//-1 is default. this box can awaken and interact with anything
                fdef.filter.maskBits = Box2DVars.BIT_ENEMY;
                fdef.isSensor = true;
                myBodies.get(x).createFixture(fdef).setUserData("awake");
                shape.dispose();

                stage.addActor(player.getGroup());
            }

            //create the world (all we are doing here is getting the size and adding the picture to the stage
            else if (myString.compareTo("level")==0){

                //find the fixture with the same User data as above
                bodyFixtures = myBodies.get(x).getFixtureList();
                for (y = 0; y< bodyFixtures.size; y++){
                    if(String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("level")==0){
                        break;
                    }
                }
                customInfo = scene.getCustomPropertiesForItem(myBodies.get(x).getFixtureList().first() ,true);

                //set up the ground
                Image groundStage = new Image(ground);
                groundStage.setSize(
                        (Float) customInfo.get("width") *Box2DVars.PPM,
                        (Float) customInfo.get("height")*Box2DVars.PPM);

                groundStage.setPosition(
                        myBodies.get(x).getPosition().x * Box2DVars.PPM - groundStage.getWidth() /2,
                        myBodies.get(x).getPosition().y * Box2DVars.PPM -groundStage.getHeight() /2);

                stage.addActor(groundStage);

                //get the most recent addition to the actors and send it to the back of the stage.
                //can we think of any better way to do this? It has to be the actor, not the image sent to the back.
                stage.getActors().get(stage.getActors().size -1).toBack();
            }

            else if(myString.compareTo("spike")==0){
                //find the fixture with the same name as above
                bodyFixtures = myBodies.get(x).getFixtureList();
                for (y = 0; y< bodyFixtures.size; y++){
                    if(String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("spike")==0){
                        break;
                    }
                }
                //get the custom info associated with that fixture (height and width)
                customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                SpikeKinematic mySpike = new SpikeKinematic(myBodies.get(x),this, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                myBodies.get(x).setUserData(mySpike);//make it so we can find it by asking


                //add the spike to the stage
                stage.addActor(mySpike);

            }
            else if(myString.compareTo("enemy")==0){
                //find the fixture with the same name as above
                bodyFixtures = myBodies.get(x).getFixtureList();
                for (y = 0; y< bodyFixtures.size; y++){
                    if(String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("enemy")==0){
                        break;
                    }
                }
                //get the custom info associated with that fixture (height and width)
                customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                GenericEnemy myEnemy = new GenericEnemy(myBodies.get(x),this, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                myBodies.get(x).setUserData(myEnemy);//make it so we can find it by asking


                //add the spike to the stage (we are using groups, so that we can add an arrow later)
                stage.addActor(myEnemy.getGroup());

            }
            else if(myString.compareTo("enemy1")==0){
                //find the fixture with the same name as above
                bodyFixtures = myBodies.get(x).getFixtureList();
                for (y = 0; y< bodyFixtures.size; y++){
                    if(String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("enemy1")==0){
                        break;
                    }
                }
                //get the custom info associated with that fixture (height and width)
                customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                Enemy1 myEnemy1 = new Enemy1(myBodies.get(x),this, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                myBodies.get(x).setUserData(myEnemy1);//make it so we can find it by asking


                //add the spike to the stage (we are using groups, so that we can add an arrow later)
                stage.addActor(myEnemy1.getGroup());

            }
        }


    }

}
