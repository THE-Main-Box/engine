package official.sketchBook.util_related.util.entity;

import official.sketchBook.components_related.toUse_component.entity.DamageReceiveComponent.DamageReceiver;
import official.sketchBook.gameObject_related.base_model.DamageAbleEntity;
import official.sketchBook.util_related.util.effect.EffectManager;

public class DamageType {

    /// Entidade que causou o dano
    public DamageAbleEntity entityOfOrigin;
    /// Objeto que tomou o dano
    public DamageReceiver target;

    /// Valor do dano
    public double amount;
    /// Efeito de dano
    public EffectManager effect;

    public DamageType() {
        this.effect = new EffectManager();
    }
}
