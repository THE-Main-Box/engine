package official.sketchBook.components_related.toUse_component.object;

import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.util_related.enumerators.types.EffectsType;
import official.sketchBook.util_related.util.effect.EffectInstance;
import official.sketchBook.util_related.util.effect.EffectReceiver;

import java.util.ArrayList;
import java.util.List;

public class EffectManagerComponent implements Component {
    /// Lista de efeitos
    private final List<EffectInstance> effects = new ArrayList<>();

    /// Lista de efeitos que não podem ser aplicados no target
    private final List<EffectsType> effectsImmune = new ArrayList<>();

    /// Objeto que irá receber os efeitos
    private EffectReceiver target;

    /// Atualizamos os efeitos que podem ser aplicados e removemos o que precisam ser removidos
    public void update(float delta) {
        //Percorremos a lista de cima pra baixo
        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).update(delta, target);
            if (effects.get(i).isFinished()) {
                effects.remove(i);
            }

        }
    }

    /// Verifica se algum efeito da lista está presente em uma lista de exceções, se sim removemos de lá
    private void removeImmuneEffects() {
        for (int i = effects.size() - 1; i >= 0; i--) {
            if (effectsImmune.contains(effects.get(i).getEffect())) {
                effects.remove(i);
            }
        }
    }

    /// Adiciona uma imunidade
    public void addEffectImmunity(EffectsType effect){
        if(effect == null) return;
        effectsImmune.add(effect);
        removeImmuneEffects();
    }

    /// Adiciona mais efeitos com base em uma instância existente, mas caso não possa ser aplicada, ignora
    public void addEffect(EffectInstance effect) {
        if(effect == null) return;
        if (effectsImmune.contains(effect.getEffect())) return;
        effects.add(effect);
    }

    /// Adiciona mais efeitos com base em uma instância não existente
    public void addEffect(EffectsType effect, float duration, float interval, boolean applyOnce) {
        addEffect(new EffectInstance(effect, duration, interval, applyOnce));
    }

    /// Limpa todos os efeitos
    public void clear() {
        effects.clear();
    }

    public void clearImmunity(){
        effectsImmune.clear();
    }

    /// Atualizamos o alvo afetado pro efeitos
    public void setTarget(EffectReceiver target) {
        this.target = target;
    }

    public EffectReceiver getTarget() {
        return target;
    }
}
