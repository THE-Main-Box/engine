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
    private boolean autoUpdateAni;                      // se deve atualizar a animação automaticamente
    private double animationSpeed;

    public ObjectAnimationPlayer() {
        this.animations = new HashMap<>();
        this.autoUpdateAni = true;

        animationSpeed = 1.0;
    }

    //salva uma nova animação em uma lista hash map contendo uma lista de sprites da animação em questão
    public void addAnimation(String animationTitle, List<Sprite> animation) {
        animations.put(animationTitle, animation);
    }

    //atualiza o frame atual com base em parametros pré-definidos e variaveis
    public void update(float deltaTime) {
        if (currentAnimation == null || currentAnimation.isEmpty() || !autoUpdateAni) return;

        elapsedTime += deltaTime;                 // Atualiza o tempo decorrido

        currentSprite = getCurrentSprite();       // seleciona o sprite atual com base no aniTick

        // se a duração for menor ou igual a 0 pula essa divisão
        if (currentSprite.getDuration() > 0) {
            frameDuration = currentSprite.getDuration();
        }

        // se o tempo decorrido for maior ou igual a duração do frame atualiza ele
        if (elapsedTime >= (float) (frameDuration / animationSpeed)) {

            aniTick++;
            elapsedTime -= (float) (frameDuration / animationSpeed);

            if (aniTick >= currentAnimation.size()) {  // reseta o anitick para reiniciar a animação
                aniTick = 0;
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

    public void resetAnimationSpeed(){
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

    // toca uma animação com base na chave passada
    public void setAnimation(String title) {
        List<Sprite> newAnimation = animations.get(title);

        // verifica se a animação a ser atualizada existe antes de atualizar
        if (newAnimation != null && !newAnimation.isEmpty() && currentAnimation != newAnimation) {
            // se existe resetamos as contagens e setamos a currentAnimation
            currentAnimation = newAnimation;
            currentAnimationKey = title;
            aniTick = 0;
            elapsedTime = 0;

        }
    }

    //determina o sprite atual com base no indice da chave da animação passada
    public void setCurrentSprite(String animationTitle, int frameIndex) {

        this.setAnimation(animationTitle); // seta a animação para a solicitada

        if (frameIndex >= 0 && frameIndex < this.currentAnimation.size()) {
            aniTick = frameIndex;
        }
    }

    public int getAniTick() {
        return aniTick;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    // determina a duração do quanto que o sprite atual deve ser mostrado na tela
    public void setFrameDuration(float frameDuration) {
        this.frameDuration = frameDuration;
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
        this.autoUpdateAni = autoUpdateAni;
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

    public void setCurrentAnimationKey(String currentAnimationKey) {
        this.currentAnimationKey = currentAnimationKey;
    }

    public Map<String, List<Sprite>> getAnimations() {
        return animations;
    }

    public void setAnimations(Map<String, List<Sprite>> animations) {
        this.animations = animations;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
