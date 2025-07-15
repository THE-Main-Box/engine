package official.sketchBook.util_related.util.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.values.FixtureType;

public class CollisionDataBuffer {
    /// Última direção da última iteração
    private Direction lastDirection;
    /// Última fixture com que tivemos uma interação
    private FixtureType lastCollisionWith;
    /// Último contato iterado
    private Contact lastContact;
    /// Ponto de contato da colisão
    private Vector2 objectCollisionPos = new Vector2(); // novo campo


    private boolean isReset;

    public CollisionDataBuffer() {
        this.init();
    }

    public void init() {
        this.lastDirection = Direction.STILL;
        this.isReset = false;
    }

    public void buff(Direction dir, Vector2 objectCollisionPos, FixtureType fixtureType, Contact contact) {
        this.lastDirection = dir;
        this.lastCollisionWith = fixtureType;
        this.lastContact = contact;
        this.objectCollisionPos = objectCollisionPos != null ? objectCollisionPos.cpy() : null;


        this.isReset = false;
    }

    public void reset() {
        this.lastContact = null;
        this.lastCollisionWith = null;
        this.lastDirection = null;
        this.isReset = true;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public FixtureType getLastCollisionWith() {
        return lastCollisionWith;
    }

    public Contact getLastContact() {
        return lastContact;
    }

    public boolean isReset() {
        return isReset;
    }

    public Vector2 getObjectCollisionPos() {
        return objectCollisionPos;
    }
}
