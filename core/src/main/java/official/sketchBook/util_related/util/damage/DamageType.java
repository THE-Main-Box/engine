package official.sketchBook.util_related.util.damage;

import official.sketchBook.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.gameObject_related.base_model.RangeWeaponWieldingEntity;

public class DamageType {

    /// Entidade que causou o dano
    public RangeWeaponWieldingEntity entityOfOrigin;
    /// Objeto que tomou o dano
    public DamageReceiverII target;

    /// Valor do dano
    public double amount;

}
