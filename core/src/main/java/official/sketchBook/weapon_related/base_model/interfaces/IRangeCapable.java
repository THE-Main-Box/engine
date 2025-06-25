package official.sketchBook.weapon_related.base_model.interfaces;

import official.sketchBook.util_related.util.weapon.RechargeManager;
import official.sketchBook.util_related.util.weapon.status.RangeWeaponStatus;

public interface IRangeCapable {
    RangeWeaponStatus getWeaponStatus();
    RechargeManager getRechargeManager();
    void recharge();
}
