package com.pinardy.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pinardy.game.FlappyDemo;
import com.pinardy.game.sprites.Bird;
import com.pinardy.game.sprites.Tube;

/**
 * Created by Pin on 25-Dec-16.
 */

public class PlayState extends State {
    public static final int TUBE_SPACING = 125; // spacing between tubes (not incl tubes)
    public static final int TUBE_COUNT = 4; // total num of tubes our game will have at any time
    public static final int GROUND_Y_OFFSET = -30; // how high the ground is

    private Bird bird;
    private Texture bg;
    private Texture ground;
    private Vector2 groundPos1, groundPos2;

    private Array<Tube> tubes;


    public PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50, 100);
        cam.setToOrtho(false, FlappyDemo.WIDTH / 2, FlappyDemo.HEIGHT / 2);
//        bg = new Texture("bg.png");
        bg = new Texture("bg_huangshan.png");
        ground = new Texture("ground.png");
        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET); // x starts from left side of camera
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET); // need to offset by width of ground texture

        tubes = new Array<Tube>();

        for (int i=1; i <= TUBE_COUNT; i++){
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
        }

    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()){
            bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        bird.update(dt);
        cam.position.x = bird.getPosition().x + 80; // +80 to offset camera a bit in front of bird

        for (Tube tube: tubes){
            if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
            }

            // check if each tube touches a player
            if (tube.collides(bird.getBounds())){
                // restart new game
                gsm.set(new MenuState(gsm));
                break;
            }
        }

        // if bird touches ground, restart game
        if (bird.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET){
            gsm.set(new MenuState(gsm));
        }

        cam.update(); // tells libgdx that cam has been repositioned
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);

        // we want to draw the bg at wherever our camera is at
        for (Tube tube : tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();

        for (Tube tube : tubes){
            tube.dispose();
        }
        System.out.println("Play state disposed");
    }

    // check if camera is passed over where pos of ground actually is
    private void updateGround(){
        if((cam.position.x - (cam.viewportWidth / 2)) > groundPos1.x + ground.getWidth()){
            groundPos1.add(ground.getWidth() * 2, 0);
         }
        if((cam.position.x - (cam.viewportWidth / 2)) > groundPos2.x + ground.getWidth()){
            groundPos2.add(ground.getWidth() * 2, 0);
        }
//        cam.update();
    }
}
