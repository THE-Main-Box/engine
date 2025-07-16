package official.sketchBook.weapon_related.util.weapon;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.weapon_related.util.weapon.status.RangeWeaponStatus;
import official.sketchBook.weapon_related.base_model.BaseWeapon;

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
        this.rechargeInputBufferTimer = new TimerComponent(0.1f);

        this.baseTime = baseTime;
        this.autoRecharge = autoRecharge;

        updateRechargeTimeLimit();
    }

    public void updateRechargeState(float delta) {
        syncAnimationWithRechargeState();

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
            if (!rangeCapableWeapon.getShootStateManager().isShooting()) {
                pendingRechargeAfterShoot = false;
                recharge();
            }
        }

        rechargingTimeLimit.update(delta);
        rechargeInputBufferTimer.update(delta);
    }

    private void autoRecharge() {
        if (weaponStatus.ammo <= 0 && !isRecharging() && !pendingRechargeAfterShoot) {
            if (rangeCapableWeapon.getShootStateManager().isShooting()) {
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
        rechargingTimeLimit.reset();
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

    private void syncAnimationWithRechargeState() {
        if (weapon.getAniPlayer() == null) return;

        if (isRecharging()) {
            // Se estamos recarregando e há modificador de velocidade
            if (weaponStatus.rechargeSpeedMultiplier > 1) {
                weapon.getAniPlayer()
                    .setAnimationSpeedToTargetDuration(rechargingTimeLimit.getTargetTime());
            }
        } else {
            // Se não estamos recarregando, reseta a velocidade para o padrão
            if (weapon.getAniPlayer().getAnimationSpeed() > 1) {
                weapon.getAniPlayer().setAnimationSpeed(1f);
            }
        }
    }


    private boolean canRecharge() {
        return weaponStatus.ammo < weaponStatus.maxAmmo
            && !rangeCapableWeapon.getShootStateManager().isShooting()
            && !isRecharging();
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
