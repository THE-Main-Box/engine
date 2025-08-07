package official.sketchBook.util_related.util.effect;

import official.sketchBook.components_related.interfaces.EffectReceiver;

public class EffectsAppliance {

    public static void burnEffect(EffectReceiver damageReceiver) {
        System.out.println("🔥 Aplicando efeito de queimadura em " + damageReceiver.getClass().getSimpleName());
        // lógica real entraria aqui
    }

    public static void freezeEffect(EffectReceiver damageReceiver) {
        System.out.println("Aplicando efeito de congelamento...");
        // lógica real entraria aqui
    }

}
