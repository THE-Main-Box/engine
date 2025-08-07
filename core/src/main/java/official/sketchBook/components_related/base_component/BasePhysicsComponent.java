package official.sketchBook.components_related.base_component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.interfaces.Physical;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public abstract class BasePhysicsComponent implements Component {

    protected Physical object;
    protected Body body;

    public BasePhysicsComponent(Physical object) {
        this.object = object;
        this.body = object.getBody();
    }

    public final void applyImpulse(Vector2 impulse) {
        if (body != null && !impulse.isZero()) {
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        }
    }

    public final void syncBodyObjectPos() {
        if (body == null) return;

        object.setX((body.getPosition().x * PPM) - (object.getWidth() / 2f));
        object.setY((body.getPosition().y * PPM) - (object.getHeight() / 2f));
    }

    public final void applyTrajectoryImpulse(float height, float distance) {
        if (body == null) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        float initialVelocityY = (float) Math.copySign(Math.sqrt(2 * gravity * Math.abs(height)), height);
        Vector2 impulse = new Vector2(distance * mass, initialVelocityY * mass);
        applyImpulse(impulse);
    }

    public final void applyDirectionalImpulse(Vector2 direction, float magnitude) {
        if (body == null) return;

        Vector2 normalized = direction.nor(); // normaliza a direção
        Vector2 impulse = normalized.scl(magnitude * body.getMass());
        applyImpulse(impulse);
    }

    public final void limitVelocity(float maxX, float maxY) {
        if (body == null) return;

        Vector2 velocity = body.getLinearVelocity();

        float maxXInMeters = maxX / PPM;
        float maxYInMeters = maxY / PPM;

        float limitedX = Math.max(-maxXInMeters, Math.min(velocity.x, maxXInMeters));
        float limitedY = Math.max(-maxYInMeters, Math.min(velocity.y, maxYInMeters));

        // Só aplica se houver alteração real
        if (velocity.x != limitedX || velocity.y != limitedY) {
            body.setLinearVelocity(limitedX, limitedY);
        }
    }

    public final void deactivate(){
        body.setActive(false);
        resetMovement();
    }

    public final void activate(){
        body.setActive(true);
    }

    public void resetMovement(){
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
    }

    public Body getBody() {
        return body;
    }
}
