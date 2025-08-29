package official.sketchBook.game.util_related.util.effect;

import official.sketchBook.engine.components_related.integration_interfaces.EffectReceiverII;

public class EffectsAppliance {

    public static void burnEffect(EffectReceiverII damageReceiver) {
        System.out.println("🔥 Aplicando efeito de queimadura em " + damageReceiver.getClass().getSimpleName());
        // lógica real entraria aqui
    }

    public static void freezeEffect(EffectReceiverII damageReceiver) {
        System.out.println("Aplicando efeito de congelamento...");
        // lógica real entraria aqui
    }

}
