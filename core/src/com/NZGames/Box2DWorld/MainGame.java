package com.NZGames.Box2DWorld;

import com.NZGames.Box2DWorld.entities.actors.GenericActor;
import com.NZGames.Box2DWorld.entities.actors.Player;
import com.NZGames.Box2DWorld.entities.UserInterface;
import com.NZGames.Box2DWorld.screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;

import java.util.Random;

public class MainGame extends Game {

	public static int SCREEN_WIDTH = 320*4;
    public static int SCREEN_HEIGHT = 240*4;

    public Random rand;


    /** GameScreen Assets **/
    public AssetManager assets;

    /** Perhaps Should be in a "gamestate" class? **/
    public Array<Body> bodiesToRemove;


    /** Actors **/
    public Player player;
    public GenericActor selectedEnemy; //we will use this to track which body the player has selected, so only one can be selected at a time

    /** shared textures **/
    public TextureAtlas atlas;
    public Skin skin;

    /**Rube scene **/
    public RubeScene scene;

    /** game stages **/
    public Stage stage;
    public Stage backgroundStage;
    public UserInterface userInterfaceStage;


    @Override
	public void create () {
        //create the skin that will be used throughout the game
        skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));
        bodiesToRemove = new Array<Body>();
        rand = new Random();//seed this for any variable thing to use later
        setScreen(new MenuScreen(this));
	}

    public void returnToMenu(){
        //destroy anything we are holding
        scene.getWorld().dispose();
        stage.dispose();
        backgroundStage.dispose();
        atlas.dispose();
        bodiesToRemove.clear();
        assets.dispose();
        
        //return to the load menu
        setScreen(new MenuScreen(this));
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

}
