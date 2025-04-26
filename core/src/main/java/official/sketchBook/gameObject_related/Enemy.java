package official.sketchBook.gameObject_related;

import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Entity {
    public Enemy(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);
    }
}
