package official.sketchBook.weapon_related;

import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;

public abstract class RangeWeapon {

    protected String name;
    protected Class<? extends Projectile> projectileType;
    protected Emitter projectileEmitter;
    protected Entity owner;

}
