package official.sketchBook.util_related.util.weapon;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.util_related.util.weapon.status.RangeWeaponStatus;
import official.sketchBook.weapon_related.base_model.BaseWeapon;

import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.shoot;

public class ShootStateManager extends RangeWeaponBaseManager {

    // Timer para controle de estado (ex: animação de tiro)
    private final TimerComponent stateBufferTimer;
    private final float stateBufferTime;

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
        this.stateBufferTimer = new TimerComponent();
        this.stateBufferTime = stateBufferTime;

        this.fireCooldownTimer = new TimerComponent();
        this.rateOfFire = rateOfFire;

        this.inputBufferTimer = new TimerComponent(0.002f);

        updateFireCooldownTimer();//inicia o temporizador
    }

    public void update(float delta) {
        fireCooldownTimer.update(delta);
        inputBufferTimer.update(delta);
        stateBufferTimer.update(delta);

        updateFireCooldownTimer();

        inputBufferTimer.resetByFinished();
        stateBufferTimer.resetByFinished();
        fireCooldownTimer.resetByFinished();

        updateInputBuffer();
    }

    private void updateInputBuffer(){
        if(inputBufferTimer.isRunning()){
            inputBufferTimer.stop();
            inputBufferTimer.reset();
            tryToShoot();
        }
    }

    private void updateFireCooldownTimer(){
        fireCooldownTimer.setTargetTime(rateOfFire / weaponStatus.fireCoolDownMultiplier);
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

        if(weapon.getAniPlayer() != null){
            weapon.getAniPlayer().playAnimation(shoot);
            weapon.getAniPlayer().setAutoUpdateAni(true);
            weapon.getAniPlayer().setAnimationLooping(false);
            weapon.getAniPlayer().setAniTick(0);
        }
    }

    private boolean canShoot() {
        return rangeCapableWeapon.canShoot() && !isShooting() && !isCoolingDown();
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
