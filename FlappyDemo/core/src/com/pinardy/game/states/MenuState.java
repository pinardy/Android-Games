package com.pinardy.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pinardy.game.FlappyDemo;

/**
 * Created by Pin on 25-Dec-16.
 */

public class MenuState extends State{
    private Texture background;
    private Texture playBtn;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("bg.png");
        playBtn = new Texture("playBtn.png");

    }

    @Override
    public void handleInput() {
        if (Gdx.input.justTouched()){
            gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput(); // check our input if user has done anything
    }

    @Override
    public void render(SpriteBatch sb) {
        /** Imagine opening a box, putting whatever in the box, closing it.
         *  Then we use whatever is in the box
         */
//        sb.setProjectionMatrix(cam.combined); // for phone
        sb.begin();
//        sb.draw(background, 0,0); // for phone
//        sb.draw(playBtn, cam.position.x - playBtn.getWidth() / 2, cam.position.y); // for phone
        sb.draw(background, 0,0, FlappyDemo.WIDTH, FlappyDemo.HEIGHT); // takes a texture , coordinate, width & height
        sb.draw(playBtn, (FlappyDemo.WIDTH/2) - (playBtn.getWidth()/2), FlappyDemo.HEIGHT/2);
        sb.end();
    }

    @Override
    // We call dispose() when we transition states
    // this is to prevent memory leaks
    public void dispose() {
        background.dispose();
        playBtn.dispose();
        System.out.println("MenuState disposed");
    }
}
