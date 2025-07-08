package official.sketchBook.weapon_related.util.weapon;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.weapon_related.util.weapon.status.RangeWeaponStatus;
import official.sketchBook.weapon_related.base_model.BaseWeapon;

public class ShootStateManager extends RangeWeaponBaseManager {

    // Timer para controle de estado (ex: animação de tiro)
    private final TimerComponent stateBufferTimer;

    // Tempo mínimo entre disparos
    private final TimerComponent fireCooldownTimer;
    private final float rateOfFire;

    // Buffer de input, para armazenar tentativa de tiro quando não é possível
    private final TimerComponent inputBufferTimer;

    // Callback de execução do tiro
    private Runnable onShootCallback;

    public ShootStateManager(
        BaseWeapon<?> weapon,
        RangeWeaponStatus weaponStatus,
        float stateBufferTime,
        float rateOfFire
    ) {
        super(weapon, weaponStatus);
        this.stateBufferTimer = new TimerComponent(stateBufferTime);

        this.fireCooldownTimer = new TimerComponent();
        this.rateOfFire = rateOfFire;

        this.inputBufferTimer = new TimerComponent(0.2f);

        updateFireCooldownTimer();//inicia o temporizador
    }

    public void update(float delta) {
        updateFireCooldownTimer();

        inputBufferTimer.resetByFinished();
        stateBufferTimer.resetByFinished();
        fireCooldownTimer.resetByFinished();

        updateInputBuffer();

        fireCooldownTimer.update(delta);
        inputBufferTimer.update(delta);
        stateBufferTimer.update(delta);
    }

    private void updateInputBuffer(){
        if(inputBufferTimer.isRunning()){
            if(canShoot()){
                inputBufferTimer.stop();
                inputBufferTimer.reset();
                triggerShootCallback();
            }
        }
    }

    private void updateFireCooldownTimer(){
        fireCooldownTimer.setTargetTime(rateOfFire / weaponStatus.fireCoolDownSpeedMultiplier);
    }

    /**
     * Tenta atirar. Se possível, executa o callback imediatamente.
     * Caso contrário, inicia o input buffer.
     */
    public void tryToShoot() {
        if (canShoot()) {
            triggerShootCallback();
        } else {
            inputBufferTimer.reset();
            inputBufferTimer.start();
        }
    }

    private void triggerShootCallback() {
        // Inicia timers de estado e cooldown
        stateBufferTimer.reset();
        stateBufferTimer.start();

        fireCooldownTimer.reset();
        fireCooldownTimer.start();

        // Executa o callback se definido
        if (onShootCallback != null) {
            onShootCallback.run();
        }

        weaponStatus.ammo -= weaponStatus.ammoCost;
    }

    private boolean canShoot() {
        return rangeCapableWeapon.canShoot() && !isCoolingDown() && weaponStatus.ammo > 0;
    }

    public boolean isShooting() {
        return stateBufferTimer.isRunning() && !stateBufferTimer.isFinished();
    }

    public boolean isCoolingDown() {
        return fireCooldownTimer.isRunning() && !fireCooldownTimer.isFinished();
    }

    // Define callback de execução do tiro
    public void setOnShootCallback(Runnable callback) {
        this.onShootCallback = callback;
    }
}
