package official.sketchBook.engine.components_related.toUse_component.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.game.util_related.util.entity.AnchorPoint;
import official.sketchBook.engine.weapon_related.base_model.BaseWeapon;
import official.sketchBook.engine.weapon_related.base_model.RangeWeapon;

public class WeaponWieldComponent implements Component {

    /// Entidade que é dona da arma
    private final RangeWeaponWielderII wielder;

    /// flags de mira adicionais
    private boolean aimingUp, aimingDown;
    /// Ponto de ancoragem de uma arma
    private final AnchorPoint weaponAnchorPoint;
    /// Valores de ponto de ancoragem relativos ao ponto de ancoragem geral
    private float rxAP, ryAP;
    /// Arma que a entidade usa
    private BaseWeapon<?> weapon;

    public WeaponWieldComponent(RangeWeaponWielderII wielder) {
        this.wielder = wielder;

        this.weaponAnchorPoint = new AnchorPoint();
    }

    public void render(SpriteBatch batch){
        if(weapon != null){
            weapon.render(batch);
        }
    }

    @Override
    public void update(float delta) {
        if (weapon != null) {
            weapon.update(delta);
            weapon.updateAniPlayer(delta);
        }
    }

    public void primaryWeaponUse() {
        if (weapon != null) {
            weapon.use();
        }
    }

    public void secondaryWeaponUse() {
        if (weapon != null) {
            weapon.secondaryUse();
        }
    }

    /// Atualização da posição do anchorPoint ao usar um valor relativo
    public void syncWeaponPosToWielder() {
        weaponAnchorPoint.set(wielder.getX() + rxAP, wielder.getY() + ryAP);
    }

    /// Será que estamos usando uma arma de alcance
    public boolean isWeaponRanged() {
        return weapon != null && weapon instanceof RangeWeapon;
    }

    /// Obtemos a arma como sendo um tipo de arma desejado
    public RangeWeapon<?> getRangeWeapon() {
        if (weapon instanceof RangeWeapon<?> rw) {
            return rw;
        }
        throw new IllegalStateException("Current weapon is not a RangeWeapon.");
    }

    /// Obtém a arma que estamos a usar
    @SuppressWarnings("unchecked")
    public <T extends BaseWeapon<?>> T getWeapon(Class<T> clazz) {
        if (clazz.isInstance(weapon)) {
            return (T) weapon;
        }
        return null;
    }

    public void dispose(){
        if(weapon != null){
            weapon.dispose();
        }
    }

    public float getRxAP() {
        return rxAP;
    }

    public void setRxAP(float rxAP) {
        this.rxAP = rxAP;
    }

    public float getRyAP() {
        return ryAP;
    }

    public void setRyAP(float ryAP) {
        this.ryAP = ryAP;
    }

    public boolean isAimingUp() {
        return aimingUp;
    }

    public void setAimingUp(boolean aimingUp) {
        this.aimingUp = aimingUp;
    }

    public boolean isAimingDown() {
        return aimingDown;
    }

    public void setAimingDown(boolean aimingDown) {
        this.aimingDown = aimingDown;
    }

    public void setWeapon(BaseWeapon<?> weapon) {
        this.weapon = weapon;
    }

    public AnchorPoint getWeaponAnchorPoint() {
        return weaponAnchorPoint;
    }
}
