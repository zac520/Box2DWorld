package com.NZGames.Box2DWorld.screens;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.Player;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.NZGames.Box2DWorld.handlers.MyContactListener;
import com.NZGames.Box2DWorld.handlers.MyInput;
import com.NZGames.Box2DWorld.handlers.MyInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;

import java.util.Map;

/**
 * Created by zac520 on 8/10/14.
 */
public class GameScreen implements Screen{
    MainGame game;
    private boolean debug = true;
    Stage stage;
    private SpriteBatch batch;
    private World world;
    private MyContactListener cl;

    OrthographicCamera camera;
    OrthographicCamera box2DCam;
    Box2DDebugRenderer box2DRenderer;

    /** Textures **/
    private TextureRegion currentPlayerFrame;
    private TextureRegion ground;

    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private static final float RUNNING_FRAME_DURATION = 0.06f;
    Player player;
    private float accelx;
    public GameScreen(MainGame pGame){
        game = pGame;
        batch = new SpriteBatch();

        //create the world
        //x and y forces, then inactive bodies should "sleep" (true)
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);

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

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("assets/textures/TestLevel2.txt"));
        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("MainCharLeft"));
        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("MainCharRight"));

        //set up the ground for later
        ground = new TextureRegion(atlas.findRegion("WhiteLevel2"));


        //createPlatform();
        loadWorld();

        //add in the input processing
        MyInput.resetKeys();
        Gdx.input.setInputProcessor(new MyInputProcessor());
    }

    public void update(float delta){
        //(step, accuracy of collisions (6 or 8 steps recommended), accuracy
        //of setting bodies after collision (2 recommended))
        world.step(delta, 6, 2);

        handleInput();

        //update the player
        player.update(delta);

        //render the stage
        stage.act();
        stage.draw();

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
        if(player.getIsWalking()) {
            currentPlayerFrame = player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(player.getStateTime(), true) : walkRightAnimation.getKeyFrame(player.getStateTime(), true);
        }
        else{
            currentPlayerFrame = player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(0,true) : walkRightAnimation.getKeyFrame(0,true);
        }
        batch.draw(currentPlayerFrame,
                player.getBody().getPosition().x * Box2DVars.PPM - player.worldWidth /2,
                player.getBody().getPosition().y * Box2DVars.PPM -player.worldHeight/2,
                player.worldWidth,
                player.worldHeight);

        batch.end();

        //set camera to follow player
        camera.position.set(
                player.getBody().getPosition().x * Box2DVars.PPM,
                player.getBody().getPosition().y * Box2DVars.PPM,
                0
        );
        camera.update();


        //draw box2d world
        if(debug) {

            box2DCam.position.set(
                    player.getBody().getPosition().x,
                    player.getBody().getPosition().y,
                    0
            );
            box2DCam.update();
            box2DRenderer.render(world, box2DCam.combined);

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

        //handle accelerometer input
        accelx = Gdx.input.getAccelerometerY();
        //player.getBody().applyForceToCenter(accelx * 2.5f, 0, true);
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
                player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 4f);
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
        if((cl.isPlayerOnGround()) && Math.abs(player.getBody().getLinearVelocity().x)>0){
            player.isWalking = true;
        }
        else{
            player.isWalking = false;
        }


    }
    private void createPlatform(){
        //create platform
        //to create a body we do 5 steps:
        //1.create world
        //  2. Define Body
        //  3. Create body
        //     4. Define Fixture
        //     5. Create Fixture
        //static body does not move, unaffected by forces
        //kinematic bodies: not affected by world forces, but can change velocities (example: moving platform)
        //dynamic bodies do get affected by forces (example: sprite)

        //define platform body
        BodyDef bdef = new BodyDef();
        bdef.position.set((game.SCREEN_WIDTH / Box2DVars.PPM)/2, 15 / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;

        //create body
        Body body = world.createBody(bdef);

        //define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((game.SCREEN_WIDTH / Box2DVars.PPM) /2, 2 / Box2DVars.PPM);

        //define fixture with above shape
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        //create fixture and make a tag setUserData
        body.createFixture(fdef).setUserData("ground");//a tag to identify this later



    }

    public void loadWorld(){

        //TODO figure out exactly what it is that causes the error upon import. I had a clue at the end of the night
        //I copy-pasted the main character into the level and scaled him to fit the new level. At certain sizes, he
        //does not work. I would have to see how they are calculating area, but I suspect it is not correct.
        //I know that a value of 0.0069 for the area of a square will break it, but 0.054 will not. The polygon,
        //though larger, breaks it also. Not sure why, but maybe we can just scale everything always to accomodate?
        //load the char body
        RubeSceneLoader loader = new RubeSceneLoader(world);
        RubeScene scene = loader.addScene(Gdx.files.internal("assets/textures/TestLevel2.json"));

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
                player = new Player(myBodies.get(x), (Float) customInfo.get("width"), (Float) customInfo.get("height")); //make a player with it
                myBodies.get(x).setUserData(player);//make it so we can find it by asking for player

                //create a box around the player to wake sleeping box2d objects
                PolygonShape shape = new PolygonShape();
                FixtureDef fdef = new FixtureDef();
                shape.setAsBox(MainGame.SCREEN_WIDTH/Box2DVars.PPM, MainGame.SCREEN_HEIGHT /Box2DVars.PPM);
                fdef.shape = shape;
                fdef.filter.categoryBits = -1;//-1 is default. this box can awaken and interact with anything
                fdef.filter.maskBits =-1;
                fdef.isSensor = true;
                myBodies.get(x).createFixture(fdef).setUserData("awake");
                shape.dispose();
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
            }

        }


    }

}
