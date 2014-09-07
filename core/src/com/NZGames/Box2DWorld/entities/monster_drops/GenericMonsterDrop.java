package com.NZGames.Box2DWorld.entities.monster_drops;

/**
 * Created by zac520 on 9/6/14.
 */

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.spells.GenericSpell;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


/**
 * Created by zac520 on 8/13/14.
 */
public class GenericMonsterDrop extends Image {


    /** Select enemy variables**/
    public boolean selected = false;
    public Group graphicsGroup;
    TextureRegion downArrow;
    Image downArrowImage;


    /**
     * How GenericEnemy relates to world*
     */
    protected float worldWidth;
    protected float worldHeight;
    protected float stateTime;
    protected Body body;
    protected Array<Fixture> actorFixtures;
    public boolean facingRight = false;
    protected MainGame game;

    /**
     * Animation*
     */
    protected Animation animation;
    protected Animation leftAnimation;
    protected TextureRegionDrawable myDrawable;
    protected TextureRegion downFacingFrame; //used for turning animation
    protected TextureRegion damage;
    public Image currentHPImage;
    public Image hpBarImage;

    /**
     * Extra Animation (such as spell damage)*
     */
    public GenericSpell currentSpell;
    protected Image extraAnimationCurrentFrame; //used for spell animations
    protected boolean haveExtraAnimation = false;
    protected float animationDurationRemaining = 0;
    protected Label label;

    /**
     * Movement *
     */
    float previousPositionX;
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;
    float forwardForce = 0;
    float timeSpentTryingDirection = 0;
    boolean playDownFrame = false;
    protected final int RADIUS = 13;
    /**
     * Reference to self*
     */
    GenericMonsterDrop genericMonsterDrop;

    /**
     * Attributes *
     */
    protected int healthRestorePoints=0;
    protected int magicRestorePoints=0;
    protected int money=0;


    public GenericMonsterDrop() {
        //make the actor a button for the user to select for targeting
        genericMonsterDrop = this;
    }

    public float getStateTime() {
        return stateTime;
    }

    public Body getBody() {
        return body;
    }

    public int getHealthRestorePoints(){
        return healthRestorePoints;
    }
    public int getMagicRestorePoints (){
        return magicRestorePoints;
    }
    public int getMoney(){
        return money;
    }
    public void destroyMonsterDrop(){

        //make sure it isn't in there already before we add it (double adding crashes app)
        for(int x = 0; x< game.bodiesToRemove.size; x++){
            if(game.bodiesToRemove.get(x) == this.body){
                game.bodiesToRemove.removeIndex(x);
            }
        }
        game.bodiesToRemove.add(this.body);


        //remove the graphic
        graphicsGroup.addAction(
                sequence(
                        fadeOut(0.2f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {

                                graphicsGroup.remove();
                                return false;
                            }
                        }
                )
        );
    }
}