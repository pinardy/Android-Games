package com.pinardy.game.states;

/**
 * Created by Pin on 25-Dec-16.
 */

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/** Abstract because we don't want anything to instantiate state, but rather extend state
 *
 * Each state needs a camera to locate a position in the world
 */
public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 mouse; // xyz coordinate system
    protected GameStateManager gsm; // manage our states (example: put a state on top of a state)

    protected State(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    protected abstract void handleInput();

    // dt is the time difference between the first frame rendered and the next frame rendered
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb); // container for everything we need to render on screen
    public abstract void dispose();
}

