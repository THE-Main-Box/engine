package official.sketchBook.gameObject_related.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.toUse_component.entity.PlayerControllerComponent;
import official.sketchBook.components_related.toUse_component.object.JumpComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.room_related.model.PlayableRoom;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.paths.EntitiesSpritePath;
import official.sketchBook.util_related.info.util.values.AnimationTitles;
import official.sketchBook.util_related.info.util.values.FixtureType;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.weapon_related.RangeWeapon;
import official.sketchBook.weapon_related.Shotgun;

import java.util.Arrays;

public class Player extends Entity {

    private PlayerControllerComponent controllerComponent;
    private JumpComponent jComponent;

    private RangeWeapon weapon;


    public Player(float x, float y, float width, float height, boolean facingForward, World world, PlayableRoom room) {
        super(x, y, width, height, facingForward, world);

        this.ownerRoom = room;

        controllerComponent = new PlayerControllerComponent(this);
        addComponent(controllerComponent);

        jComponent = new JumpComponent(this, 35, 100, 0.1f, false);
        addComponent(jComponent);

        this.initSpriteSheet();
        this.initAnimations();
        this.initProjectileUsage();
    }

    private void initProjectileUsage() {
        EmitterRegister.register(new Emitter(this));
        this.weapon = new Shotgun(this);
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
        this.objectAnimationPlayerList.get(0).addAnimation(AnimationTitles.idle, Arrays.asList(
            new Sprite(0, 0, 0.15f),
            new Sprite(1, 0, 0.15f),
            new Sprite(2, 0, 0.15f),
            new Sprite(3, 0, 0.15f)
        ));
        this.objectAnimationPlayerList.get(0).addAnimation(AnimationTitles.run, Arrays.asList(
            new Sprite(4, 0, 0.075f),
            new Sprite(0, 1, 0.075f),
            new Sprite(1, 1, 0.075f),
            new Sprite(2, 1, 0.075f),
            new Sprite(3, 1, 0.075f),
            new Sprite(4, 1, 0.075f),
            new Sprite(0, 2, 0.075f),
            new Sprite(1, 2, 0.075f)
        ));

        objectAnimationPlayerList.get(0).setAnimation(AnimationTitles.idle);
    }

    @Override
    protected void setBodyDefValues() {
        this.defDens = 0.1f;
        this.defFric = 1f;
        this.defRest = 0;
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
            defRest
        );

        this.body.setUserData(new FixtureType(FixtType.ENTITY, this));
        this.body.setFixedRotation(true);
        this.body.setBullet(true);
    }

    @Override
    public void update(float deltaTime) {
        this.updateComponents(deltaTime);
        super.update(deltaTime);

        updateAnimation();
        updateAnimationPlayer(deltaTime);

        if(weapon != null){
            weapon.update(deltaTime);
        }
    }

    private void updateAnimation() {
        for (ObjectAnimationPlayer animationPlayer : objectAnimationPlayerList) {
            if (moving) {
                animationPlayer.setAnimation(AnimationTitles.run);
            } else if (onGround) {
                animationPlayer.setAnimation(AnimationTitles.idle);
            }
        }
    }

    private void updateAnimationPlayer(float delta) {
        for (ObjectAnimationPlayer animationPlayer : objectAnimationPlayerList) {
            animationPlayer.update(delta);
        }

        if(weapon != null){
            weapon.updateAniPlayer(delta);
        }
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
        if(weapon != null){
            weapon.render(batch);
        }
        super.render(batch);
    }


    public void rechargeWeapon(){
        if(weapon != null){
            weapon.recharge();
        }
    }

    public RangeWeapon getWeapon() {
        return weapon;
    }

    public void setWeapon(RangeWeapon weapon) {
        this.weapon = weapon;
    }

    //verifica se dá para pular
    public boolean canJump() {
        return jComponent.getCoyoteTimer().isRunning() || onGround;
    }

    public PlayerControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public JumpComponent getjComponent() {
        return jComponent;
    }
}
