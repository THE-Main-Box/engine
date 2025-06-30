package official.sketchBook.weapon_related.base_model.interfaces;

import official.sketchBook.weapon_related.util.weapon.RechargeManager;
import official.sketchBook.weapon_related.util.weapon.ShootStateManager;
import official.sketchBook.weapon_related.util.weapon.status.RangeWeaponStatus;

public interface IRangeCapable {
    RangeWeaponStatus getWeaponStatus();
    RechargeManager getRechargeManager();
    ShootStateManager getShootStateManager();
    void recharge();
    boolean canShoot();
}
