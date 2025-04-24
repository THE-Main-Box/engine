package official.sketchBook.gameObject_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.toUse_component.MovementComponent;
import official.sketchBook.components_related.toUse_component.PhysicsComponent;

public abstract class MovableGameObject extends GameObject {

    protected MovementComponent moveC;
    protected PhysicsComponent physicsC;

    public MovableGameObject(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        moveC = new MovementComponent(this.body.getMass());
        addComponent(moveC);

        physicsC = new PhysicsComponent(this, this.body);
        addComponent(physicsC);
    }

    @Override
    protected void createBody() {

    }

    @Override
    public void update(float deltaTime) {
        applySpeedOnBody();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    //atualiza a posição do corpo através do componente de física
    protected void applySpeedOnBody() {
        if (physicsC == null || moveC == null) return;
        physicsC.applyImpulseForSpeed(moveC.getxSpeed(), moveC.getySpeed(), moveC.getxMaxSpeed(), moveC.getySpeed());
    }

    public MovementComponent getMoveC() {
        return moveC;
    }

    public PhysicsComponent getPhysicsC() {
        return physicsC;
    }
}
