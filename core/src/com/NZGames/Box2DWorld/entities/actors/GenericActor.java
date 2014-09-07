package com.NZGames.Box2DWorld.entities.actors;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.entities.monster_drops.HealthDrop;
import com.NZGames.Box2DWorld.entities.spells.GenericSpell;
import com.NZGames.Box2DWorld.handlers.AnimatedImage;
import com.NZGames.Box2DWorld.handlers.Box2DVars;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 8/13/14.
 */
public class GenericActor extends Image  {

    /** Select enemy variables**/
    public boolean selected = false;
    public Group graphicsGroup;//used to link in the arrow later
    TextureRegion downArrow;
    Image downArrowImage;

    /** How GenericEnemy relates to world**/
    protected float worldWidth;
    protected float worldHeight;
    protected float stateTime;
    protected Body body;
    protected Array<Fixture> actorFixtures;
    public boolean facingRight = false;
    protected MainGame game;

    /** Animation**/
    protected Animation rightAnimation;
    protected Animation leftAnimation;
    protected TextureRegionDrawable myDrawable;
    protected TextureRegion downFacingFrame; //used for turning animation
    protected TextureRegion damage;
    public Image currentHPImage;
    public Image hpBarImage;

    /**Extra Animation (such as spell damage)**/
    public GenericSpell currentSpell;
    protected Image extraAnimationCurrentFrame; //used for spell animations
    protected boolean haveExtraAnimation = false;
    protected float animationDurationRemaining = 0;
    protected Label label;

    /** Movement **/
    float previousPositionX;
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;
    float forwardForce = 0;
    float timeSpentTryingDirection = 0;
    boolean playDownFrame = false;

    /** Reference to self**/
    GenericActor genericActor;

    /** Character Attributes **/
    protected int hitPoints;
    protected int maxHitPoints;
    protected int magicPoints;
    protected int maxMagicPoints;
    protected int contactDamage; //damage player gets when he contacts enemy
    public float percentHitPointsRemaining;
    protected float maxHPImageWidth;
    protected int money=0;

    public GenericActor(){
        //make the actor a button for the user to select for targeting
        genericActor = this;
        this.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(downArrow ==null){
                    downArrow = new TextureRegion(game.atlas.findRegion("Arrowdowngreen"));
                }
                game.selectEnemy(genericActor);
                return true;
            }
        });

    }
    public void toggleSelected(){

        //toggle the selected enemy
        if(!selected) {

            //put on the health bar
            graphicsGroup.addActor(currentHPImage);
            graphicsGroup.addActor(hpBarImage);


            //make the bouncing arrow
            downArrowImage = new Image(downArrow);
            downArrowImage.setSize(75, 75);
            downArrowImage.setPosition(getX(), getY() + worldHeight);

            //make the arrow move up and down
            downArrowImage.addAction(forever(
                    sequence(
                            moveTo(
                                    getX(),
                                    getY()+ worldHeight +25,
                                    0.5f
                            ),
                            moveTo(
                                    getX(),
                                    getY()+ worldHeight,
                                    0.5f
                            )
                    )));


            graphicsGroup.addActor(downArrowImage);

            selected = true;
        }
        else{
            graphicsGroup.removeActor(downArrowImage);
            graphicsGroup.removeActor(currentHPImage);
            graphicsGroup.removeActor(hpBarImage);

            selected = false;
        }
    }
    public float getStateTime(){
        return stateTime;
    }
    public Body getBody(){
        return body;
    }

    public void incurDamage(int hp){
        if(hitPoints>0) {
            //make the hp label
            final Label label = new Label(String.valueOf(hp), game.skin, "default-font", Color.CYAN);
            label.setFontScale(3);
            label.setPosition(
                    getX() - 25,
                    getY() + worldHeight
            );
            //make the hp float up and then disappear
            label.addAction(
                    sequence(
                            moveTo(
                                    getX(),
                                    getY() + worldHeight + 50,
                                    2
                            ),
                            fadeOut(1),
                            new Action() {
                                @Override
                                public boolean act(float delta) {
                                    label.remove();
                                    return false;
                                }
                            }
                    )
            );
            graphicsGroup.addActor(label);

            //subtract the hp, and if player dies, then remove
            hitPoints -= hp;

            //update the percentage of hit and magic points left
            percentHitPointsRemaining =(float) hitPoints / maxHitPoints;

            //if player, update the userInterface with this new hp percent
            if(String.valueOf(body.getUserData()).compareTo("Player")==0) {
                //make the player contact invincible
                actorFixtures = body.getFixtureList();
                for(int x = 0; x< actorFixtures.size; x++){
                    Filter myFilter = actorFixtures.get(x).getFilterData();
                    short bits = myFilter.maskBits;
                    bits &= ~Box2DVars.BIT_ENEMY;
                    myFilter.maskBits=bits;
                    actorFixtures.get(x).setFilterData(
                            myFilter
                    );
                }

                game.userInterfaceStage.currentHP.setWidth(game.userInterfaceStage.maxPlayerVitalsWidth * percentHitPointsRemaining);

            }

            //or it's a monster
            else{
                float newWidth =  maxHPImageWidth * percentHitPointsRemaining;
                if(newWidth<0){
                    newWidth = 0 ;
                }

                currentHPImage.setWidth(
                        newWidth
                );
            }


            if (hitPoints <= 0) {
                destroyActor();
            }
        }
    }

    public void incurDamage(int hp, AnimatedImage damageAnimation){

        if(hitPoints>0) {

            graphicsGroup.addActor(damageAnimation);

            incurDamage(hp);

        }
    }

    public void getHitByPhysicalAttack(int hp){

        if(hitPoints>0){

            makeDamageLabel(hp);

            //subtract the hp, and if actor dies, then remove
            hitPoints -= hp;

            updateHPLabels();

            //check to see if actor died
            if (hitPoints <= 0) {
                destroyActor();
            }

            else{

                makeContactInvincible();

                handleInvincibleAnimation();

            }


        }

    }


    private void makeDamageLabel(int hp){
        //make the hp label
        final Label label = new Label(String.valueOf(hp), game.skin, "default-font", Color.CYAN);
        label.setFontScale(3);
        label.setPosition(
                getX() - 25,
                getY() + worldHeight
        );
        //make the hp float up and then disappear
        label.addAction(
                sequence(
                        moveTo(
                                getX(),
                                getY() + worldHeight + 50,
                                2
                        ),
                        fadeOut(1),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                label.remove();
                                return false;
                            }
                        }
                )
        );
        graphicsGroup.addActor(label);

    }

    private void updateHPLabels(){
        //update the percentage of hit and magic points left
        percentHitPointsRemaining =(float) hitPoints / maxHitPoints;

        //if it's the player, update the HP bar at the bottom of the screen
        if(String.valueOf(body.getUserData()).compareTo("Player")==0) {
            game.userInterfaceStage.currentHP.setWidth(game.userInterfaceStage.maxPlayerVitalsWidth * percentHitPointsRemaining);
        }

        //update the selected actor hp bar
        float newWidth =  maxHPImageWidth * percentHitPointsRemaining;
        if(newWidth<0){
            newWidth = 0 ;
        }
        currentHPImage.setWidth(
                newWidth
        );

    }

    public void makeContactInvincible(){
        //make the actor contact invincible
        actorFixtures = body.getFixtureList();
        for (int x = 0; x < actorFixtures.size; x++) {
            Filter myFilter = actorFixtures.get(x).getFilterData();
            short bits = myFilter.maskBits;
            short categoryBits = myFilter.categoryBits;
            if(String.valueOf(body.getUserData()).compareTo("Player")==0) {
                if(categoryBits!=Box2DVars.BIT_SWORD) {
                    bits &= ~Box2DVars.BIT_ENEMY;
                }
            }
            else{
                bits &= ~Box2DVars.BIT_SWORD;
            }
            myFilter.maskBits = bits;
            actorFixtures.get(x).setFilterData(
                    myFilter
            );
        }
    }
    private void removeContactInvincibility(){
        //make the player able to be hit again
        actorFixtures = body.getFixtureList();
        for (int x = 0; x < actorFixtures.size; x++) {
            Filter myFilter = actorFixtures.get(x).getFilterData();
            short bits = myFilter.maskBits;//get the mask bits
            short categoryBits=myFilter.categoryBits;
            //if it's a player, we add back the enemy bit
            if(String.valueOf(body.getUserData()).compareTo("Player")==0) {
                if(categoryBits!=Box2DVars.BIT_SWORD) {
                    bits |= Box2DVars.BIT_ENEMY;//add back in the enemy, for all except for the sword
                }
            }
            else{
                bits |= Box2DVars.BIT_SWORD;//add back in the sword
            }

            myFilter.maskBits = bits;
            actorFixtures.get(x).setFilterData(
                    myFilter
            );
        }
    }
    private void handleInvincibleAnimation(){
        //reverse the momentum for the hit if it's the player
        if(String.valueOf(body.getUserData()).compareTo("Player")==0) {
            body.setLinearVelocity(
                    (body.getLinearVelocity().x > 0) ? -4 : 4,
                    (body.getLinearVelocity().y > 0) ? -4 : 4
            );
        }

        //if it's a monster, that may throw him into the player after a sword slash. Always propel away
        else{
            body.setLinearVelocity(
                    //compare the x position of the enemy to the player
                    (game.player.getBody().getPosition().x < body.getPosition().x) ? 5 : -5,
                    body.getLinearVelocity().y
            );
        }


        //now add a flashing graphic
        graphicsGroup.addAction(
                sequence(
                        repeat(3,
                                sequence(
                                        fadeOut(0.1f),
                                        fadeIn(0.1f)
                                )

                        ),
                        new Action() {
                            @Override
                            public boolean act(float delta) {

                                removeContactInvincibility();

                                return true;
                            }
                        }
                )


        );
    }
    private void destroyActor(){

        //remove the box2d body
        game.bodiesToRemove.add(this.getBody());


        //remove the graphic
        graphicsGroup.addAction(
                sequence(
                        fadeOut(1),
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

    /**
     * since box2d will not allow the removal or creation of bodies during a step, this must be done outside.
     * this will be called when we are safely destroying the body.
     */
    public void spawnPickup(){
        //    int randomNum = rand.nextInt((max - min) + 1) + min;

        int dropNumber = game.rand.nextInt((2 - 1) + 1) + 1;//one in 2 chance of a drop
        if(dropNumber==1) {
            //spawn the monster drops
            HealthDrop healthDrop = new HealthDrop(body.getPosition(), game);
            game.stage.addActor(healthDrop.getGroup());
        }
    }

    public void recieveHealth(int health){
        this.hitPoints += health;
        if(hitPoints>maxHitPoints){
            hitPoints = maxHitPoints;
        }
        updateHPLabels();
    }
    public void recieveMagic(int magic){
        this.magicPoints += magic;
        if(magicPoints>maxMagicPoints){
            magicPoints=maxMagicPoints;
        }
    }

    public void recieveMoney(int money){
        this.money += money;
    }
}
