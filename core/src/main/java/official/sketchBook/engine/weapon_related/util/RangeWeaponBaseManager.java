package official.sketchBook.engine.weapon_related.util;

import official.sketchBook.engine.weapon_related.util.status.RangeWeaponStatus;
import official.sketchBook.engine.weapon_related.base_model.BaseWeapon;
import official.sketchBook.engine.weapon_related.base_model.interfaces.IRangeCapable;

import java.security.InvalidParameterException;

public class RangeWeaponBaseManager {
    /// Status de alcance da arma, exclusivo para armas que podem disparar projéteis
    protected RangeWeaponStatus weaponStatus;
    /// Variável genérica para usar qualquer tipo de arma de alcance futuro
    protected BaseWeapon<?> weapon;
    protected IRangeCapable rangeCapableWeapon;

    public RangeWeaponBaseManager(BaseWeapon<?> weapon, RangeWeaponStatus weaponStatus) {
        initWeapon(weapon);
        initWeaponStatus(weaponStatus);
    }

    private void initWeaponStatus(RangeWeaponStatus weaponStatus){
        if(weaponStatus != null){
            this.weaponStatus = weaponStatus;
        } else {
            throw new NullPointerException("Precisamos que o status de arma não seja null");
        }
    }

    /**
     * Realiza uma validação de se é possível utilizar este sistema ou não
     *
     * @param weapon arma básica padrão
     */
    private void initWeapon(BaseWeapon<?> weapon) {
        if (weapon instanceof IRangeCapable rangeCapable) {
            this.weapon = weapon;
            this.rangeCapableWeapon = rangeCapable;
        } else {
            throw new InvalidParameterException(
                "Arma precisa implementar interface ["
                    + IRangeCapable.class.getSimpleName()
                    + "] para poder usar sistemas de arma de alcance"
            );
        }
    }
}
