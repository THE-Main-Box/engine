package official.sketchBook.game.projectiles_related.projectiles;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageDealerII;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageDealerOwnerII;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageReceiverII;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.RawDamageData;

public class ShotgunProjectile extends Projectile implements DamageDealerII {
    private static final RawDamageData dmgData;

    static {
        dmgData = new RawDamageData(
            1,
            1,
            0,
            1,
            1,
            true
        );
    }

    private boolean damageAble;

    public ShotgunProjectile(World world) {
        super(world);

        this.setLifeTime(5);

        this.initBodyBehavior(
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        );

    }

    @Override
    public void onWallStuck() {
        this.damageAble = false;
    }

    @Override
    public void init(RangeWeaponWielderII owner) {
        super.init(owner);
        damageAble = true;
    }

    @Override
    protected void setBodyDefValues() {
        this.radius = 3.4f;
        this.defFric = 0f;
        this.defRest = 1f;
        this.defDens = 0.1f;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public DamageDealerOwnerII getOwner() {
        return owner;
    }

    @Override
    public void onElimination(DamageReceiverII receiver) {

    }

    @Override
    public void onDamage(DamageReceiverII receiver) {

    }

    @Override
    public RawDamageData getDamageData() {
        return dmgData;
    }

    @Override
    public boolean isDamageAble() {
        return damageAble;
    }
}
