package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.gameObject_related.util.AnchorPoint;

public abstract class BaseWeapon<T extends BaseWeapon<T>> {

    protected Class<T> weaponClass;

    /// Nome da arma
    protected String name;
    /// Descrição da arma
    protected String description;
    /// Dono da arma
    protected Entity owner;

    /// Gerenciador de dados da sprite-sheet
    protected SpriteSheetDataHandler spriteDataHandler;
    /// Gerenciador de animações
    protected ObjectAnimationPlayer aniPlayer;

    /// Posições da arma
    protected float x, y;
    /// Offset da posição a ser aplicada
    protected float xOffset, yOffset;

    protected AnchorPoint point;

    public BaseWeapon(Class<T> weaponClass, Entity owner, AnchorPoint point) {
        this.weaponClass = weaponClass;
        this.owner = owner;
        this.point = point;
        updatePosValues();
    }

    public Class<T> getWeaponClass() {
        return weaponClass;
    }

    public boolean isWeaponType(Class<?> cls) {
        return weaponClass == cls;
    }

    public T castToSelf() {
        return weaponClass.cast(this); // Seguro: você garantiu que T é a classe correta
    }

    public static <U extends BaseWeapon<U>> U cast(BaseWeapon<?> weapon, Class<U> cls) {
        if (!cls.isAssignableFrom(weapon.getWeaponClass())) {
            throw new ClassCastException("Weapon cannot be cast to " + cls.getSimpleName());
        }
        return cls.cast(weapon);
    }

    protected void updatePosValues() {
        this.x = point.getX();
        this.y = point.getY();
    }

    public void render(SpriteBatch batch) {
        if (spriteDataHandler == null || aniPlayer == null || owner == null) return;

        updatePosValues();
        spriteDataHandler.updatePosition(x, y);

        spriteDataHandler.setDrawOffSetX(xOffset);
        spriteDataHandler.setDrawOffSetY(yOffset);

        spriteDataHandler.setFacingFoward(owner.isFacingForward());
        spriteDataHandler.renderSprite(batch, aniPlayer.getCurrentSprite());
    }

    /// Meio de ‘update’ genérico
    public abstract void update(float deltaTime);

    /// Gestão de animações
    public abstract void updateAnimations();

    /// Inicialização de animações
    protected abstract void initAnimations();

    /// Inicia a spriteSheet e os dados relevantes
    protected abstract void initSpriteSheet();

    /// Atualização do tocador de animações
    public void updateAniPlayer(float deltaTime) {
        if (aniPlayer != null) {
            aniPlayer.update(deltaTime);    
        }
    }

    protected void setRelativeOffset(float xOff, float yOff){
        this.xOffset = (spriteDataHandler.getCanvasWidth() / 2f) + xOff;
        this.yOffset = (spriteDataHandler.getCanvasHeight() / 2f) + yOff;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ObjectAnimationPlayer getAniPlayer() {
        return aniPlayer;
    }

    public SpriteSheetDataHandler getSpriteDataHandler() {
        return spriteDataHandler;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void dispose() {
        if (spriteDataHandler != null) {
            spriteDataHandler.dispose();
        }
    }
}
