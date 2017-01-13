package com.mygdx.mariobros;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import Screens.PlayScreen;

public class MarioBros extends Game {
	// virtual width and height
	public static int V_WIDTH = 400;
	public static int V_HEIGHT = 208;
	public static float PPM = 100; // pixels per meter. float not integer (for division)

	// every fixture in box2d has a filter (which has a category and a mask)
	public static final short GROUND_BIT = 1; // every fixture created has a category bit set to 1
	public static final short MARIO_BIT = 2; // powers of 2 for all short for bitwise operations
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;

	public SpriteBatch batch;
	public static AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
        manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.finishLoading();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render(); // delegate render method to playscreen

	}
	
	@Override
	public void dispose () {
        super.dispose();
		batch.dispose();
        manager.dispose();
	}
}
