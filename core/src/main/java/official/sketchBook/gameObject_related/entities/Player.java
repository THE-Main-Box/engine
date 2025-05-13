package official.sketchBook.gameObject_related.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.toUse_component.entity.PlayerControllerComponent;
import official.sketchBook.components_related.toUse_component.object.JumpComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.util.values.FixtureType;

public class Player extends Entity {

    private PlayerControllerComponent controllerComponent;
    private JumpComponent jComponent;

    public Player(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        setOwnerRoom(ownerRoom);

        controllerComponent = new PlayerControllerComponent(this);
        addComponent(controllerComponent);

        jComponent = new JumpComponent(this, 35, 100, 0.1f, true);
        addComponent(jComponent);
    }

    @Override
    protected void setBodyDefValues() {
        this.defDens = 0.1f;
        this.defFric = 1f;
        this.defRest = 0;
    }

    @Override
    protected void createBody() {

        this.body = BodyCreatorHelper.createCapsule(
            world,
            new Vector2(x, y),
            width,
            height,
            BodyDef.BodyType.DynamicBody,
            defDens,
            defFric,
            defRest
        );

        this.body.setUserData(new FixtureType(FixtType.ENTITY, this));
        this.body.setFixedRotation(true);
        this.body.setBullet(true);
    }

    @Override
    public void update(float deltaTime) {
        this.updateComponents(deltaTime);
        super.update(deltaTime);

    }

    @Override
    protected void applySpeedOnBody() {
        if (physicsC == null || moveC == null) return;
        if (jComponent.isEntityLanded() && !moveC.isAcceleratingX()) {

            float reducedSpeedX = moveC.getxSpeed() * 0.7f; // exemplo de redução
            physicsC.applyImpulseForSpeed(
                reducedSpeedX,
                moveC.getySpeed(),
                moveC.getxMaxSpeed(),
                moveC.getyMaxSpeed()
            );

        } else {

            physicsC.applyImpulseForSpeed(
                moveC.getxSpeed(),
                moveC.getySpeed(),
                moveC.getxMaxSpeed(),
                moveC.getyMaxSpeed()
            );

        }

    }

    @Override
    public void render(SpriteBatch batch) {

    }

    //verifica se dá para pular
    public boolean canJump() {
        return jComponent.getCoyoteTimer().isRunning() || onGround;
    }

    public PlayerControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public JumpComponent getjComponent() {
        return jComponent;
    }
}
