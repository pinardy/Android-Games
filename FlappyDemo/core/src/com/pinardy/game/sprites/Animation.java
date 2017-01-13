package com.pinardy.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Pin on 26-Dec-16.
 */

public class Animation {
    private Array<TextureRegion> frames;
    private float maxFrameTime; // how long a frame has to stay before switching to next
    private float currentFrameTime; // time that animation has been in a current frame
    private int frameCount; // num of frames in animation
    private int frame; // current frame

    public Animation(TextureRegion region, int frameCount, float cycleTime) {
        frames = new Array<TextureRegion>();
        int frameWidth = region.getRegionWidth() / frameCount; // width of a single frame
        for (int i=0; i<frameCount; i++){
            // cut the image into smaller pieces
            frames.add(new TextureRegion(region, i * frameWidth, 0, frameWidth, region.getRegionHeight()));
        }
        this.frameCount = frameCount;
        maxFrameTime = cycleTime / frameCount;
        frame = 0;
    }

    public void update(float dt){
        currentFrameTime += dt; // how long the current frame has been here
        if (currentFrameTime > maxFrameTime){
            frame++;
            currentFrameTime = 0;
        }
        if (frame >= frameCount){
            frame = 0;
        }
    }

    public TextureRegion getFrame(){
        return frames.get(frame);
    }
}
