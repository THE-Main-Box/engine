package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.components_related.toUse_component.object.WeaponWieldComponent;
import official.sketchBook.util_related.enumerators.types.FactionTypes;

public abstract class RangeWeaponWieldingEntity extends Entity implements RangeWeaponWielderII {

    protected FactionTypes faction;
    protected WeaponWieldComponent weaponWC;

    public RangeWeaponWieldingEntity(float x, float y, float width, float height, boolean xAxisNormal, boolean yAxisNormal, World world) {
        super(x, y, width, height, xAxisNormal, yAxisNormal, world);

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

    public FactionTypes getFaction() {
        return faction;
    }

}
