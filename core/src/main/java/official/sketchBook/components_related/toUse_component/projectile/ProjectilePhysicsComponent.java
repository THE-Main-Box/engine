package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.projectiles_related.Projectile;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class ProjectilePhysicsComponent extends Component {
    protected Projectile object;
    protected Body body;
    private boolean affectedByGravity = false;

    public ProjectilePhysicsComponent(Projectile object) {
        this.object = object;
        this.body = object.getBody();
    }

    public void update(float delta) {

    }

    public void applyImpulse(Vector2 impulse) {
        if (body != null && !impulse.isZero()) {
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        }
    }

    public void syncBodyObjectPos() {
        if (body == null) return;

        object.setX((body.getPosition().x * PPM) - (object.getRadius() * 2) / 2f);
        object.setY((body.getPosition().y * PPM) - (object.getRadius() * 2) / 2f);
    }

    public void applyTrajectoryImpulse(float height, float distance) {
        if (body == null) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        float initialVelocityY = (float) Math.copySign(Math.sqrt(2 * gravity * Math.abs(height)), height);
        Vector2 impulse = new Vector2(distance * mass, initialVelocityY * mass);
        applyImpulse(impulse);
    }

    public void resetMovement() {
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setActive(false); // Desativa o corpo no mundo físico
    }

    public void setAffectedByGravity(boolean affected) {
        if (body != null) {
            body.setGravityScale(affected ? 1f : 0f);
        }
        this.affectedByGravity = affected;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public Projectile getObject() {
        return object;
    }

    public Body getBody() {
        return body;
    }
}
