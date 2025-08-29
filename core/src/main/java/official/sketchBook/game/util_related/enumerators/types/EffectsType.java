package official.sketchBook.game.util_related.enumerators.types;

import official.sketchBook.engine.components_related.integration_interfaces.EffectReceiverII;
import official.sketchBook.game.util_related.util.effect.EffectsAppliance;

import java.util.function.Consumer;

public enum EffectsType {
    NONE(null),
    FREEZE(EffectsAppliance::freezeEffect);

    private final Consumer<EffectReceiverII> affected;


    EffectsType(Consumer<EffectReceiverII> affected) {
        this.affected = affected;
    }

    public void applyTo(EffectReceiverII target) {
        if (affected != null && target != null)
            affected.accept(target);
    }

}
