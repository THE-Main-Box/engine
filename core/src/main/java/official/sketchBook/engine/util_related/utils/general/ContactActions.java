package official.sketchBook.engine.util_related.utils.general;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;

public class ContactActions {

    /**
     * Dada a normal do WorldManifold, retorna de que lado veio o impacto *relativo* ao projétil:
     * - UP    = bateu por baixo (chão)
     * - DOWN  = bateu por cima (teto)
     * - LEFT  = bateu à direita
     * - RIGHT = bateu à esquerda
     * E as diagonais também.
     */
    public static Direction getCollisionType(Contact contact) {
        if (contact == null) return Direction.STILL;

        WorldManifold worldManifold = contact.getWorldManifold();
        if (worldManifold == null) return Direction.STILL;

        Vector2 normal = worldManifold.getNormal();
        float x = normal.x;
        float y = normal.y;

        float threshold = 0.5f;

        // Invertido: normal aponta do B para A (força aplicada sobre A)
        if (y > threshold && x < -threshold) return Direction.DOWN_LEFT;   // bateu de cima-esquerda
        if (y > threshold && x > threshold)  return Direction.DOWN_RIGHT;  // bateu de cima-direita
        if (y < -threshold && x < -threshold) return Direction.UP_LEFT;    // bateu de baixo-esquerda
        if (y < -threshold && x > threshold)  return Direction.UP_RIGHT;   // bateu de baixo-direita

        if (y > threshold && Math.abs(x) < threshold) return Direction.DOWN;  // bateu de cima
        if (y < -threshold && Math.abs(x) < threshold) return Direction.UP;   // bateu de baixo
        if (x > threshold && Math.abs(y) < threshold) return Direction.LEFT;  // bateu da direita
        if (x < -threshold && Math.abs(y) < threshold) return Direction.RIGHT; // bateu da esquerda

        return Direction.STILL;
    }


    public static void applyDefaultFrictionLogic(Contact contact) {
        if (contact == null) return; // Evita NullPointerException

        WorldManifold worldManifold = contact.getWorldManifold();
        if (worldManifold == null) return; // Precaução extra

        float normalX = worldManifold.getNormal().x;
        float normalY = worldManifold.getNormal().y;

        // Se o normal é quase para os lados (lateral forte), reduzir atrito
        if (Math.abs(normalX) > 0.8f && Math.abs(normalY) < 0.5f) {
            // Batendo mais de lado do que de cima
            contact.setFriction(0f); // Sem atrito nas paredes
        }
        // Se o normal é para cima (chão forte)
        else if (normalY > 0.8f) {
            contact.setFriction(0.8f); // Chão normal, atrito alto para não escorregar
        }
        else {
            // Casos meio inclinados (tipo quinas), colocar um atrito médio
            contact.setFriction(0.4f);
        }
    }


}
