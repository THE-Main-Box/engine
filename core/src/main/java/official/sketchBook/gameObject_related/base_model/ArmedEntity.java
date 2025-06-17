package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.util.AnchorPoint;
import official.sketchBook.weapon_related.base_model.BaseWeapon;
import official.sketchBook.weapon_related.base_model.RangeWeapon;

public abstract class ArmedEntity extends Entity {

    protected AnchorPoint weaponAnchorPoint;
    protected float xAP, yAP;

    protected BaseWeapon weapon;

    public ArmedEntity(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        weaponAnchorPoint = new AnchorPoint();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (weapon != null) {
            weapon.update(deltaTime);
        }
    }

    @Override
    protected void updateAnimationPlayer(float delta) {
        super.updateAnimationPlayer(delta);

        if (weapon != null) {
            weapon.updateAniPlayer(delta);
        }
    }

    @Override
    public void updateSpritePos() {
        super.updateSpritePos();
        weaponAnchorPoint.set(x + xAP, y + yAP);
    }

    public void useWeapon(){
        if(weapon == null) return;
        weapon.use();
    }

    public void useWeaponSecondary(){
        if(weapon == null) return;
        weapon.secondaryUse();
    }

    public AnchorPoint getWeaponAnchorPoint() {
        return weaponAnchorPoint;
    }
    // ======= AUXILIARES TIPADOS =======

    public boolean hasRangeWeapon() {
        return weapon instanceof RangeWeapon<?>;
    }

    public RangeWeapon getRangeWeapon() {
        if (weapon instanceof RangeWeapon rw) {
            return rw;
        }
        throw new IllegalStateException("Current weapon is not a RangeWeapon.");
    }

//    public boolean hasMeleeWeapon() {
//        return weapon instanceof MeleeWeapon;
//    }

//    public MeleeWeapon getMeleeWeapon() {
//        if (weapon instanceof MeleeWeapon mw) {
//            return mw;
//        }
//        throw new IllegalStateException("Current weapon is not a MeleeWeapon.");
//    }

    // opcional: generic-safe cast
    @SuppressWarnings("unchecked")
    public <T extends BaseWeapon> T getWeapon(Class<T> clazz) {
        if (clazz.isInstance(weapon)) {
            return (T) weapon;
        }
        throw new ClassCastException("Weapon is not of type " + clazz.getSimpleName());
    }

}
