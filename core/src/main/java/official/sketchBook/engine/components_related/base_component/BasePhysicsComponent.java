package official.sketchBook.engine.components_related.base_component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.integration_interfaces.PhysicalObjectII;

import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;


public abstract class BasePhysicsComponent implements Component {

    /// Objeto físico
    protected PhysicalObjectII object;
    /// Corpo do objeto físico
    protected Body body;

    /// Buffer da posição do corpo
    protected final Vector2 tempPos = new Vector2();
    /// Buffer de impulso a ser aplicado no corpo
    protected final Vector2 tmpImpulse = new Vector2();
    /// Buffer de velocidade a ser aplicado no corpo
    protected final Vector2 tmpVel = new Vector2();

    public BasePhysicsComponent(PhysicalObjectII object) {
        this.object = object;
        this.body = object.getBody();
    }

    /// Aplicamos um impulso diretamente no centro do corpo do objeto
    public final void applyImpulse(Vector2 impulse) {
        if (body != null && !impulse.isZero()) {
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        }
    }

    /// Atualizamos o buffer da posição do corpo e usamos ela para sincronizar a posição do objeto a ela
    public final void syncBodyObjectPos() {
        if (body == null) return;

        updatePosBuffer();

        object.setX((tempPos.x * PPM) - (object.getWidth() / 2f));
        object.setY((tempPos.y * PPM) - (object.getHeight() / 2f));
    }

    public final void applyTrajectoryImpulse(float height, float distance) {
        if (body == null) return;

        float gravity = Math.abs(body.getWorld().getGravity().y);
        float mass = body.getMass();

        float initialVelocityY = (float) Math.copySign(Math.sqrt(2 * gravity * Math.abs(height)), height);
        tmpImpulse.set(distance * mass, initialVelocityY * mass);
        applyImpulse(tmpImpulse);
    }

    /**
     * Aplicamos um impulso de acordo com a direção aplicada
     *
     * @param direction direção que devemos aplicar,
     *                  se for maior que 1 nos eixos x e y independentemente de ser negativo ou não,
     *                  normalizamos o vetor
     * @param magnitude força/magnitude que o corpo deve se mover naquela direção
     */
    public final void applyDirectionalImpulse(Vector2 direction, float magnitude) {
        if (body == null || direction.isZero()) return;

        //Preparamos a direção do impulso
        tmpImpulse.set(direction);

        //Normalizamos caso preciso
        if (direction.len2() > 1) {
            tmpImpulse.nor();
        }
        //Finalmente escalamos e aplicamos como impulso
        applyImpulse(tmpImpulse.scl(magnitude * body.getMass()));
    }

    /// Limitamos a velocidade do corpo usando isso daqui
    public final void limitVelocity(float maxX, float maxY) {
        if (body == null) return;

        updateVelBuffer();

        float maxXInMeters = maxX / PPM;
        float maxYInMeters = maxY / PPM;

        float limitedX = Math.max(-maxXInMeters, Math.min(tmpVel.x, maxXInMeters));
        float limitedY = Math.max(-maxYInMeters, Math.min(tmpVel.y, maxYInMeters));

        // Só aplica se houver alteração real arredondada
        if (Math.abs(tmpVel.x - limitedX) > 0.0001f || Math.abs(tmpVel.y - limitedY) > 0.0001f) {
            body.setLinearVelocity(limitedX, limitedY);
        }

    }

    protected final void updateVelBuffer() {
        if (body != null) {
            tmpVel.set(body.getLinearVelocity());
        }
    }

    protected final void updatePosBuffer() {
        if (body != null) {
            tempPos.set(body.getPosition());
        }
    }

    public final void deactivate() {
        if (body == null) return;
        body.setActive(false);
        resetMovement();
    }

    public final void activate() {
        if (body == null) return;

        body.setActive(true);
    }

    public void resetMovement() {
        if (body == null) return;

        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getTempPos() {
        return tempPos;
    }

    public Vector2 getTmpImpulse() {
        return tmpImpulse;
    }

    public Vector2 getTmpVel() {
        return tmpVel;
    }
}
