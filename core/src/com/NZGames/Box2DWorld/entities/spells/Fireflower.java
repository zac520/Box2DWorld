package com.NZGames.Box2DWorld.entities.spells;

import com.NZGames.Box2DWorld.MainGame;
import com.NZGames.Box2DWorld.handlers.AnimatedImage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/31/14.
 */
public class Fireflower extends GenericSpell {//extends actor just so that I can grab it without making an empty superclass
    public static  float FRAME_DURATION = 0.2f;

    public Fireflower(MainGame myGame){
        game = myGame;

        //grab the spell animation
        spellAnimation = new Animation(FRAME_DURATION, game.atlas.findRegions("Fireball1"));

        spellDamage = 3;
        spellMinPercent = 0.6f;
    }

}
