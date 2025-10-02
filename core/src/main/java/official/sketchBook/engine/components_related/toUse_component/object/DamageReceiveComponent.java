package official.sketchBook.engine.components_related.toUse_component.object;

import com.badlogic.gdx.utils.Array;
import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.engine.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.PolishDamageData;


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
        if(data == null || data.damageData == null) return;
        this.applyDamage(data);//aplica o dano
        this.manageDeath();
        if(isAlive()) {
            this.applyInvincibility(data);//após aplicar o dano aplica o recuo
            this.applyKnockBack(data);//aplica o knockBack
        }
    }

    private void manageDeath(){
        if(isAlive()) return;

        owner.onDeath();
    }

    private void applyKnockBack(PolishDamageData data) {
        if (!data.damageData.isApplyKnockBack()) return;

        float kb = data.damageData.getKnockBack();

        //aplicamos a velocidade do knockback na direção do impacto
        owner.getBody().setLinearVelocity(
            (kb * data.dmgDirX),
            (kb * data.dmgDirY)
        );

    }

    private void applyInvincibility(PolishDamageData data) {
        // se não estiver vivo, ou o não pudermos aplicar a invencibilidade
        if (data.damageData.getInvincibilityTime() <= 0) return;

        initInvincibility(data.damageData.getInvincibilityTime());

    }

    /// executa a aplicação do dano
    private void applyDamage(PolishDamageData data) {
        /*
         *se não estivermos passando dados
         *se estivermos invencíveis
         *se não houver dados a respeito do dano
         *se o dano for 0 ou negativo
         *retornamos e não prosseguimos
         */
        if (invincible || data.damageData.getAmount() <= 0 || health <= 0) return;

        double dmg = data.damageData.getAmount();
        float dmgMod = data.damageData.getAmountMod();

        health -= dmg * (dmgMod > 0 ? dmgMod : 1);

        owner.onDamage();
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
