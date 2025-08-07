package official.sketchBook.util_related.util.damage;

import official.sketchBook.components_related.interfaces.DamageReceiver;
import official.sketchBook.gameObject_related.base_model.DamageAbleEntity;

public class DamageType {

    /// Entidade que causou o dano
    public DamageAbleEntity entityOfOrigin;
    /// Objeto que tomou o dano
    public DamageReceiver target;

    /// Valor do dano
    public double amount;

}
