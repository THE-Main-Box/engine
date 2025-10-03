package official.sketchBook.engine.components_related.toUse_component.object;

import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.game.util_related.enumerators.types.EffectsType;
import official.sketchBook.game.util_related.instance_data_related.effect.EffectInstance;
import official.sketchBook.engine.components_related.integration_interfaces.EffectReceiverII;

import java.util.ArrayList;
import java.util.List;

public class EffectReceiverComponent implements Component {
    /// Lista de efeitos
    private final List<EffectInstance> effects = new ArrayList<>();

    /// Lista de efeitos que não podem ser aplicados no target
    private final List<EffectsType> effectsImmune = new ArrayList<>();

    /// Objeto que irá receber os efeitos
    private EffectReceiverII target;

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
    public void setTarget(EffectReceiverII target) {
        this.target = target;
    }

    public EffectReceiverII getTarget() {
        return target;
    }
}
