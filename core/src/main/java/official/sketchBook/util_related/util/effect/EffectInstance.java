package official.sketchBook.util_related.util.effect;

import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.util_related.enumerators.types.EffectsType;


public class EffectInstance {
    /// Efeito que deve ser aplicado
    private final EffectsType effect;
    /// Duração do efeito
    private final TimerComponent durationTimer;
    /// Espaço de tempo entre uma aplicação e outra do efeito
    private final TimerComponent intervalTimer;
    /// Flag que determina se o efeito deve ser aplicado uma única vez ou não
    private final boolean applyOnce;

    /// Flag para determinar se aplicamos o efeito ou não
    private boolean applied;

    public EffectInstance(EffectsType effect, float duration, float interval, boolean applyOnce) {
        if (effect == null) throw new IllegalArgumentException("Effect can't be null");
        this.effect = effect;

        this.durationTimer = new TimerComponent(duration);
        this.intervalTimer = (interval > 0) ? new TimerComponent(interval) : null;

        this.applied = false;
        this.applyOnce = applyOnce;

    }

    private void apply(EffectReceiver target){
        if(target == null) return;
        effect.applyTo(target);
        applied = true;
    }

    public void update(float delta, EffectReceiver target) {
        //Se tivermos terminado paramos de atualizar
        if(isFinished()){
            durationTimer.stop();
            return;
        }

        //Se não tivermos iniciado e estivermos atualizando,
        // iniciamos garantindo um tempo limpo
        if(!durationTimer.isRunning()){
            durationTimer.reset();
            durationTimer.start();
        }

        //Verificamos se ainda não aplicamos e se devemos aplicar apenas uma única vez
        if(applyOnce && !applied){//Aplicamos caso a if esteja correta
            apply(target);
            return;
        }
        //Se tivermos um tempo de intervalo, ou seja um delay até ser executado
        if(intervalTimer != null){
            //Verificamos se está rodando, se não iniciamos
            if(!intervalTimer.isRunning()){
                intervalTimer.reset();
                intervalTimer.start();
            }
            //Se tiver terminado executamos, ou se não tiver sido aplicado ainda
            if(intervalTimer.isFinished() || !applied){
                apply(target);
                intervalTimer.reset();
            }

            //Atualizamos no final de tudo para garantir uma sincronia maior
            intervalTimer.update(delta);
        } else {//caso não estejamos lidando com nenhum caso especial, iremos lidar com tudo de modo contínuo
            apply(target);
        }
        //atualizamos no final de tudo para garantir um efeito correto
        durationTimer.update(delta);
    }

    public EffectsType getEffect() {
        return effect;
    }

    public boolean isFinished() {
        return durationTimer.isFinished();
    }
}
