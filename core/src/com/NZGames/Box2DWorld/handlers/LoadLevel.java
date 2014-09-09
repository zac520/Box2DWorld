package com.NZGames.Box2DWorld.handlers;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.actors.Enemy1;
import com.NZGames.Box2DWorld.entities.actors.Enemy2;
import com.NZGames.Box2DWorld.entities.actors.Player;
import com.NZGames.Box2DWorld.entities.actors.SpikeKinematic;
import com.NZGames.Box2DWorld.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;

import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 8/22/14.
 */
public class LoadLevel implements Screen {

    MainGame game;

    private boolean gameIsLoaded = false;

    /** LoadScreen assets **/
    Animation rightAnimation;
    private float RUNNING_FRAME_DURATION = 0.2f;
    Stage stage;
    BitmapFont font;
    OrthographicCamera camera;
    TextureAtlas atlas;
    SpriteBatch batch;
    String filepath;
    String filename;

    GameScreen gameScreen;

    /**background stuff**/
    public Texture[][] myLevel;
    public Array <String> filenames;
    private Texture ground;
    private Texture background;

    public Stage gameStage;
    public RubeScene scene;
    public Player player;
    Stage backgroundStage;

    private float percentLoaded;


    public LoadLevel(MainGame myGame, String filePath, String fileName){
        game = myGame;

        filepath = filePath;
        filename = fileName;

        gameIsLoaded = false;
        batch = new SpriteBatch();

        //start up the asset manager
        game.assets = new AssetManager();

        filenameLoad();

        //have the asset manager load each piece of the screen
        for(int x = 0; x< filenames.size; x++){
            game.assets.load(filenames.get(x), Texture.class);
        }

        //have the asset manager get the rest of the graphics
        //game.assets.load(filepath +"Atlas/"+ filename + ".txt", TextureAtlas.class);
        game.assets.load(filepath + "Background/" + filename + ".png", Texture.class);


        //the rest of this is just the loading image
        // create viewport
        camera=new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
        stage=new Stage();
        stage.getViewport().setCamera(camera);

        //create font
        font = new BitmapFont();

        //set up the background camera
        backgroundStage=new Stage();

        game.atlas = new TextureAtlas(filepath +"Atlas/"+ filename +".txt");
        rightAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("HerowSword_RV1"));
        rightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        AnimatedImage myAnimatedImage = new AnimatedImage(rightAnimation);
        myAnimatedImage.setCenterPosition(game.SCREEN_WIDTH / 2, game.SCREEN_HEIGHT / 2);
        stage.addActor(myAnimatedImage);


    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // let the stage act and draw
        stage.act(delta);
        stage.draw();

        // draw our text
        percentLoaded = game.assets.getProgress();
        percentLoaded -= 0.1f;//subtract 10% because the last step will not show
        percentLoaded *=100;//turn into percent
        batch.begin();
        font.draw(batch, String.valueOf(((int) percentLoaded))+"%", 50, 80);
        batch.end();


        if(game.assets.update()){


            // all the assets are loaded, time to load the screen!


            //remove all of the loading screen stuff before adding in the new stuff
            stage.clear();

            //load the world (we repeat a bit of code from the steps above... could be more efficient, but only a little
            loadWorld();

            //load the next screen
            game.player = player;
            game.stage = stage;
            game.backgroundStage = backgroundStage;
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


    public void loadWorld() {
            //load the scene file
            RubeSceneLoader loader = new RubeSceneLoader();
            scene = loader.addScene(Gdx.files.internal(filepath + "Box2d/" + filename + ".json"));
            game.scene = scene;


            //get all of the bodies that we just loaded in (will have their names as UserData)
            Array<Body> myBodies = scene.getBodies();
            String myString;
            Map customInfo;
            Array<Fixture> bodyFixtures;
            int y = 0;
            //iterate through, and assign each body to its appropriate class
            for (int x = 0; x < myBodies.size; x++) {
                myString = (String) myBodies.get(x).getUserData();

                //create the player
                if (myString.compareTo("player") == 0) {

                    //find the fixture with the same name as above
                    bodyFixtures = myBodies.get(x).getFixtureList();
                    for (y = 0; y < bodyFixtures.size; y++) {
                        if (String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("player") == 0) {
                            break;
                        }
                    }
                    //get the custom info associated with that fixture (height and width)
                    customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                    //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                    player = new Player(game, myBodies.get(x), (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                    myBodies.get(x).setUserData(player);//make it so we can find it by asking for player

                    //create a box around the player to wake sleeping box2d objects
                    PolygonShape shape = new PolygonShape();
                    FixtureDef fdef = new FixtureDef();
                    shape.setAsBox(MainGame.SCREEN_WIDTH/1.5f / Box2DVars.PPM, MainGame.SCREEN_HEIGHT/1.5f / Box2DVars.PPM);
                    fdef.shape = shape;
                    fdef.filter.categoryBits = Box2DVars.BIT_GROUND;//right now this box shares enemy bit and can only awaken enemies
                    fdef.filter.maskBits = Box2DVars.BIT_ENEMY;
                    fdef.isSensor = true;
                    myBodies.get(x).createFixture(fdef).setUserData("awake");
                    shape.dispose();

                    stage.addActor(player.getGroup());
                }

                //create the world (all we are doing here is getting the size and adding the picture to the stage
                else if (myString.compareTo("level") == 0) {

                    //find the fixture with the same User data as above
                    bodyFixtures = myBodies.get(x).getFixtureList();
                    for (y = 0; y < bodyFixtures.size; y++) {
                        if (String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("level") == 0) {
                            customInfo = scene.getCustomPropertiesForItem(myBodies.get(x).getFixtureList().get(y), true);


                            //test area
                            FileHandle[] filehandles;
                            String folder = filepath + "LevelPieces/";
                            filehandles = Gdx.files.internal(folder).list();
                            int rowCount = 0;
                            int columnCount = 0;
                            int rowNumber = 0;
                            int columnNumber = 0;


                            //get the row and column count in the directory (skip folders..)
                            //TODO right now this works for single digit only... need to make one for double digit extensions
                            //also, will need to move this to the loading screen, as it takes awhile.
                            for (int k = 0; k < filehandles.length; k++) {
                                if (filehandles[k].isDirectory()) {
                                    continue;
                                } else {
                                    //we have a filename. Now lets work with it
                                    String filename = filehandles[k].name();
                                    //for our current workflow, this will be the row number
                                    rowNumber = Character.getNumericValue(filename.charAt(filename.length() - 5));
                                    columnNumber = Character.getNumericValue(filename.charAt(filename.length() - 7));

                                    if (rowNumber >= rowCount) {
                                        rowCount++;
                                    }
                                    if (columnNumber >= columnCount) {
                                        columnCount++;
                                    }
                                }
                            }
                            //create the 2d array to the size we learned from above
                            myLevel = new Texture[rowCount][columnCount];

                            //set the elements of the array, iterating through the same folder
                            for (int k = 0; k < filehandles.length; k++) {
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

                                    myLevel[rowNumber][columnNumber] = game.assets.get(folder + filename);
                                }
                            }


                            //get the total width of the stage to get pixel per width
                            int pixelCount = 0;
                            Float width = (Float) customInfo.get("width") * Box2DVars.PPM;
                            float widthPerPixel;
                            int currentWidthPixel = 0;
                            for (columnNumber = 0; columnNumber < columnCount; columnNumber++) {
                                pixelCount += myLevel[0][columnNumber].getWidth();

                            }
                            widthPerPixel = width / pixelCount;

                            //get the total hieght of the stage to get per pixel height
                            pixelCount = 0;
                            Float height = (Float) customInfo.get("height") * Box2DVars.PPM;
                            float heightPerPixel;
                            int currentHeightPixel = 0;
                            for (rowNumber = 0; rowNumber < rowCount; rowNumber++) {
                                pixelCount += myLevel[rowNumber][0].getHeight();
                            }
                            heightPerPixel = height / pixelCount;

                            //iterate through the array, adding the appropriate size and position to the stage
                            for (rowNumber = rowCount - 1; rowNumber >= 0; rowNumber--) {//we will build from the bottom up

                                //iterate through all of the columns in this row
                                for (columnNumber = 0; columnNumber < columnCount; columnNumber++) {
                                    Image myImage = new Image(myLevel[rowNumber][columnNumber]);

                                    myImage.setSize(
                                            widthPerPixel * myImage.getWidth(),
                                            heightPerPixel * myImage.getHeight());

                                    myImage.setPosition(
                                            currentWidthPixel,
                                            currentHeightPixel + 20);//not sure we why need to subtract 20...

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
                            background = game.assets.get(filepath + "Background/" + filename + ".png");
                            Image groundStageBackground = new Image(background);
                            groundStageBackground.setSize(
                                    (Float) customInfo.get("width") * Box2DVars.PPM,
                                    (Float) customInfo.get("height") * Box2DVars.PPM);

                            groundStageBackground.setPosition(
                                    myBodies.get(x).getPosition().x * Box2DVars.PPM - groundStageBackground.getWidth() / 2,
                                    (myBodies.get(x).getPosition().y * Box2DVars.PPM - groundStageBackground.getHeight() / 2) + 20);
                            backgroundStage.addActor(groundStageBackground);
//
//
//
//                        //get the most recent addition to the actors and send it to the back of the stage.
//                        //can we think of any better way to do this? It has to be the actor, not the image sent to the back.
                            backgroundStage.getActors().get(backgroundStage.getActors().size - 1).toBack();
                            break;
                        }
                    }

                } else if (myString.compareTo("spike") == 0) {
                    //find the fixture with the same name as above
                    bodyFixtures = myBodies.get(x).getFixtureList();
                    for (y = 0; y < bodyFixtures.size; y++) {
                        if (String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("spike") == 0) {
                            break;
                        }
                    }
                    //get the custom info associated with that fixture (height and width)
                    customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                    //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                    SpikeKinematic mySpike = new SpikeKinematic(myBodies.get(x), game, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                    myBodies.get(x).setUserData(mySpike);//make it so we can find it by asking


                    //add the spike to the stage
                    stage.addActor(mySpike);

                } else if (myString.compareTo("enemy1") == 0) {
                    //find the fixture with the same name as above
                    bodyFixtures = myBodies.get(x).getFixtureList();
                    for (y = 0; y < bodyFixtures.size; y++) {
                        if (String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("enemy1") == 0) {
                            break;
                        }
                    }
                    //get the custom info associated with that fixture (height and width)
                    customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                    //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                    Enemy1 myEnemy1 = new Enemy1(myBodies.get(x), game, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                    myBodies.get(x).setUserData(myEnemy1);//make it so we can find it by asking


                    //add the spike to the stage (we are using groups, so that we can add an arrow later)
                    stage.addActor(myEnemy1.getGroup());

                } else if (myString.compareTo("enemy2") == 0) {
                    //find the fixture with the same name as above
                    bodyFixtures = myBodies.get(x).getFixtureList();
                    for (y = 0; y < bodyFixtures.size; y++) {
                        if (String.valueOf(bodyFixtures.get(y).getUserData()).compareTo("enemy2") == 0) {
                            break;
                        }
                    }
                    //get the custom info associated with that fixture (height and width)
                    customInfo = scene.getCustomPropertiesForItem(bodyFixtures.get(y), true);


                    //Oddly, we cannot cast to (float), we must cast to (Float). Java silliness.
                    Enemy2 myEnemy2 = new Enemy2(myBodies.get(x), game, (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                    myBodies.get(x).setUserData(myEnemy2);//make it so we can find it by asking


                    //add the spike to the stage (we are using groups, so that we can add an arrow later)
                    stage.addActor(myEnemy2.getGroup());

                }


            }

            //sleep all of the bodies except for the player
            for (int x = 0; x < myBodies.size; x++) {
                if (String.valueOf(myBodies.get(x).getUserData()).compareTo("Player") != 0) {
                    myBodies.get(x).setAwake(false);
                }

            }


        }


    //load up all of the filenames
    private void filenameLoad() {
        FileHandle[] filehandles;
        String folder = filepath + "LevelPieces";
        filehandles = Gdx.files.internal(folder).list();
        filenames = new Array<String>();
        //get the row and column count in the directory (skip folders..)
        //TODO right now this works for single digit only... need to make one for double digit extensions
        //also, will need to move this to the loading screen, as it takes awhile.
        for (int k = 0; k < filehandles.length; k++) {
            if (filehandles[k].isDirectory()) {
                continue;
            } else {

                filenames.add(filehandles[k].path());

            }
        }
    }

}
