package com.pinardy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pinardy.game.states.GameStateManager;
import com.pinardy.game.states.MenuState;

public class FlappyDemo extends ApplicationAdapter {
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;

	public static final String TITLE = "Flappy Jia Hui";
    private GameStateManager gsm;
    private SpriteBatch batch; // SpriteBatch is a very heavy file. Only use one in all states
	private Music music;

	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        gsm = new GameStateManager();
		img = new Texture("badlogic.jpg");

		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true); // music continuously plays
		music.setVolume(0.1f); // 10% volume
		music.play();

        Gdx.gl.glClearColor(1, 0, 0, 1);
        gsm.push(new MenuState(gsm));
    }

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		music.dispose();
	}
}
