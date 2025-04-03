package official.sketchBook.gameObject_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.toUse_component.MovementComponent;

public class MovableGameObject extends GameObject {

    protected MovementComponent moveC;

    public MovableGameObject(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        moveC = new MovementComponent(this.body.getMass());
        addComponent(moveC);

    }

    @Override
    protected void createBody() {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public MovementComponent getMoveC() {
        return moveC;
    }
}
