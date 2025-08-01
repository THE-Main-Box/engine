package official.sketchBook.gameObject_related.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.toUse_component.entity.PlayerAnimationManagerComponent;
import official.sketchBook.components_related.toUse_component.entity.PlayerControllerComponent;
import official.sketchBook.components_related.toUse_component.object.JumpComponent;
import official.sketchBook.gameObject_related.base_model.ArmedEntity;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.room_related.model.PlayableRoom;
import official.sketchBook.util_related.enumerators.types.FactionTypes;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.paths.EntitiesSpritePath;
import official.sketchBook.util_related.info.values.GameObjectTag;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.weapon_related.Shotgun;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.util_related.enumerators.layers.CollisionLayers.*;
import static official.sketchBook.util_related.info.values.AnimationTitles.Entity.*;

public class Player extends ArmedEntity {

    private PlayerAnimationManagerComponent animationController;
    private PlayerControllerComponent controllerComponent;
    private JumpComponent jComponent;

    public Player(float x, float y, float width, float height, boolean facingForward, World world, PlayableRoom room) {
        super(x, y, width, height, facingForward, world);

        this.ownerRoom = room;

        this.initSpriteSheet();
        this.initAnimations();
        this.initProjectileUsage();

        this.xAP = width / 2;
        this.yAP = width / 2;

        initComponents();

        this.faction = FactionTypes.ALLY;
    }

    private void initComponents() {
        ObjectAnimationPlayer aniPlayer = this.objectAnimationPlayerList.get(0);

        animationController = new PlayerAnimationManagerComponent(this);
        addComponent(animationController);

        controllerComponent = new PlayerControllerComponent(this);
        addComponent(controllerComponent);

        jComponent = new JumpComponent(
            this,
            35,
            100,
            0.1f,
            0.2f,
            aniPlayer.getTotalAnimationTime(aniPlayer.getAnimationByKey(afterFall)),
            true
        );
        addComponent(jComponent);
    }

    private void initProjectileUsage() {
        EmitterRegister.register(new Emitter(this));
        this.weapon = new Shotgun(this, this.weaponAnchorPoint);
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
                facingForward,
                false,
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
        this.defDens = 0.1f;
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
    protected void applySpeedOnBody() {
        if (physicsC == null || moveC == null) return;
        if (jComponent.isEntityLanded() && !moveC.isAcceleratingX()) {

            float reducedSpeedX = moveC.getxSpeed() * 0.7f; // exemplo de redução
            physicsC.applyImpulseForSpeed(
                reducedSpeedX,
                moveC.getySpeed(),
                moveC.getxMaxSpeed(),
                moveC.getyMaxSpeed()
            );

        } else {

            physicsC.applyImpulseForSpeed(
                moveC.getxSpeed(),
                moveC.getySpeed(),
                moveC.getxMaxSpeed(),
                moveC.getyMaxSpeed()
            );

        }

    }

    @Override
    public void render(SpriteBatch batch) {
        if (weapon != null) {
            weapon.render(batch);
        }
        super.render(batch);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (weapon != null) {
            weapon.dispose();
        }
    }

    public void rechargeWeapon() {
        if (weapon == null) return;

        if (hasRangeWeapon()) {
            getRangeWeapon().recharge();
        }

    }

    //verifica se dá para pular
    public boolean canJump() {
        return jComponent.isCoyoteJumpAvailable() || jComponent.isOnGround();
    }

    public PlayerControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public JumpComponent getjComponent() {
        return jComponent;
    }
}
