package official.sketchBook.components_related.toUse_component.object;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.interfaces.DamageReceiver;
import official.sketchBook.util_related.util.damage.DamageType;

public class DamageReceiveComponent implements Component {

    private final DamageReceiver receiver;
    private double health;
    private boolean invincible;

    public DamageReceiveComponent(DamageReceiver receiver, double health) {
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

    public DamageReceiver getReceiver() {
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
