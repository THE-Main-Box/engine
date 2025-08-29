package official.sketchBook.game.util_related.util.damage;

import official.sketchBook.engine.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.engine.gameObject_related.base_model.RangeWeaponWieldingEntity;

public class DamageType {

    /// Entidade que causou o dano
    public RangeWeaponWieldingEntity entityOfOrigin;
    /// Objeto que tomou o dano
    public DamageReceiverII target;

    /// Valor do dano
    public double amount;

}
