package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.interfaces.MovementCapable;
import official.sketchBook.projectiles_related.Projectile;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public class ProjectilePhysicsComponent extends BasePhysicsComponent implements Component {

    public ProjectilePhysicsComponent(Projectile proj) {
        super(proj);
    }

    public void update(float delta) {

    }

    /**
     * Aplica um impulso baseado em um deslocamento ao longo do tempo,
     * levando em consideração a gravidade (ou não).
     */
    public void applyTimedTrajectory(Vector2 displacement, float time) {
        if (time <= 0f) return;

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

        applyBallisticImpulse(height, distance);
    }

    /// Aplica impulso simulando uma trajetória balística com base em altura e distância
    public void applyBallisticImpulse(float height, float distance) {
        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        float vy = (float) Math.copySign(Math.sqrt(2 * gravity * Math.abs(height)), height);
        Vector2 impulse = new Vector2(distance * mass, vy * mass);

        applyImpulse(impulse);
    }

    /// Reset da velocidade da body do projétil
    public void resetMovement() {
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
    }

    /// zera ou reseta a escala de gravidade do corpo
    public void setAffectedByGravity(boolean affected) {
        body.setGravityScale(affected ? 1f : 0f);
    }

}
