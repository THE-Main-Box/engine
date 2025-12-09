package official.sketchBook.engine.util_related.utils.general;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import official.sketchBook.engine.components_related.integration_interfaces.move.MovementCapableII;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;

public class ContactActions {

    // thresholds — ajustáveis (declare constantes se quiser)
    private static final float MIN_VEL_TO_ZERO = 0.05f;       // abaixo disso consideramos "parado"

    private static final Vector2 tmpVel = new Vector2();

    private static final Vector2 cachedNormal = new Vector2();
    private static Direction cachedDirection = Direction.STILL;
    private static final float EPSILON = 0.001f;

    /// Permite uma validação prévia, antes de executar uma função complexa
    public static Direction getCachedCollisionDirection(Contact contact) {
        if (contact == null) return Direction.STILL;

        WorldManifold manifold = contact.getWorldManifold();
        if (manifold == null) return Direction.STILL;

        Vector2 normal = manifold.getNormal();

        // Só atualiza se o normal mudou
        if (Math.abs(normal.x - cachedNormal.x) > EPSILON || Math.abs(normal.y - cachedNormal.y) > EPSILON) {
            cachedNormal.set(normal);
            cachedDirection = ContactActions.getCollisionDirection(contact);
        }

        return cachedDirection;
    }

    /**
     * Dada a normal do WorldManifold, retorna de que lado veio o impacto *relativo* ao projétil:
     * - UP    = bateu por baixo (chão)
     * - DOWN  = bateu por cima (teto)
     * - LEFT  = bateu à direita
     * - RIGHT = bateu à esquerda
     * E as diagonais também.
     */
    private static Direction getCollisionDirection(Contact contact) {
        if (contact == null) return Direction.STILL;

        WorldManifold worldManifold = contact.getWorldManifold();
        if (worldManifold == null) return Direction.STILL;

        Vector2 normal = worldManifold.getNormal();
        float x = normal.x;
        float y = normal.y;

        float threshold = 0.5f;

        if (y > threshold && x < -threshold) return Direction.DOWN_LEFT;
        if (y > threshold && x > threshold)  return Direction.DOWN_RIGHT;
        if (y < -threshold && x < -threshold) return Direction.UP_LEFT;
        if (y < -threshold && x > threshold)  return Direction.UP_RIGHT;

        if (y > threshold && Math.abs(x) < threshold) return Direction.DOWN;
        if (y < -threshold && Math.abs(x) < threshold) return Direction.UP;
        if (x > threshold && Math.abs(y) < threshold) return Direction.LEFT;
        if (x < -threshold && Math.abs(y) < threshold) return Direction.RIGHT;

        return Direction.STILL;
    }






    public static void handleBlockedMovement(Direction dir, MovementCapableII mob) {
        if (dir == null || mob == null || dir == Direction.STILL) return;

        tmpVel.set(mob.getBody().getLinearVelocity());

        // --- EIXO HORIZONTAL ---
        if (dir.isLeft()) { // colisão à esquerda
            if (tmpVel.x < -MIN_VEL_TO_ZERO) { // somente se indo contra a parede
                mob.getMoveC().setxSpeed(0);
                mob.getBody().setLinearVelocity(0f, tmpVel.y);
            }
        } else if (dir.isRight()) { // colisão à direita
            if (tmpVel.x > MIN_VEL_TO_ZERO) { // somente se indo contra a parede
                mob.getMoveC().setxSpeed(0);
                mob.getBody().setLinearVelocity(0f, tmpVel.y);
            }
        }

        // --- EIXO VERTICAL ---
        if (dir.isUp()) { // colisão com teto
            if (tmpVel.y > MIN_VEL_TO_ZERO) { // indo contra o teto
                mob.getMoveC().setySpeed(0);
                mob.getBody().setLinearVelocity(tmpVel.x, 0f);
            }
        } else if (dir.isDown()) { // colisão com chão
            if (tmpVel.y < -MIN_VEL_TO_ZERO) { // indo contra o chão
                mob.getMoveC().setySpeed(0);
                mob.getBody().setLinearVelocity(tmpVel.x, 0f);
            }
        }
    }


//    public static void applyDefaultFrictionLogic(Contact contact) {
//        if (contact == null) return; // Evita NullPointerException
//
//        WorldManifold worldManifold = contact.getWorldManifold();
//        if (worldManifold == null) return; // Precaução extra
//
//        float normalX = worldManifold.getNormal().x;
//        float normalY = worldManifold.getNormal().y;
//
//        // Se o normal é quase para os lados (lateral forte), reduzir atrito
//        if (Math.abs(normalX) > 0.8f && Math.abs(normalY) < 0.5f) {
//            // Batendo mais de lado do que de cima
//            contact.setFriction(0f); // Sem atrito nas paredes
//        }
//        // Se o normal é para cima (chão forte)
//        else if (normalY > 0.8f) {
//            contact.setFriction(0.8f); // Chão normal, atrito alto para não escorregar
//        }
//        else {
//            // Casos meio inclinados (tipo quinas), colocar um atrito médio
//            contact.setFriction(0.4f);
//        }
//    }


}
