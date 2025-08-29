package official.sketchBook.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.animation_related.ObjectAnimationPlayer;
import official.sketchBook.engine.animation_related.Sprite;
import official.sketchBook.engine.animation_related.SpriteSheetDataHandler;
import official.sketchBook.engine.components_related.integration_interfaces.GroundInteractableII;
import official.sketchBook.engine.components_related.integration_interfaces.JumpCapableII;
import official.sketchBook.engine.components_related.toUse_component.entity.PlayerAnimationManagerComponent;
import official.sketchBook.engine.components_related.toUse_component.entity.PlayerControllerComponent;
import official.sketchBook.engine.components_related.toUse_component.object.JumpComponent;
import official.sketchBook.engine.gameObject_related.base_model.RangeWeaponWieldingEntity;
import official.sketchBook.engine.projectileRelated.emitters.Emitter;
import official.sketchBook.game.util_related.enumerators.types.FactionTypes;
import official.sketchBook.engine.util_related.enumerators.type.ObjectType;
import official.sketchBook.engine.util_related.utils.body.BodyCreatorHelper;
import official.sketchBook.game.util_related.info.paths.EntitiesSpritePath;
import official.sketchBook.engine.util_related.utils.general.GameObjectTag;
import official.sketchBook.engine.util_related.utils.registers.EmitterRegister;
import official.sketchBook.game.weapon_related.model.Shotgun;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.engine.util_related.enumerators.layers.CollisionLayers.*;
import static official.sketchBook.game.util_related.info.values.AnimationTitles.Entity.*;

public class Player extends RangeWeaponWieldingEntity implements JumpCapableII, GroundInteractableII {
    private JumpComponent jComponent;

    public Player(float x, float y, float width, float height, boolean xAxisInverted, boolean yAxisInverted, World world) {
        super(x, y, width, height, xAxisInverted, yAxisInverted, world);

        this.initSpriteSheet();
        this.initAnimations();
        this.initComponents();

        this.weaponWC.setRxAP(width / 2);
        this.weaponWC.setRyAP(height / 2);


        this.faction = FactionTypes.ALLY;
    }

    public void updateRayCast() {
        updateOnGroundValue();
    }

    private void initComponents() {
        ObjectAnimationPlayer aniPlayer = this.objectAnimationPlayerList.get(0);

        addComponent(new PlayerAnimationManagerComponent(this));
        addComponent(new PlayerControllerComponent(this));

        jComponent = new JumpComponent(
            this,
            40,
            100,
            0.1f,
            0.2f,
            aniPlayer.getTotalAnimationTime(aniPlayer.getAnimationByKey(afterFall)),
            1f,
            1f,
            false
        );
        addComponent(jComponent);


    }

    public void onEnterNewRoom() {
        initProjectileUsage();
    }

    private void initProjectileUsage() {
        EmitterRegister.register(new Emitter(this));
        this.weaponWC.setWeapon(new Shotgun(this, this.weaponWC.getWeaponAnchorPoint()));
    }

    private void initSpriteSheet() {
        this.spriteSheetDatahandlerList.add(
            new SpriteSheetDataHandler(
                this.x,
                this.y,
                8,
                0,
                5,
                4,
                xAxisInverted,
                yAxisInverted,
                new Texture(EntitiesSpritePath.duck_path)
            )
        );
    }

    private void initAnimations() {
        this.objectAnimationPlayerList.add(new ObjectAnimationPlayer());

        this.objectAnimationPlayerList.get(0).addAnimation(idle, Arrays.asList(
            new Sprite(0, 0, 0.15f),
            new Sprite(1, 0, 0.15f),
            new Sprite(2, 0, 0.15f),
            new Sprite(3, 0, 0.15f)
        ));

        this.objectAnimationPlayerList.get(0).addAnimation(run, Arrays.asList(
            new Sprite(4, 0, 0.075f),
            new Sprite(0, 1, 0.075f),
            new Sprite(1, 1, 0.075f),
            new Sprite(2, 1, 0.075f),
            new Sprite(3, 1, 0.075f),
            new Sprite(4, 1, 0.075f),
            new Sprite(0, 2, 0.075f),
            new Sprite(1, 2, 0.075f)
        ));

        this.objectAnimationPlayerList.get(0).addAnimation(jump, Arrays.asList(
            new Sprite(2, 2),
            new Sprite(3, 2)
        ));

        this.objectAnimationPlayerList.get(0).addAnimation(fall, List.of(
            new Sprite(4, 2)
        ));

        this.objectAnimationPlayerList.get(0).addAnimation(afterFall, Arrays.asList(
            new Sprite(0, 3, 0.1f),
            new Sprite(1, 3, 0.1f)
        ));

        objectAnimationPlayerList.get(0).playAnimation(idle);
    }

    @Override
    protected void setBodyDefValues() {
        this.defDens = 0.5f;
        this.defFric = 1.5f;
        this.defRest = 0;

        this.setCategoryBit(ALLY_ENTITY.bit());
        this.setMaskBit((short) (SENSOR.bit() | ENVIRONMENT.bit()));
    }

    @Override
    protected void createBody() {

        this.body = BodyCreatorHelper.createCapsule(
            world,
            new Vector2(x, y),
            width,
            height,
            BodyDef.BodyType.DynamicBody,
            defDens,
            defFric,
            defRest,
            categoryBit,
            maskBit
        );

        this.body.setUserData(new GameObjectTag(ObjectType.ENTITY, this));
        this.body.setFixedRotation(true);
        this.body.setBullet(true);
    }

    @Override
    public void update(float deltaTime) {
        this.updateComponents(deltaTime);
        super.update(deltaTime);

        updateAnimationPlayer(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        this.weaponWC.render(batch);
        super.render(batch);
    }

    //verifica se dá para pular
    public boolean canJump() {
        return jComponent.isCoyoteJumpAvailable() || jComponent.isOnGround();
    }

    public JumpComponent getJumpC() {
        return jComponent;
    }
}
