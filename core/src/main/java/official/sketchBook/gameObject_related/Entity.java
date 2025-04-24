package official.sketchBook.gameObject_related;

import com.badlogic.gdx.physics.box2d.World;

public abstract class Entity extends MovableGameObject{
    public Entity(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
