package official.sketchBook.components_related.base_component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.gameObject_related.GameObject;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public abstract class BasePhysicsComponent extends Component{

    protected GameObject object;
    protected Body body;

    public BasePhysicsComponent(GameObject object, Body body) {
        this.object = object;
        this.body = body;
    }

    public void applyImpulse(Vector2 impulse) {
        if (body != null && !impulse.isZero()) {
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        }
    }

    public void syncBodyObjectPos() {
        if (body == null) return;

        object.setX((body.getPosition().x * PPM) - (object.getWidth() / 2f));
        object.setY((body.getPosition().y * PPM) - (object.getHeight() / 2f));
    }

    public void applyTrajectoryImpulse(float height, float distance) {
        if (body == null) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        float initialVelocityY = (float) Math.copySign(Math.sqrt(2 * gravity * Math.abs(height)), height);
        Vector2 impulse = new Vector2(distance * mass, initialVelocityY * mass);
        applyImpulse(impulse);
    }

    public void resetMovement(){
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setActive(false); // Desativa o corpo no mundo físico
    }

    public Body getBody() {
        return body;
    }
}
