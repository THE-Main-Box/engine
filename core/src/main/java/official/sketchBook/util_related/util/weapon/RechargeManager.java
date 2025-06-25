package official.sketchBook.util_related.util.weapon;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.util_related.util.weapon.status.RangeWeaponStatus;
import official.sketchBook.weapon_related.base_model.BaseWeapon;

import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.shoot;

public class RechargeManager extends RangeWeaponBaseManager {

    private final TimerComponent rechargingTimeLimit;
    private final TimerComponent rechargeInputBufferTimer;
    private boolean bufferRechargeRequest = false;

    private float baseTime;
    private boolean autoRecharge;
    private boolean pendingRechargeAfterShoot = false;

    private Runnable onRechargeFinishedCallback;
    private Runnable onRechargeStartCallback;

    public RechargeManager(
        BaseWeapon<?> baseWeapon,
        RangeWeaponStatus weaponStatus,
        float baseTime,
        boolean autoRecharge
    ) {
        super(baseWeapon, weaponStatus);

        this.rechargingTimeLimit = new TimerComponent();
        this.rechargeInputBufferTimer = new TimerComponent(0.02f); // Buffer manual: 200ms

        this.baseTime = baseTime;
        this.autoRecharge = autoRecharge;

        updateRechargeTimeLimit();
    }

    public void updateRechargeState(float delta) {
        rechargingTimeLimit.update(delta);
        rechargeInputBufferTimer.update(delta);

        updateRechargeRate();

        // Finalização da recarga
        if (rechargingTimeLimit.isFinished()) {
            rechargingTimeLimit.stop();
            rechargingTimeLimit.reset();
            onRechargeFinish();
        }

        // Tentativa de auto-recarregar
        if (autoRecharge) autoRecharge();

        // Se o buffer foi solicitado, tentamos recarregar quando terminar
        if (bufferRechargeRequest && rechargeInputBufferTimer.isFinished()) {
            bufferRechargeRequest = false; // Reseta a flag para não repetir
            attemptManualRecharge();       // Tenta recarregar de novo
        }

        // Se acabou de atirar e deve esperar antes de recarregar
        if (pendingRechargeAfterShoot) {
            if (!isPlayingShootAnimation()) {
                pendingRechargeAfterShoot = false;
                recharge();
            }
        }
    }

    private void autoRecharge() {
        if (weaponStatus.ammo <= 0 && !isRecharging() && !pendingRechargeAfterShoot) {
            if (isPlayingShootAnimation()) {
                pendingRechargeAfterShoot = true;
            } else {
                recharge();
            }
        }
    }

    public void recharge() {
        // Chamada direta: normalmente vem de um input do jogador
        if (canRecharge()) {
            startRecharge();
        }
        // Se não puder, iniciar buffer
        else if (!rechargeInputBufferTimer.isRunning()) {
            rechargeInputBufferTimer.reset();
            rechargeInputBufferTimer.start();
            bufferRechargeRequest = true;
        }
    }

    private void attemptManualRecharge() {
        if (canRecharge()) {
            startRecharge();
        }
        // Se ainda não puder, não tentamos novamente
    }

    private void startRecharge() {
        rechargingTimeLimit.start();
        updateRechargeTimeLimit();

        if (onRechargeStartCallback != null) {
            onRechargeStartCallback.run();
        }
    }

    private void onRechargeFinish() {
        weaponStatus.refillOnLimit();

        if (onRechargeFinishedCallback != null) {
            onRechargeFinishedCallback.run();
        }
    }

    private void updateRechargeTimeLimit() {
        rechargingTimeLimit.setTargetTime(baseTime / weaponStatus.rechargeSpeedMultiplier);
    }

    private void updateRechargeRate() {
        if (isRecharging() && weaponStatus.rechargeSpeedMultiplier > 1 && weapon.getAniPlayer() != null) {
            weapon.getAniPlayer().setAnimationSpeedToTargetDuration(rechargingTimeLimit.getTargetTime());
        } else if (weapon.getAniPlayer() != null && weapon.getAniPlayer().getAnimationSpeed() > 1) {
            weapon.getAniPlayer().setAnimationSpeed(1);
        }
    }

    private boolean canRecharge() {
        return weaponStatus.ammo < weaponStatus.maxAmmo
            && !rangeCapableWeapon.getShootStateManager().isShooting()
            && !isRecharging();
    }

    private boolean isPlayingShootAnimation() {
        return weapon.getAniPlayer() != null
            && weapon.getAniPlayer().getCurrentAnimationKey().equals(shoot);
    }

    public boolean isRecharging() {
        return rechargingTimeLimit.isRunning();
    }

    public void setOnRechargeStartCallback(Runnable onRechargeStartCallback) {
        this.onRechargeStartCallback = onRechargeStartCallback;
    }

    public void setOnRechargeFinishedCallback(Runnable callback) {
        this.onRechargeFinishedCallback = callback;
    }
}
