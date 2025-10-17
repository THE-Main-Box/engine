package official.sketchBook.engine.components_related.toUse_component.object;

import com.badlogic.gdx.utils.Array;
import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.components_related.integration_interfaces.DamageDealerII;
import official.sketchBook.engine.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.engine.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.engine.util_related.pools.PolishDamageDataPool;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.PolishDamageData;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.RawDamageData;
import official.sketchBook.engine.util_related.utils.registers.PolishDamageDataPoolRegister;


public class DamageReceiveComponent implements Component {

    /// Dono a quem receberá dano
    private final DamageReceiverII owner;
    /// O quanto que ainda pode receber de dano
    private double health;
    /// Flag para determinar se está invulnerável
    private boolean invincible;
    /// Temporizador para garantir que a invencibilidade tenha fim
    private final TimerComponent invincibleTimer;

    public DamageReceiveComponent(DamageReceiverII owner, double health) {
        this.owner = owner;
        this.health = health;

        this.invincibleTimer = new TimerComponent();
    }

    @Override
    public void update(float delta) {
        manageInvincibilityTime();
        invincibleTimer.update(delta);
    }

    /// Chama os métodos responsáveis pelo comportamento do dano
    public void damage(PolishDamageData data) {
        if (data == null || data.getDamageData() == null) return;

        // Aplicamos o dano primeiro
        this.applyDamage(data);

        // Verificamos se o receptor continua vivo
        boolean alive = isAlive();

        // Gerenciamos a morte se necessário
        this.manageDeath();

        // Se ainda estiver vivo, aplicamos invencibilidade e knockBack
        if (alive) {
            this.applyInvincibility(data);
            this.applyKnockBack(data);
        }

        if(!data.isReset()){
            data.endUse();
        }
    }

    public void damage(RawDamageData rawData, DamageDealerII dealer) {
        if (rawData == null) return;

        PolishDamageDataPool pool = PolishDamageDataPoolRegister.getPool(
            this.owner.getOwnerRoom()
        );

        PolishDamageData data = pool.obtain();

        pool.initPDD(
            this.owner,
            dealer,
            rawData,
            data
        );

        // Aplicamos o dano primeiro
        this.applyDamage(data);

        // Verificamos se o receptor continua vivo
        boolean alive = isAlive();

        // Gerenciamos a morte se necessário
        this.manageDeath();

        // Se ainda estiver vivo, aplicamos invencibilidade e knockBack
        if (alive) {
            this.applyInvincibility(data);
            this.applyKnockBack(data);
        }

        if(!data.isReset()){
            data.endUse();
        }
    }

    private void applyDamage(PolishDamageData data) {
        if (invincible || data.getDamageData().getAmount() <= 0 || health <= 0) return;

        double dmg = data.getDamageData().getAmount();
        float dmgMod = data.getDamageData().getAmountMod();

        health -= dmg * (dmgMod > 0 ? dmgMod : 1);

        owner.onDamage();
    }

    private void applyInvincibility(PolishDamageData data) {
        if (data.getDamageData().getInvincibilityTime() <= 0) return;
        initInvincibility(data.getDamageData().getInvincibilityTime());
    }

    private void applyKnockBack(PolishDamageData data) {
        if (!data.getDamageData().isApplyKnockBack()) return;

        float kb = data.getDamageData().getKnockBack();
        owner.getBody().setLinearVelocity(
            kb * data.getDmgDirX(),
            kb * data.getDmgDirY()
        );
    }

    private void manageDeath(){
        if(isAlive()) return;

        owner.onDeath();
    }

    /// Lida com o gerenciamento da flag de invencibilidade, usando o temporizador como base
    private void manageInvincibilityTime() {
        if (invincible) {//se invencível
            //se não estiver rodando o temporizador
            if (!invincibleTimer.isRunning()) {
                invincibleTimer.reset();
                invincibleTimer.start();
            }
            //se o temporizador estiver marcado como finalizado
            if (invincibleTimer.isFinished()) {
                invincibleTimer.stop();
                invincibleTimer.reset();
                invincible = false;
                invincibleTimer.setTargetTime(0);
            }

        } else if (invincibleTimer.isRunning()) {//se não estiver invencível e o temporizador ainda estiver rodando
            invincibleTimer.stop();
            invincibleTimer.reset();
        }
    }

    /**
     * Aplica a invencíbilidade
     *
     * @param time tempo que ela deve durar
     */
    public void initInvincibility(float time) {
        this.invincible = true;
        this.invincibleTimer.setTargetTime(time);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public DamageReceiverII getOwner() {
        return owner;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public boolean isInvincible() {
        return invincible;
    }


}
