package official.sketchBook.components_related.toUse_component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.gameObject_related.GameObject;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class MObjectPhysicsComponent extends BasePhysicsComponent {

    public MObjectPhysicsComponent(GameObject object, Body body) {
        super(object, body);
    }

    public void update(float deltaTime) {
        // Caso precise de alguma atualização por frame
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

        Vector2 currentVelocity = body.getLinearVelocity();
        float desiredX = limitAndConvertSpeedToMeters(xSpeed, maxXSpeed, currentVelocity.x);
        float desiredY = limitAndConvertSpeedToMeters(ySpeed, maxYSpeed, currentVelocity.y);

        Vector2 deltaV = new Vector2(
            desiredX != 0 ? desiredX - currentVelocity.x : 0,
            desiredY != 0 ? desiredY - currentVelocity.y : 0
        );

        applyImpulse(deltaV.scl(body.getMass()));
    }

    //converte os valores de velocidade em pixel para metros, e os limita com base em uma velocidade maxima passada
    public float limitAndConvertSpeedToMeters(float speedToApply, float maxSpeed, float currentSpeed) {
        if (speedToApply != 0) {
            return Math.max(-maxSpeed / PPM, Math.min(speedToApply / PPM, maxSpeed / PPM));
        }
        return currentSpeed; // Se speed for 0, não altera a velocidade atual
    }
}
