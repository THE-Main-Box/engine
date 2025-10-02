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

    /// Dados a respeito dos danos recebidos
    private final Array<PolishDamageData> damageEventList;

    public DamageReceiveComponent(DamageReceiverII owner, double health) {
        this.owner = owner;
        this.health = health;

        this.invincibleTimer = new TimerComponent();
        this.damageEventList = new Array<>();
    }

    @Override
    public void update(float delta) {
        manageInvincibilityTime();
        invincibleTimer.update(delta);

        processDamageList();
    }

    /// Processa a lista de dano disponível
    private void processDamageList() {
        //Percorre a lista de cima pra baixo
        for (int i = damageEventList.size - 1; i >= 0; i--) {
            applyDamage(damageEventList.get(i));//tenta aplicar o dano
            damageEventList.removeIndex(i);//Após tentar processar o dano, remove da lista
        }
    }

    private void applyDamage(PolishDamageData data) {
        if (data == null || invincible) return;
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
            }

        } else if (invincibleTimer.isRunning()) {//se não estiver invencível e o temporizador ainda estiver rodando
            invincibleTimer.stop();
            invincibleTimer.reset();
        }
    }


    public void damage(PolishDamageData data) {
        if (health > 0) {
            this.applyDamage(data);
            owner.onDamage();
        } else {
            owner.onDeath();
        }
    }

    /**
     * Aplica a invencíbilidade
     *
     * @param time tempo que ela deve durar
     */
    public void applyInvincibility(float time) {
        this.invincible = true;
        this.invincibleTimer.setTargetTime(time);
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
