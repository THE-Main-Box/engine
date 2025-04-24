package official.sketchBook.gameObject_related.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.MovableGameObject;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class Player extends MovableGameObject {

    private PlayerControllerComponent controllerComponent;

    public Player(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        controllerComponent = new PlayerControllerComponent(this);
        addComponent(controllerComponent);
    }

    @Override
    protected void createBody() {

        this.body = BodyCreatorHelper.createCircle(
            world,
            new Vector2(x, y),
            width,
            BodyDef.BodyType.DynamicBody,
            1f,
            0.5f,
            0.1f
        );

        this.body.setFixedRotation(true);
    }

    @Override
    public void update(float deltaTime) {
        updateComponents(deltaTime);

        //atualiza a posição do corpo através do componente de física
        physicsC.applyImpulseForSpeed(moveC.getxSpeed(), moveC.getySpeed(), moveC.getxMaxSpeed(), moveC.getySpeed());
    }

    public PlayerControllerComponent getControllerComponent() {
        return controllerComponent;
    }
}
