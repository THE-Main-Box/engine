package official.sketchBook.gameObject_related.entities;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.Enemy;

public class Dummy extends Enemy {
    public Dummy(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);
    }

    @Override
    protected void setBodyDefValues() {

    }

    @Override
    public boolean canJump() {
        return false;
    }
}
