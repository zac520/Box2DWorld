package com.NZGames.Box2DWorld.entities.spells;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.AnimatedImage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Action;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/31/14.
 */
public class GenericSpell {

    public MainGame game;
    public AnimatedImage spellAnimatedImage;
    public Animation spellAnimation; //I think we will put these in their own class later.
    protected float spellMinPercent;
    protected int spellDamage;

    public GenericSpell(){

    }
    public int getSpellDamage(){
        int minDamage = (int) Math.ceil(spellDamage * spellMinPercent);

        int randSpellDamage = game.rand.nextInt((spellDamage - minDamage) + 1) + minDamage;

        int critical = game.rand.nextInt((10 - 1) + 1) + 1;//one in ten chance of critical

        if(critical == 10) {
            return spellDamage*2;
        }
        else{
            return randSpellDamage;
        }
    }

    public AnimatedImage getSpellAnimation(){

        //turn it into something we can add to the stage directly
        final AnimatedImage mySpellAnimation = new AnimatedImage(spellAnimation);
        mySpellAnimation.setSize(50,50);
        mySpellAnimation.setCenterPosition(
                game.selectedEnemy.getCenterX(),
                game.selectedEnemy.getCenterY()
        );
        mySpellAnimation.addAction(
                sequence(
                        delay(1),
                        fadeOut(1),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                mySpellAnimation.remove();
                                return false;
                            }
                        }));
        return mySpellAnimation;
    }
}
