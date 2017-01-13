package Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import Screens.PlayScreen;

/**
 * Created by Pin on 02-Jan-17.
 */

public abstract class Enemy extends Sprite{
    protected World world;
    protected PlayScreen screen;
    public Body b2body;

    public Enemy(PlayScreen screen, float x, float y){ // x & y are positions
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
    }

    protected abstract void defineEnemy(); // to be implemented in Boomba class
}
