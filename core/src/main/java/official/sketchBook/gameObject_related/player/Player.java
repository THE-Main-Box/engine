package official.sketchBook.gameObject_related.player;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.components_related.toUse_component.MovementComponent;
import official.sketchBook.components_related.toUse_component.PhysicsComponent;
import official.sketchBook.gameObject_related.GameObject;

import static official.sketchBook.PlayScreen.PPM;

public class Player extends GameObject {

    public Player(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

//        body.setGravityScale(0);

        addComponent(new MovementComponent(this.body.getMass()));
        addComponent(new PhysicsComponent(this, this.body));
        addComponent(new PlayerControllerComponent(this));
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

    }

    @Override
    public void render() {

    }
}
