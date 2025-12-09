package official.sketchBook.engine.components_related.toUse_component.object;

import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageDealerII;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageReceiverII;
import official.sketchBook.engine.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.engine.util_related.pools.PolishDamageDataPool;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.PolishDamageData;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.RawDamageData;
import official.sketchBook.engine.util_related.utils.registers.PolishDamageDataPoolRegister;

public class DamageReceiveComponent implements Component {

    private final DamageReceiverII owner;
    private double health;
    private boolean invincible;
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

        processDamage(data);

        // Sempre liberar o objeto para o pool
        data.endUse();
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

        processDamage(data);

        // Sempre liberar o objeto para o pool
        data.endUse();
    }

    private void processDamage(PolishDamageData data) {
        // Aplicar dano (ignorar invencibilidade aqui, pois morte ignora invencibilidade)
        applyDamage(data);

        // Gerenciar morte (verifica se ficou com health <= 0)
        DamageDealerII dealer = data.getDealer();
        if (!isAlive()) {
            manageDeath(dealer);
            return; // Sair cedo se morreu
        }

        // Se sobreviveu, aplicar invencibilidade e knockback
        applyInvincibility(data);
        applyKnockBack(data);
    }

    private void applyDamage(PolishDamageData data) {
        // Não aplicar dano se já está morto
        if (!isAlive()) return;

        // Não aplicar dano se o valor é inválido
        if (data.getDamageData().getAmount() <= 0) return;

        // Não aplicar dano se está invencível
        if (invincible) return;

        double dmg = data.getDamageData().getAmount();
        float dmgMod = data.getDamageData().getAmountMod();

        health -= dmg * dmgMod;

        owner.onDamage(data.getDealer());
    }

    private void applyInvincibility(PolishDamageData data) {
        float invTime = data.getDamageData().getInvincibilityTime();
        if (invTime > 0) {
            initInvincibility(invTime);
        }
    }

    private void applyKnockBack(PolishDamageData data) {
        if (!data.getDamageData().isApplyKnockBack()) return;

        float kb = data.getDamageData().getKnockBack();
        float kbM = data.getDamageData().getKnockBackMulti();

        owner.getBody().setLinearVelocity(
            (kb * kbM) * data.getDmgDirX(),
            (kb * kbM) * data.getDmgDirY()
        );
    }

    private void manageDeath(DamageDealerII dealer) {
        owner.onDeath(dealer);
    }

    private void manageInvincibilityTime() {
        if (!invincible) {
            // Se não está invencível e o timer está rodando, parar
            if (invincibleTimer.isRunning()) {
                invincibleTimer.stop();
                invincibleTimer.reset();
            }
            return;
        }

        // Está invencível
        if (!invincibleTimer.isRunning()) {
            invincibleTimer.reset();
            invincibleTimer.start();
        }

        if (invincibleTimer.isFinished()) {
            invincibleTimer.stop();
            invincibleTimer.reset();
            invincible = false;
            invincibleTimer.setTargetTime(0);
        }
    }

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
