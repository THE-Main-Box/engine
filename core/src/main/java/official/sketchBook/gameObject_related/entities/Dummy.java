package official.sketchBook.gameObject_related.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.components_related.toUse_component.object.DamageReceiveComponent;
import official.sketchBook.gameObject_related.base_model.Enemy;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.paths.EntitiesSpritePath;
import official.sketchBook.util_related.info.values.GameObjectTag;

import java.util.List;

import static official.sketchBook.util_related.enumerators.layers.CollisionLayers.*;
import static official.sketchBook.util_related.info.values.AnimationTitles.Entity.idle;

public class Dummy extends Enemy implements DamageReceiverII {

    private DamageReceiveComponent damageReceiveC;

    public Dummy(float x, float y, float width, float height, boolean xAxisInverted, boolean yAxisInverted, World world) {
        super(x, y, width, height, xAxisInverted, yAxisInverted, world);

        this.initSpriteSheet();

        this.initComponents();
        this.initAnimations();
    }

    private void initComponents() {
        damageReceiveC = new DamageReceiveComponent(this, 100);
    }

    private void initAnimations() {
        ObjectAnimationPlayer aniPlayer = new ObjectAnimationPlayer();
        this.objectAnimationPlayerList.add(aniPlayer);

        aniPlayer.addAnimation(idle, List.of(new Sprite(0, 0)));

        aniPlayer.playAnimation(idle);
    }

    private void initSpriteSheet() {
        this.spriteSheetDatahandlerList.add(
                new SpriteSheetDataHandler(
                        this.x,
                        this.y,
                        41,
                        17,
                        4,
                        1,
                        this.xAxisInverted,
                        this.yAxisInverted,
                        new Texture(EntitiesSpritePath.dummy_path)
                )
        );
    }

    @Override
    protected void setBodyDefValues() {
        this.defFric = 0;
        this.defDens = 1;
        this.defRest = 0;

        this.categoryBit = ENEMY_ENTITY.bit();
        this.maskBit = ENVIRONMENT.bit();
    }

    @Override
    protected void createBody() {

        this.body = BodyCreatorHelper.createBox(
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

        this.createHutBox();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (this.xAxisInverted) {
            spriteSheetDatahandlerList.get(0).setDrawOffSetX(26);
        } else {
            spriteSheetDatahandlerList.get(0).setDrawOffSetX(41);
        }
        super.render(batch);
    }

    @Override
    public void onEnterNewRoom() {

    }

    @Override
    public void createHutBox() {
        float sensorWidth = width * 0.8f;    // Ajuste conforme necessário
        float sensorHeight = height * 0.9f;

        PolygonShape sensorShape = BodyCreatorHelper.createBoxShape(
                sensorWidth,
                sensorHeight,
                0,
                0
        );
        FixtureDef sensorFixtureDef = BodyCreatorHelper.createFixture(
                sensorShape,
                0,     // densidade irrelevante
                0,     // fricção irrelevante
                0,     // restituição irrelevante
                getHBCategoryBit(), // categoria do sensor
                getHBMaskBit()             // máscara do sensor
        );
        sensorFixtureDef.isSensor = true; // marca como sensor

        body.createFixture(sensorFixtureDef).setUserData("HURTBOX");

        sensorShape.dispose();
    }

    @Override
    public void die() {

    }

    @Override
    public DamageReceiveComponent getDamageReceiveC() {
        return damageReceiveC;
    }

    @Override
    public short getHBMaskBit() {
        return (short) (ALLY_ENTITY.bit() | ALLY_PROJECTILE.bit());
    }

    @Override
    public short getHBCategoryBit() {
        return (short) (SENSOR.bit() | ALLY_ENTITY.bit());
    }
}
