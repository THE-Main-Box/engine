package official.sketchBook.util_related.util.effect;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.util_related.enumerators.types.EffectsType;

import java.util.ArrayList;
import java.util.List;

public class EffectManager {
    private final List<EffectInstance> effects = new ArrayList<>();
    private EffectReceiver target;

    public void update(float delta) {
        //Percorremos a lista de cima pra baixo
        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).update(delta, target);
            if(effects.get(i).isFinished()){
                effects.remove(i);
            }

        }
    }

    /// Adiciona mais efeitos com base em uma instancia existente
    public void addEffect(EffectInstance effect) {
        effects.add(effect);
    }

    /// Adiciona mais efeitos com base em uma instancia não existente
    public void addEffect(EffectsType effect, float duration, float interval, boolean applyOnce) {
        effects.add(new EffectInstance(effect, duration, interval, applyOnce));
    }

    /// Limpa todos os efeitos
    public void clear() {
        effects.clear();
    }

    /// Atualizamos o alvo afetado pro efeitos
    public void setTarget(EffectReceiver target) {
        this.target = target;
    }

    public EffectReceiver getTarget() {
        return target;
    }
}
