package official.sketchBook.engine.components_related.toUse_component.object;

import official.sketchBook.engine.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.engine.components_related.integration_interfaces.MovementCapableII;
import official.sketchBook.engine.gameObject_related.base_model.PhysicalGameObject;

import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;


public class MObjectPhysicsComponent extends BasePhysicsComponent {

    private final MovementCapableII mob;

    public MObjectPhysicsComponent(PhysicalGameObject object) {
        super(object);
        this.mob = (MovementCapableII) object;
    }

    public void update(float deltaTime) {
        limitVelocity(mob.getMoveC().getxMaxSpeed(), mob.getMoveC().getyMaxSpeed());
    }

    /**
     * Aplica um impulso para alcançar uma velocidade em específico
     * (todos os valores precisam ser em píxels já que serão convertidos em metros)
     *
     * @param xSpeed velocidade horizontal em píxels
     * @param maxXSpeed velocidade horizontal máxima permitida em píxels
     * @param ySpeed velocidade vertical em píxels
     * @param maxYSpeed velocidade vertical máxima permitida em píxels
     * */
    public void applyImpulseForSpeed(float xSpeed, float ySpeed, float maxXSpeed, float maxYSpeed) {
        if (body == null) return;

        updateVelBuffer();

        float desiredX = limitAndConvertSpeedToMeters(xSpeed, maxXSpeed, tmpVel.x);
        float desiredY = limitAndConvertSpeedToMeters(ySpeed, maxYSpeed, tmpVel.y);

        tmpVel.set(
            desiredX != 0 ? desiredX - tmpVel.x : 0,
            desiredY != 0 ? desiredY - tmpVel.y : 0
        );

        applyImpulse(tmpVel.scl(body.getMass()));
    }

    //converte os valores de velocidade em pixel para metros, e os limita com base em uma velocidade maxima passada
    public final float limitAndConvertSpeedToMeters(float speedToApply, float maxSpeed, float currentSpeed) {
        if (speedToApply != 0) {
            return Math.max(-maxSpeed / PPM, Math.min(speedToApply / PPM, maxSpeed / PPM));
        }
        return currentSpeed; // Se speed for 0, não altera a velocidade atual
    }
}
