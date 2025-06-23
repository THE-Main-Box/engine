package official.sketchBook.util_related.info.util.weapon;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.weapon_related.base_model.RangeWeapon;

import static official.sketchBook.util_related.info.util.values.AnimationTitles.Weapon.recharge;

public class RechargeManager {
    private RangeWeapon weapon;
    private TimerComponent rechargingTimeLimit;

    public RechargeManager(RangeWeapon weapon) {
        this.rechargingTimeLimit = new TimerComponent();
        this.weapon = weapon;

        updateRechargeTimeLimit();
    }

    public void updateRechargeState(float delta) {
        updateRechargeRate();

        updateRechargingTime(delta);
        autoRecharge();
    }

    /// Atualiza o estado de recarga da arma com base no estado do temporizador
    private void updateRechargingTime(float delta) {
        if (weapon.getWeaponStatus() == null) return;

        rechargingTimeLimit.update(delta);

        if (rechargingTimeLimit.isFinished()) {
            rechargingTimeLimit.stop();
            rechargingTimeLimit.reset();
            weapon.getWeaponStatus().refillOnLimit();
        }
    }

    /// Atualiza o tempo de recarga com base no multiplicador
    private void updateRechargeTimeLimit() {
        if (weapon.getAniPlayer() == null) return;

        rechargingTimeLimit.setTargetTime(
            weapon.getAniPlayer().getTotalAnimationTime(weapon.getAniPlayer().getAnimationByKey(recharge))
                / weapon.getWeaponStatus().rechargeSpeedMultiplier
        );
    }

    /// Atualiza o tempo de recarga da animação com base no estado da arma e do multiplicador
    private void updateRechargeRate() {
        if (weapon.getWeaponStatus() == null) return;

        if (isRecharging() && weapon.getWeaponStatus().rechargeSpeedMultiplier > 1) {
            weapon.getAniPlayer().setAnimationSpeed(weapon.getWeaponStatus().rechargeSpeedMultiplier);
            updateRechargeTimeLimit();
        } else if (weapon.getAniPlayer().getAnimationSpeed() > 1) {
            weapon.getAniPlayer().setAnimationSpeed(1);
        }
    }

    /// Recarrega a arma depois depois que ela estiver vazia
    private void autoRecharge() {
        if (weapon.getWeaponStatus() == null) return;

        if (weapon.getWeaponStatus().ammo <= 0) {
            weapon.recharge();
        }
    }

    public void recharge(){
        this.rechargingTimeLimit.start();
    }

    public boolean isRecharging() {
        return rechargingTimeLimit.isRunning();
    }
}
