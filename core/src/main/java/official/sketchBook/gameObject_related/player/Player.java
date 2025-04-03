package official.sketchBook.gameObject_related.player;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.MovableGameObject;

import static official.sketchBook.PlayScreen.PPM;

public class Player extends MovableGameObject {

    private PlayerControllerComponent controllerComponent;

    public Player(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        body.setGravityScale(0);

        controllerComponent = new PlayerControllerComponent(this);
        addComponent(controllerComponent);
    }

    @Override
    protected void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius((width / 2) / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        body.createFixture(fixtureDef);
        shape.dispose();
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
