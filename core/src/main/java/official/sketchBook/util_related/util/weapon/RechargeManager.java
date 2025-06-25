package official.sketchBook.util_related.util.weapon;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.util_related.util.weapon.status.RangeWeaponStatus;
import official.sketchBook.weapon_related.base_model.BaseWeapon;

public class RechargeManager extends RangeWeaponBaseManager {
    /// Tempo de recarga da arma
    private TimerComponent rechargingTimeLimit;
    /// Tempo base para validar o tempo de recarga
    private float baseTime;

    /// Flag de comportamento para dizer se pode recarregar assim que a munição acabar
    private boolean autoRecharge;

    public RechargeManager(
        BaseWeapon<?> baseWeapon,
        RangeWeaponStatus weaponStatus,
        float baseTime,
        boolean autoRecharge
    ) {
        super(baseWeapon, weaponStatus);

        this.rechargingTimeLimit = new TimerComponent();
        this.baseTime = baseTime;

        this.autoRecharge = autoRecharge;

        updateRechargeTimeLimit();
    }

    public void updateRechargeState(float delta) {
        updateRechargeRate();

        updateRechargingTime(delta);

        if (autoRecharge) {
            autoRecharge();
        }
    }

    /// Atualiza o estado de recarga da arma com base no estado do temporizador
    private void updateRechargingTime(float delta) {
        rechargingTimeLimit.update(delta);

        if (rechargingTimeLimit.isFinished()) {
            onRechargeFinish();
        }
    }

    private void onRechargeFinish() {
        rechargingTimeLimit.stop();
        rechargingTimeLimit.reset();

        weaponStatus.refillOnLimit();
    }

    /// Atualiza o tempo de recarga com base no multiplicador de uma animação
    private void updateRechargeTimeLimit() {
        rechargingTimeLimit.setTargetTime(baseTime / weaponStatus.rechargeSpeedMultiplier);
    }

    /// Atualiza o tempo de recarga da animação com base no estado da arma e do multiplicador
    private void updateRechargeRate() {
        if (isRecharging() && weaponStatus.rechargeSpeedMultiplier > 1) {
            if (weapon.getAniPlayer() != null) {
                weapon.getAniPlayer().setAnimationSpeedToTargetDuration(rechargingTimeLimit.getTargetTime());
            }
            updateRechargeTimeLimit();
        } else if (weapon.getAniPlayer() != null && weapon.getAniPlayer().getAnimationSpeed() > 1) {
            weapon.getAniPlayer().setAnimationSpeed(1);
        }
    }

    /// Recarrega a arma depois depois que ela estiver vazia
    private void autoRecharge() {
        if (weaponStatus.ammo <= 0) {
            rangeCapableWeapon.recharge();
        }
    }

    public void recharge() {
        this.rechargingTimeLimit.start();
    }

    public boolean isRecharging() {
        return rechargingTimeLimit.isRunning();
    }
}
