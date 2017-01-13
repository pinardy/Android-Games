package Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBros;

import java.util.ArrayList;

import Screens.PlayScreen;

/**
 * Created by Pin on 27-Dec-16.
 */

public class Mario extends Sprite {
    // States of Mario
    public enum State {FALLING, JUMPING, STANDING, RUNNING};
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation marioRun;
    private Animation marioJump;
    private float stateTimer; // the amount of time we are in any given state
    private boolean runningRight;

    public Mario (PlayScreen screen){
        // call to sprite class, take in TextureRegion
        super(screen.getAtlas().findRegion("little_mario"));

        // init default values
        this.world = screen.getWorld();

        // states
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        // running
        for (int i=1; i<4; i++){
            frames.add(new TextureRegion(getTexture(), i * 16, 12, 16, 16 ));
        }
        marioRun = new Animation(0.1f, frames); // duration of each frame is 0.1f
        frames.clear();

        // running
        for (int i=1; i<6; i++){
            frames.add(new TextureRegion(getTexture(), i * 16, 12, 16, 16 ));
        }
        marioJump = new Animation(0.1f, frames); // duration of each frame is 0.1f


        defineMario();
//        marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16 ); // this is a fireball
        marioStand = new TextureRegion(getTexture(), 1, 11, 16, 16);
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case JUMPING:
                region = (TextureRegion) marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = (TextureRegion) marioRun.getKeyFrame(stateTimer, true); // true because looping
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        // below 2 cases are for mario standing still
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING) {
            return State.JUMPING;
        }
        else if(b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        }
        else if(b2body.getLinearVelocity().x != 0) { // not moving to L or R (or is moving to L or R)
            return State.RUNNING;
        }
        else{
            return State.STANDING;
        }
    }

    private void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        // what mario can collide with
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                                MarioBros.COIN_BIT |
                                MarioBros.BRICK_BIT |
                                MarioBros.ENEMY_BIT |
                                MarioBros.OBJECT_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape(); //edgeshape is a line between 2 diff points
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true; // no longer collides with an event in the world

        b2body.createFixture(fdef).setUserData("head");
    }
}
