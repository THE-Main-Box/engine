package official.sketchBook.animation_related;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectAnimationPlayer {
    private List<Sprite> currentAnimation;              // animação atual
    private Sprite currentSprite;                       // sprite atual baseado no anitick
    private String currentAnimationKey;                 // a chave da animação atual
    private Map<String, List<Sprite>> animations;       // lista de animações associadas com um chave String
    private int aniTick;                                // contagem da animação
    private float frameDuration;                        // duração da animação
    private float elapsedTime;                          // tempo decorrido desde a ultima troca de sprites
    private double animationSpeed;

    private boolean autoUpdateAni;                      // se deve atualizar a animação automaticamente
    private boolean animationLooping = true;  // por padrão true (looping)

    public ObjectAnimationPlayer() {
        this.animations = new HashMap<>();
        this.autoUpdateAni = true;

        animationSpeed = 1.0;
    }

    //salva uma nova animação em uma lista hash map contendo uma lista de sprites da animação em questão
    public void addAnimation(String animationTitle, List<Sprite> animation) {
        animations.put(animationTitle, animation);
    }

    //É preciso 'settar' o autoUpdateAni manualmente dentro de uma animação que será tocada várias vezes,
    // porém não deve repetir em loop
    public void update(float deltaTime) {
        if (currentAnimation == null || currentAnimation.isEmpty() || !autoUpdateAni) return;

        elapsedTime += deltaTime;

        currentSprite = getCurrentSprite();

        if (currentSprite.getDuration() > 0) {
            frameDuration = currentSprite.getDuration();
        }

        if (elapsedTime >= (float) (frameDuration / animationSpeed)) {
            aniTick++;
            elapsedTime -= (float) (frameDuration / animationSpeed);

            if (aniTick >= currentAnimation.size()) {
                if (animationLooping) {
                    aniTick = 0; // reseta para looping
                } else {
                    aniTick = currentAnimation.size() - 1; // mantém no último frame se não for looping
                    autoUpdateAni = false; // para de atualizar a animação, pois terminou
                }
            }
        }
    }


    public float getTotalAnimationTime(List<Sprite> animation) {
        float value = 0;

        for (Sprite sprite : animation) {
            value += sprite.getDuration() > 0 ? sprite.getDuration() : 0;
        }

        return value;
    }

    public void setAnimationSpeedToTargetDuration(float targetDuration) {
        if (currentAnimation == null || currentAnimation.isEmpty() || targetDuration == 0) return;

        // Calcula a duração total da animação atual
        float totalAnimationTime = getTotalAnimationTime(currentAnimation); // Já sendo feita no update

        // Se a duração total da animação for 0, não faz ajustes
        if (totalAnimationTime == 0) return;

        // Ajusta a velocidade da animação, alterando a animationSpeed de forma que
        // a animação termine no tempo desejado
        animationSpeed = totalAnimationTime / targetDuration;
    }

    public void resetAnimationSpeed() {
        animationSpeed = 1;
    }

    // determina a animação atual com base em uma lista de sprite passado
    public void setCurrentAnimation(List<Sprite> animation) {
        if (animation != null && !animation.isEmpty()) {
            this.currentAnimation = animation;
            this.aniTick = 0;
            this.elapsedTime = 0;
            this.currentAnimationKey = null; // Resetando a chave já que foi setada diretamente
        }
    }

    public boolean isPlaying(String animationTitle) {
        return currentAnimation != null && currentAnimationKey.equals(animationTitle);
    }

    // toca uma animação com base na chave passada
    public void playAnimation(String title) {
        List<Sprite> newAnimation = animations.get(title);

        // verifica se a animação a ser atualizada existe antes de atualizar
        if (newAnimation != null && !newAnimation.isEmpty() && currentAnimation != newAnimation) {
            // se existe resetamos as contagens e setamos a currentAnimation
            currentAnimation = newAnimation;
            currentAnimationKey = title;
            animationLooping = true;
            aniTick = 0;
            elapsedTime = 0;

        }
    }

    /** Se a animação não está mais atualizando (chegou ao último frame e não está looping) */
    public boolean isAnimationFinished() {
        // terminou quando:
        // 1) não está em loop
        // 2) o aniTick está no último índice
        // 3) o autoUpdateAni foi desligado pelo update()
        return !animationLooping
            && aniTick == currentAnimation.size()-1
            && !autoUpdateAni;
    }

    // Setter para o looping
    public void setAnimationLooping(boolean looping) {
        if(this.animationLooping != looping){
            this.animationLooping = looping;
        }
    }

    public boolean isAnimationLooping() {
        return animationLooping;
    }

    // seleciona o sprite atual baseado no index da animação que está senod tocada(anitick)
    public Sprite getCurrentSprite() {
        return currentAnimation.get(aniTick);
    }

    public List<Sprite> getCurrentAnimation() {
        return currentAnimation;
    }

    public boolean isAutoUpdateAni() {
        return autoUpdateAni;
    }

    //determina se a animação deve tocar automaticamente ou não
    public void setAutoUpdateAni(boolean autoUpdateAni) {
        if (this.autoUpdateAni != autoUpdateAni) {
            this.autoUpdateAni = autoUpdateAni;
        }
    }

    public String getCurrentAnimationKey() {
        return currentAnimationKey;
    }

    public void setAniTick(int aniTick) {
        if (aniTick >= 0 && aniTick <= currentAnimation.size()) {
            this.aniTick = aniTick;
        }
    }

    public void setCurrentSprite(Sprite currentSprite) {
        this.currentSprite = currentSprite;
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public List<Sprite> getAnimationByKey(String key) {
        return animations.get(key);
    }

}
