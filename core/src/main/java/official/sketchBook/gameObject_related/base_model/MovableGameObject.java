package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.toUse_component.object.MObjectPhysicsComponent;
import official.sketchBook.components_related.toUse_component.object.MovementComponent;
import official.sketchBook.util_related.info.values.constants.SpeedRelatedConstants;

public abstract class MovableGameObject extends GameObject {

    protected MovementComponent moveC;
    protected MObjectPhysicsComponent physicsC;

    public MovableGameObject(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        moveC = new MovementComponent(this.body.getMass());
        addComponent(moveC);

        physicsC = new MObjectPhysicsComponent(this);
        addComponent(physicsC);

        setDefaultMovementValues();
    }

    private void setDefaultMovementValues() {
        this.moveC.setyMaxSpeed(SpeedRelatedConstants.Mobs.VERTICAL_MAX);
        this.moveC.setxMaxSpeed(SpeedRelatedConstants.Mobs.HORIZONTAL_MAX);
    }

    @Override
    public void update(float deltaTime) {
        applySpeedOnBody();
    }


    public void updateSpritePos() {
        for (SpriteSheetDataHandler dataHandler : spriteSheetDatahandlerList) {
            dataHandler.updatePosition(this.getX(), this.getY());
        }
    }

    //atualiza a posição do corpo através do componente de física
    protected void applySpeedOnBody() {
        if (physicsC == null || moveC == null) return;
        physicsC.applyImpulseForSpeed(moveC.getxSpeed(), moveC.getySpeed(), moveC.getxMaxSpeed(), moveC.getyMaxSpeed());
    }

    public MovementComponent getMoveC() {
        return moveC;
    }

    public MObjectPhysicsComponent getPhysicsC() {
        return physicsC;
    }
}
