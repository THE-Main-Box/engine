package official.sketchBook.components_related.toUse_component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.gameObject_related.GameObject;

import static official.sketchBook.PlayScreen.PPM;

public class PhysicsComponent extends Component {

    protected GameObject object;
    protected Body body;

    public PhysicsComponent(GameObject object, Body body) {
        this.object = object;
        this.body = body;
    }

    public void update(float deltaTime) {
        // Caso precise de alguma atualização por frame
    }

    //aplica um impulso para alcançar uma velocidade em específico
    public void applyImpulseForSpeed(float xSpeed, float ySpeed, float maxXSpeed, float maxYSpeed) {
        if (body == null) return;

        // Obter a velocidade atual do corpo (em m/s)
        Vector2 currentVelocity = body.getLinearVelocity();

        // Calcular o delta da velocidade e aplicar o impulso
        applyImpulseToBody(
            currentVelocity,
            limitAndConvertSpeedToMeters(xSpeed, maxXSpeed, currentVelocity.x),
            limitAndConvertSpeedToMeters(ySpeed, maxYSpeed, currentVelocity.y)
        );
    }

    //converte os valores de velocidade em pixel para metros, e os limita com base em uma velocidade maxima passada
    public float limitAndConvertSpeedToMeters(float speedToApply, float maxSpeed, float currentSpeed) {
        if (speedToApply != 0) {
            return Math.max(-maxSpeed / PPM, Math.min(speedToApply / PPM, maxSpeed / PPM));
        }
        return currentSpeed; // Se speed for 0, não altera a velocidade atual
    }

    //aplica um impulso a um corpo
    public void applyImpulseToBody(Vector2 currentVelocity, float desiredXVelocity, float desiredYVelocity) {
        if (body == null) return;

        Vector2 deltaV = new Vector2(
            desiredXVelocity != 0 ? desiredXVelocity - currentVelocity.x : 0,
            desiredYVelocity != 0 ? desiredYVelocity - currentVelocity.y : 0
        );

        Vector2 impulse = deltaV.scl(body.getMass());

        if (!impulse.isZero()) {
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        }
    }

    public void applyTrajectoryImpulse(float height, float distance) {
        if (body == null) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        // Evita raiz quadrada negativa
        float initialVelocityY = height >= 0 ? (float) Math.sqrt(2 * gravity * height) : -(float) Math.sqrt(-2 * gravity * height);

        // Aplica o impulso proporcional à distância
        Vector2 impulse = new Vector2(distance * mass, initialVelocityY * mass);
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }


    public void syncBodyObjectPos() {
        if (body == null) return;

        object.setX((body.getPosition().x * PPM) - (object.getWidth() / 2f));
        object.setY((body.getPosition().y * PPM) - (object.getHeight() / 2f));
    }

    public GameObject getObject() {
        return object;
    }

    public Body getBody() {
        return body;
    }
}
