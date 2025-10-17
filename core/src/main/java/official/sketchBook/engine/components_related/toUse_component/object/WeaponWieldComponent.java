package official.sketchBook.engine.components_related.toUse_component.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.point.AnchorPoint;
import official.sketchBook.engine.weapon_related.base_model.BaseWeapon;
import official.sketchBook.engine.weapon_related.base_model.RangeWeapon;

public class WeaponWieldComponent implements Component {

    /// Entidade que é dona da arma
    private final RangeWeaponWielderII wielder;

    /// Vetor de mira
    private final Vector2 aim = new Vector2();

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

    public void render(SpriteBatch batch) {
        if (weapon != null) {
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

    public BaseWeapon<?> getBaseWeapon() {
        return weapon;
    }

    public void dispose() {
        if (weapon != null) {
            weapon.dispose();
        }
    }

    /**
     * Determina a mira caso ela não seja maior que 1 ou menor que -1 em cada eixo
     *
     * @param x mira do eixo x
     * @param y mira do eixo y
     */
    public void aim(int x, int y) {
        if (Math.abs(x) > 1 || Math.abs(y) > 1) return;

        aim.set(x, y);
    }

    public Vector2 getAim() {
        return aim;
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
        return aim.y > 0; //Se mira for maior que 0 estamos mirando pra cima
    }

    public boolean isAimingDown() {
        return aim.y < 0; //Se mira for menor que 0 estamos mirando pra baixo
    }

    public boolean isAimingRight() {
        return aim.x > 0; //Se mira for maior que 0 estamos mirando pra direita
    }

    public boolean isAimingLeft() {
        return aim.x < 0; //Se mira for menor que 0 estamos mirando pra esquerda
    }

    public boolean isDiagonalAim(){
        return Math.abs(aim.x) > 0 && Math.abs(aim.y) > 0;
    }

    public void setWeapon(BaseWeapon<?> weapon) {
        this.weapon = weapon;
    }

    public AnchorPoint getWeaponAnchorPoint() {
        return weaponAnchorPoint;
    }
}
