package official.sketchBook.engine.weapon_related.base_model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.engine.animation_related.ObjectAnimationPlayer;
import official.sketchBook.engine.animation_related.SpriteSheetDataHandler;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.util_related.utils.RayCastHelper;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.point.AnchorPoint;

public abstract class BaseWeapon<T extends BaseWeapon<T>> {

    protected Class<T> weaponClass;

    /// Nome da arma
    protected String name;

    /// Descrição da arma
    protected String description;

    /// Dono da arma
    protected RangeWeaponWielderII owner;

    /// Gerenciador de dados da sprite-sheet
    protected SpriteSheetDataHandler spriteDataHandler;

    /// Gerenciador de animações
    protected ObjectAnimationPlayer aniPlayer;

    /// Posições da arma
    protected float x, y;

    /// Offset da posição a ser aplicada na renderização
    protected float xDrawOffset, yDrawOffset;

    /// Valor que contém o offset que deve ser aplicado na posição da arma relativamente à posição do dono
    protected AnchorPoint point;

    protected RayCastHelper rayCastHelper;

    public BaseWeapon(Class<T> weaponClass, RangeWeaponWielderII owner, AnchorPoint point) {
        this.weaponClass = weaponClass;
        this.owner = owner;
        this.point = point;
        this.rayCastHelper = owner.getRayCastHelper();
        updatePosValues();
    }

    /// Habilidade primária
    public abstract void use();

    /// Habilidade secundária
    public abstract void secondaryUse();

    protected void updateRenderVariables() {
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
        spriteDataHandler.setxAxisInvert(owner.isxAxisInverted());
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

    protected final void setRelativeOffset(float xOff, float yOff) {
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

    public RayCastHelper getRayCastHelper() {
        return rayCastHelper;
    }

    public void dispose() {
        if (spriteDataHandler != null) {
            spriteDataHandler.dispose();
        }
    }
}
