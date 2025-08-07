package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.toUse_component.entity.WeaponWieldComponent;

public abstract class DamageAbleEntity extends Entity{

    protected WeaponWieldComponent weaponWC;

    public DamageAbleEntity(float x, float y, float width, float height, boolean xAxisInverted,boolean yAxisInverted, World world) {
        super(x, y, width, height, xAxisInverted, yAxisInverted, world);

        weaponWC = new WeaponWieldComponent(this);
        addComponent(weaponWC);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

    }

    public void rechargeWeapon() {
        if(!weaponWC.isWeaponRanged()) return;
        weaponWC.getRangeWeapon().recharge();
    }

    @Override
    public void dispose() {
        super.dispose();
        weaponWC.dispose();
    }

    @Override
    public void onObjectBodySync() {
        super.onObjectBodySync();
        weaponWC.syncWeaponPosToWielder();

    }

    public WeaponWieldComponent getWeaponWC() {
        return weaponWC;
    }
}
