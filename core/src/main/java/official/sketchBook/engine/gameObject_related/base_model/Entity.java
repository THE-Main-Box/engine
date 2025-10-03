package official.sketchBook.engine.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.util_related.utils.RayCastUtils;


public abstract class Entity extends MovablePhysicalGameObject {

    protected boolean onGround;
    protected RayCastUtils rayCastUtils;

    public Entity(float x, float y, float width, float height, boolean xAxisInverted, boolean yAxisInverted, World world) {
        super(x, y, width, height, xAxisInverted, yAxisInverted, world);

        this.rayCastUtils = new RayCastUtils(world);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (rayCastUtils != null) {
            rayCastUtils.dispose();
        }
    }

    public RayCastUtils getRayCastHelper() {
        return rayCastUtils;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isMoving() {
        return onGround && moveC.isMovingX();
    }

    public boolean isIdle() {
        return onGround && !moveC.isMovingX();
    }

}
