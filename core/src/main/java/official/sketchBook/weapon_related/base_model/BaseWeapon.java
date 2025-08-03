package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.gameObject_related.base_model.ArmedEntity;
import official.sketchBook.util_related.util.entity.AnchorPoint;

public abstract class BaseWeapon<T extends BaseWeapon<T>> {

    protected Class<T> weaponClass;

    /// Nome da arma
    protected String name;
    /// Descrição da arma
    protected String description;
    /// Dono da arma
    protected ArmedEntity owner;

    /// Gerenciador de dados da sprite-sheet
    protected SpriteSheetDataHandler spriteDataHandler;
    /// Gerenciador de animações
    protected ObjectAnimationPlayer aniPlayer;

    /// Posições da arma
    protected float x, y;
    /// Offset da posição a ser aplicada na renderização
    protected float xDrawOffset, yDrawOffset;

    protected AnchorPoint point;

    public BaseWeapon(Class<T> weaponClass, ArmedEntity owner, AnchorPoint point) {
        this.weaponClass = weaponClass;
        this.owner = owner;
        this.point = point;
        updatePosValues();
    }

    /// Habilidade primária
    public abstract void use();

    /// Habilidade secundária
    public abstract void secondaryUse();

    protected void updateRenderVariables(){
        updatePosValues();
        updateAnimations();
    }
    private void updatePosValues() {
        this.x = point.getX();
        this.y = point.getY();
    }

    public final void render(SpriteBatch batch) {
        if (spriteDataHandler == null || aniPlayer == null || owner == null) return;

        updateRenderVariables();
        spriteDataHandler.updatePosition(x, y);
        spriteDataHandler.setDrawOffSetX(xDrawOffset);
        spriteDataHandler.setDrawOffSetY(yDrawOffset);
        spriteDataHandler.setFacingForward(owner.isFacingForward());
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
    public final void updateAniPlayer(float deltaTime) {
        if (aniPlayer != null) {
            aniPlayer.update(deltaTime);
        }
    }

    protected final void setRelativeOffset(float xOff, float yOff){
        this.xDrawOffset = (spriteDataHandler.getCanvasWidth() / 2f) + xOff;
        this.yDrawOffset = (spriteDataHandler.getCanvasHeight() / 2f) + yOff;
    }

    public static <U extends BaseWeapon<U>> U cast(BaseWeapon<?> weapon, Class<U> cls) {
        if (!cls.isAssignableFrom(weapon.getWeaponClass())) {
            throw new ClassCastException("Weapon cannot be cast to " + cls.getSimpleName());
        }
        return cls.cast(weapon);
    }

    public Class<T> getWeaponClass() {
        return weaponClass;
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
