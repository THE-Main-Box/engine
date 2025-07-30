package official.sketchBook.weapon_related.base_model.interfaces;

import official.sketchBook.weapon_related.util.RechargeManager;
import official.sketchBook.weapon_related.util.ShootStateManager;
import official.sketchBook.weapon_related.util.status.RangeWeaponStatus;

public interface IRangeCapable {
    RangeWeaponStatus getWeaponStatus();
    RechargeManager getRechargeManager();
    ShootStateManager getShootStateManager();
    void recharge();
    boolean canShoot();
}
