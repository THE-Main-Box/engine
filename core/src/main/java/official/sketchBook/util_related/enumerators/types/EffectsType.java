package official.sketchBook.util_related.enumerators.types;

import official.sketchBook.components_related.toUse_component.entity.DamageReceiveComponent.DamageReceiver;
import official.sketchBook.util_related.util.effect.EffectReceiver;
import official.sketchBook.util_related.util.effect.EffectsAppliance;

import java.util.function.Consumer;

public enum EffectsType {
    NONE(null),
    FREEZE(EffectsAppliance::freezeEffect);

    private final Consumer<EffectReceiver> affected;


    EffectsType(Consumer<EffectReceiver> affected) {
        this.affected = affected;
    }

    public void applyTo(EffectReceiver target) {
        if (affected != null && target != null)
            affected.accept(target);
    }

}
