package official.sketchBook.engine.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.engine.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.projectileRelated.model.Projectile;


public class ProjectilePhysicsComponent extends BasePhysicsComponent implements Component {

    public ProjectilePhysicsComponent(Projectile proj) {
        super(proj);
    }

    public void update(float delta) {

    }

    /**
     * Aplica um impulso baseado em um deslocamento ao longo do tempo,
     * levando em consideração a gravidade (ou não).
     *
     * @param displacement distancia a ser percorrida
     *
     * @param time tempo até chegar lá
     */
    public void applyTimedTrajectory(Vector2 displacement, float time) {
        if (time <= 0f) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float gravityScale = body.getGravityScale();

        float dx = displacement.x;
        float dy = displacement.y;

        if (gravityScale == 0f || gravity == 0f) {
            // Sem influência gravitacional: movimento linear
            tmpImpulse.set(dx / time, dy / time).scl(body.getMass());
            applyImpulse(tmpImpulse);
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
        tmpImpulse.set(distance * mass, vy * mass);

        applyImpulse(tmpImpulse);
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
