package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.projectiles_related.Projectile;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public class ProjectilePhysicsComponent implements Component {
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

        object.setX((body.getPosition().x * PPM) - object.getRadius());
        object.setY((body.getPosition().y * PPM) - object.getRadius());
    }

    public void applyTimedTrajectory(Vector2 displacement, float time) {
        if (body == null || time <= 0f) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float gravityScale = body.getGravityScale();

        float dx = displacement.x;
        float dy = displacement.y;

        if (gravityScale == 0f || gravity == 0f) {
            // Sem influência gravitacional: movimento linear
            Vector2 velocity = new Vector2(dx / time, dy / time);
            Vector2 impulse = velocity.scl(body.getMass());
            applyImpulse(impulse);
            return;
        }

        // Com gravidade: calcular arco
        float vx = dx / time;
        float vy = (dy / time) + (0.5f * gravity * gravityScale * time);

        float height = (vy * vy) / (2 * gravity * gravityScale); // altura do arco
        float distance = vx * time;                              // distância total

        applyTrajectoryImpulse(height, distance);
    }


    public void applyTrajectoryImpulse(float height, float distance) {
        if (body == null) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        float initialVelocityY = (float) Math.copySign(Math.sqrt(2 * gravity * Math.abs(height)), height);
        Vector2 impulse = new Vector2(distance * mass, initialVelocityY * mass);
        applyImpulse(impulse);
    }

    /// Reset da velocidade da body do projétil
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
