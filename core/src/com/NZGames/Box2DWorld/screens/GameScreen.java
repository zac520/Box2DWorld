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

    /** Textures **/
    private TextureRegion currentPlayerFrame;
    private Texture ground;
    private Texture background;
    public TextureAtlas atlas;

    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private static final float RUNNING_FRAME_DURATION = 0.06f;

    /** Actors **/
    Player player;
    GenericActor selectedEnemy; //we will use this to track which body the player has selected, so only one can be selected at a time
    AssetManager manager;

    /** Perhaps Should be in a "gamestate" class? **/
    public Array<Body> bodiesToRemove;
    private Vector2 playerPreviousPosition;

    public GameScreen(MainGame pGame){
        game = pGame;
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));

        //set up the main camera
        camera=new OrthographicCamera();
        camera.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        stage=new Stage();
        stage.getViewport().setCamera(camera);

        //set up the background camera
        backgroundCamera=new OrthographicCamera();
        backgroundCamera.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        backgroundStage=new Stage();
        backgroundStage.getViewport().setCamera(backgroundCamera);

        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, MainGame.SCREEN_WIDTH / Box2DVars.PPM, MainGame.SCREEN_HEIGHT / Box2DVars.PPM);

        //set up bodiesToRemove for later
        bodiesToRemove = new Array<Body>();
        atlas = game.assets.get("assets/textures/FirstLevel.txt", TextureAtlas.class);
//        atlas = new TextureAtlas(Gdx.files.internal("assets/textures/FirstLevel.txt"));
//        AssetManager manager=new AssetManager();
//        manager.load("assets/textures/Level1.txt", TextureAtlas.class);
//        manager.finishLoading();
//        atlas = manager.get("assets/textures/Level1.txt", TextureAtlas.class);
        //atlas = new TextureAtlas(Gdx.files.internal("assets/textures/Level1.txt"));
//        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("MainCharLeft"));
//        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("MainCharRight"));

        //set up the ground for later
        ground = new Texture(Gdx.files.internal("assets/maps/AlphascreenLevel1_2.png"));
        background = new Texture(Gdx.files.internal("assets/maps/BackgroundLevel1_1.png"));

        //set up the font
        font = new BitmapFont();


        //set up the UI cam with its own separate stage
        userInterfaceCam=new OrthographicCamera();
        userInterfaceCam.setToOrtho(false, MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT);
        userInterfaceStage=new UserInterface(game, this);
        userInterfaceStage.getViewport().setCamera(userInterfaceCam);

        //createPlatform();
        loadWorld();

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

    public void updateBox2dWorld(){
        scene.getWorld().step(0.02f,6,2);
    }
    public void update(float delta){
        //(step, accuracy of collisions (6 or 8 steps recommended), accuracy
        //of setting bodies after collision (2 recommended))
        //world.step(delta, 6, 2);
        //scene.getWorld().step(0.02f,6,2);
        updateBox2dWorld();

        //remove enemies if necessary
        for(int i = 0; i< bodiesToRemove.size; i++){
            Body b = bodiesToRemove.get(i);
            scene.getWorld().destroyBody(b);
            //player.collectCrystal(); this sort of thing will be used to give the player experience later
        }
        bodiesToRemove.clear();


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
        //userInterfaceStage.act(delta);
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
            if(selectedEnemy != null) {
                selectedEnemy.incurDamage(3, player.spellAnimation, 1);
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

    /**
     * This function will be called from an Actor to let this GameScreen class know which Actor is selected
     * and toggle them accordingly
     * @param myEnemy
     */
    public void selectEnemy(GenericActor myEnemy){

        if(selectedEnemy!=null) {
            if (selectedEnemy == myEnemy) {//if the body is already selected, then we unselect.
                selectedEnemy.toggleSelected();
                selectedEnemy = null;
                return;
            } else {
                //let the old body know we are unselecting it
                selectedEnemy.toggleSelected();

                //set it to the new enemy
                selectedEnemy = myEnemy;

                //toggle the new enemy
                selectedEnemy.toggleSelected();
            }

        }
        else{
            selectedEnemy = myEnemy;
            selectedEnemy.toggleSelected();
        }
    }
    public void loadWorld(){

        //load the scene file
        RubeSceneLoader loader = new RubeSceneLoader();
        scene = loader.addScene(Gdx.files.internal("assets/textures/FirstLevel.json"));

        //attach a contact listener
        cl = new MyContactListener(this);
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
                fdef.filter.categoryBits = Box2DVars.BIT_ENEMY;//right now this box shares enemy bit and can only awaken enemies
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
                        customInfo = scene.getCustomPropertiesForItem(myBodies.get(x).getFixtureList().get(y) ,true);





                        //test area
                        FileHandle [] filehandles;
                        String folder = "assets/maps/test";
                        filehandles = Gdx.files.internal(folder).list();
                        int rowCount=0;
                        int columnCount=0;
                        int rowNumber=0;
                        int columnNumber=0;


                        //get the row and column count in the directory (skip folders..)
                        //TODO right now this works for single digit only... need to make one for double digit extensions
                        //also, will need to move this to the loading screen, as it takes awhile.
                        for (int k = 0; k< filehandles.length; k++) {
                            if (filehandles[k].isDirectory()) {
                                continue;
                            } else {
                                //we have a filename. Now lets work with it
                                String filename = filehandles[k].name();
                                //for our current workflow, this will be the row number
                                rowNumber = Character.getNumericValue(filename.charAt(filename.length()-5));
                                columnNumber = Character.getNumericValue(filename.charAt(filename.length()-7));

                                if(rowNumber>=rowCount){
                                    rowCount ++;
                                }
                                if(columnNumber>=columnCount){
                                    columnCount ++;
                                }
                            }
                        }
                        //create the 2d array to the size we learned from above
                        myLevel = new Texture[rowCount][columnCount];

                        //set the elements of the array, iterating through the same folder
                        for (int k = 0; k< filehandles.length; k++) {
                            if (filehandles[k].isDirectory()) {
                                //listFilesForFolder(fileEntry);
                                continue;
                            } else {
                                //System.out.println(fileEntry.getName());
                                //we have a filename. Now lets work with it
                                String filename = filehandles[k].name();
                                //for our current workflow, this will be the row number
                                rowNumber = Character.getNumericValue(filename.charAt(filename.length() - 5));
                                columnNumber = Character.getNumericValue(filename.charAt(filename.length() - 7));

                                myLevel[rowNumber][columnNumber] = new Texture(Gdx.files.internal(folder + "/" + filename));
                            }
                        }


                        //get the total width of the stage to get pixel per width
                        int pixelCount=0;
                        Float width = (Float) customInfo.get("width") *Box2DVars.PPM;
                        float widthPerPixel;
                        int currentWidthPixel=0;
                        for(columnNumber=0; columnNumber<columnCount;columnNumber++){
                            pixelCount += myLevel[0][columnNumber].getWidth();

                        }
                        widthPerPixel = width/pixelCount;

                        //get the total hieght of the stage to get per pixel height
                        pixelCount=0;
                        Float height = (Float) customInfo.get("height") *Box2DVars.PPM;
                        float heightPerPixel;
                        int currentHeightPixel=0;
                        for(rowNumber=0; rowNumber<rowCount;rowNumber++){
                            pixelCount += myLevel[rowNumber][0].getHeight();
                        }
                        heightPerPixel = height/pixelCount;

                        //iterate through the array, adding the appropriate size and position to the stage
                        for( rowNumber = rowCount-1; rowNumber>=0; rowNumber--) {//we will build from the bottom up

                            //iterate through all of the columns in this row
                            for (columnNumber = 0; columnNumber < columnCount; columnNumber++) {
                                Image myImage = new Image(myLevel[rowNumber][columnNumber]);

                                myImage.setSize(
                                        widthPerPixel * myImage.getWidth(),
                                        heightPerPixel * myImage.getHeight());

                                myImage.setPosition(
                                        currentWidthPixel,
                                        currentHeightPixel +20);//not sure we why need to subtract 20...

                                stage.addActor(myImage);
                                stage.getActors().get(stage.getActors().size - 1).toBack();

                                //add this width to the current width and same for height
                                currentWidthPixel += myImage.getWidth();
                            }
                            currentWidthPixel = 0;
                            currentHeightPixel += heightPerPixel * myLevel[rowNumber][0].getHeight();

                        }

                        //end test area




//                        //set up the ground
//                        Image groundStage = new Image(ground);
//                        groundStage.setSize(
//                                (Float) customInfo.get("width") *Box2DVars.PPM ,
//                                (Float) customInfo.get("height")*Box2DVars.PPM);
//
//                        //TODO figure out why we need an offset of 20 for this positioning.. We may need to better center the image
//                        //in RUBE
//                        groundStage.setPosition(
//                                myBodies.get(x).getPosition().x * Box2DVars.PPM - groundStage.getWidth() /2,
//                                (myBodies.get(x).getPosition().y * Box2DVars.PPM -groundStage.getHeight() /2) +20);
//                        stage.addActor(groundStage);
//                        stage.getActors().get(stage.getActors().size -1).toBack();


                        //for now, just make the background move at the same speed (so just make a background under foreground)
                        Image groundStageBackground = new Image(background);
                        groundStageBackground.setSize(
                                (Float) customInfo.get("width") *Box2DVars.PPM ,
                                (Float) customInfo.get("height")*Box2DVars.PPM);

                        groundStageBackground.setPosition(
                                myBodies.get(x).getPosition().x * Box2DVars.PPM - groundStageBackground.getWidth() /2,
                                (myBodies.get(x).getPosition().y * Box2DVars.PPM -groundStageBackground.getHeight() /2) +20);
                        backgroundStage.addActor(groundStageBackground);
//
//
//
//                        //get the most recent addition to the actors and send it to the back of the stage.
//                        //can we think of any better way to do this? It has to be the actor, not the image sent to the back.
                        backgroundStage.getActors().get(backgroundStage.getActors().size -1).toBack();
                        break;
                    }
                }

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
            else if(myString.compareTo("enemy2")==0){
                //find the fixture with the same name as above
                bodyFixtures = myBodies.get(x).getFixtureList();
                for (y = 0; y< bodyFixtures.size; y++){
                    if(String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("enemy2")==0){
                        break;
                    }
                }
                //get the custom info associated with that fixture (height and width)
                customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                Enemy2 myEnemy2 = new Enemy2(myBodies.get(x),this, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                myBodies.get(x).setUserData(myEnemy2);//make it so we can find it by asking


                //add the spike to the stage (we are using groups, so that we can add an arrow later)
                stage.addActor(myEnemy2.getGroup());

            }



        }


    }
    public void listFilesForFolder(final File folder) {
        //http://stackoverflow.com/questions/1844688/read-all-files-in-a-folder
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                //System.out.println(fileEntry.getName());
                //we have a filename. Now lets work with it
                String filename = fileEntry.getName();
                //for our current workflow, this will be the row number
                int rowNumber = filename.charAt(filename.length()-5);
                int columnNumber = filename.charAt(filename.length()-7);

            }
        }
    }
}
