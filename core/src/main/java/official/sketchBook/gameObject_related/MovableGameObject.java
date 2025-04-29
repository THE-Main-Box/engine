package official.sketchBook.gameObject_related;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.toUse_component.object.MovementComponent;
import official.sketchBook.components_related.toUse_component.object.MObjectPhysicsComponent;

public abstract class MovableGameObject extends GameObject {

    protected MovementComponent moveC;
    protected MObjectPhysicsComponent physicsC;

    public MovableGameObject(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        moveC = new MovementComponent(this.body.getMass());
        addComponent(moveC);

        physicsC = new MObjectPhysicsComponent(this);
        addComponent(physicsC);
    }

    @Override
    public void update(float deltaTime) {
        applySpeedOnBody();
    }

    //atualiza a posição do corpo através do componente de física
    protected void applySpeedOnBody() {
        if (physicsC == null || moveC == null) return;
        physicsC.applyImpulseForSpeed(moveC.getxSpeed(), moveC.getySpeed(), moveC.getxMaxSpeed(), moveC.getySpeed());
    }

    public MovementComponent getMoveC() {
        return moveC;
    }

    public MObjectPhysicsComponent getPhysicsC() {
        return physicsC;
    }
}
