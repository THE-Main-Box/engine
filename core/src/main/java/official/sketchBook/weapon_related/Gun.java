package official.sketchBook.weapon_related;

import official.sketchBook.projectiles_related.Projectile;

public abstract class Gun {

    protected String name;
    protected Class<? extends Projectile> projectileType;

}
