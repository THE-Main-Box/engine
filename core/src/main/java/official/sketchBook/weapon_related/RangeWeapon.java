package official.sketchBook.weapon_related;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;

public abstract class RangeWeapon {

    protected String name;
    protected String description;

    protected Class<? extends Projectile> projectileType;
    protected Emitter projectileEmitter;
    protected Entity owner;

    protected SpriteSheetDataHandler spriteDataHandler;

    protected void configProjectileTypeOnEmitter(Class<? extends Projectile> type){
        this.projectileType = type;
        this.projectileEmitter.configure(type);
    }

    // Ponto de disparo com base na direção
    public final Vector2 getProjectileSpawnPosition(Direction direction) {
        Vector2 base = new Vector2(
            (owner.getX() + owner.getWidth() / 2f),
            (owner.getY() + owner.getHeight() / 2f)
        );

        return base.add(getOffsetForDirection(direction));
    }

    // obtém offset da direção
    protected Vector2 getOffsetForDirection(Direction direction) {
        if (direction.isRight()) return getRightOffset();
        if (direction.isLeft()) return getLeftOffset();
        if (direction.isDown()) return getDownOffset();
        if (direction.isUp()) return getUpOffset();

        return new Vector2(); // STILL ou default
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Métodos customizáveis por subclasses
    protected Vector2 getRightOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getLeftOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getDownOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getUpOffset() {
        return new Vector2(0, 0);
    }

    public void dispose() {
        if (spriteDataHandler != null) {
            spriteDataHandler.dispose();
        }
    }
}
