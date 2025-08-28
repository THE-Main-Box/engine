package official.sketchBook.components_related.toUse_component.object;

import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.util_related.util.damage.DamageType;

public class DamageReceiveComponent implements Component {

    private final DamageReceiverII receiver;
    private double health;
    private boolean invincible;

    public DamageReceiveComponent(DamageReceiverII receiver, double health) {
        this.receiver = receiver;
        this.health = health;
    }

    @Override
    public void update(float delta) {

    }

    public void damage(DamageType damage){
        if(health > 0){
            health -= damage.amount;
        } else {
            die();
        }
    }

    public void die(){
        receiver.die();
    }

    public DamageReceiverII getReceiver() {
        return receiver;
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

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }


}
