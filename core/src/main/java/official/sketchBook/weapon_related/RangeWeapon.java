package official.sketchBook.weapon_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.registers.EmitterRegister;

public abstract class RangeWeapon {

    protected String name;
    protected String description;

    protected Class<? extends Projectile> projectileType;
    protected Emitter projectileEmitter;
    protected Entity owner;

    protected SpriteSheetDataHandler spriteDataHandler;
    protected ObjectAnimationPlayer aniPlayer;

    protected float x, y, xOffset, yOffset;

    protected boolean recharging;

    protected TimerComponent rechargingTimeLimit;

    public RangeWeapon(Entity owner) {
        this.owner = owner;
        this.projectileEmitter = EmitterRegister.getEmitter(owner);

        if (this.projectileEmitter == null) {
            throw new IllegalArgumentException("Entity must have an Emitter registered to use Shotgun");
        }

        this.initSpriteSheet();
        this.initAnimations();

        this.updatePosValues();
    }

    protected void updatePosValues() {
        this.x = (owner.getX() + owner.getWidth() / 2f);
        this.y = (owner.getY() + owner.getHeight() / 2f);
    }

    protected void configProjectileTypeOnEmitter(Class<? extends Projectile> type) {
        this.projectileType = type;
        this.projectileEmitter.configure(type);
    }

    public void render(SpriteBatch batch) {
        if (spriteDataHandler == null || aniPlayer == null || owner == null) return;

        updatePosValues();
        spriteDataHandler.setDrawOffSetX(xOffset);
        spriteDataHandler.setDrawOffSetY(yOffset);
        spriteDataHandler.updatePosition(x, y);
        spriteDataHandler.setFacingFoward(owner.isFacingForward());
        spriteDataHandler.renderSprite(batch, aniPlayer.getCurrentSprite());
    }

    public abstract void update(float deltaTime);

    public abstract void updateAnimations();

    public void updateAniPlayer(float deltaTime) {
        if (aniPlayer != null) {
            aniPlayer.update(deltaTime);
        }
    }

    public abstract void recharge();

    /// Inicia as animações da arma
    protected abstract void initAnimations();

    /// Inicia a spriteSheet e os dados relevantes
    protected abstract void initSpriteSheet();

    // Ponto de disparo com base na direção
    public final Vector2 getProjectileSpawnPosition(Direction direction) {
        Vector2 base = new Vector2(
            (owner.getX() + owner.getWidth() / 2f),
            (owner.getY() + owner.getHeight() / 2f)
        );

        return base.add(getOffsetForDirection(direction));
    }

    // obtém offset da direção
    protected Vector2 getOffsetForDirection(Direction direction) {
        if (direction.isRight()) return getRightOffset();
        if (direction.isLeft()) return getLeftOffset();
        if (direction.isDown()) return getDownOffset();
        if (direction.isUp()) return getUpOffset();

        return new Vector2(); // STILL ou default
    }

    public boolean isRecharging() {
        return recharging;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
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

    // Métodos customizáveis por subclasses
    protected Vector2 getRightOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getLeftOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getDownOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getUpOffset() {
        return new Vector2(0, 0);
    }

    public void dispose() {
        if (spriteDataHandler != null) {
            spriteDataHandler.dispose();
        }
    }
}
