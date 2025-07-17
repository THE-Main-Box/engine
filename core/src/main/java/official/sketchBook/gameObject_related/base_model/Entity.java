package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.helpers.RayCastHelper;
import official.sketchBook.util_related.info.values.GameObjectTag;

import static official.sketchBook.util_related.helpers.HelpMethods.getTag;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public abstract class Entity extends MovableGameObject {

    protected boolean onGround;
    protected RayCastHelper rayCastHelper;
    protected GameObjectTag groundTag;

    public Entity(float x, float y, float width, float height, boolean facingForward, World world) {
        super(x, y, width, height, facingForward, world);

        this.rayCastHelper = new RayCastHelper(world);
    }

    public void updateRayCast() {
        updateOnGroundValue();
    }


    protected final void updateOnGroundValue() {
        if (world == null || body == null) return;

        onGround = false;
        Vector2 center = body.getPosition();

        float halfWidth = (width / 2f) / PPM;
        float halfHeight = (height / 2f) / PPM;
        float footY = center.y - halfHeight + 2 / PPM; // Origem no "pé"

        float rayLength = 3f / PPM; // Alcance pequeno pra detectar chão

        float margin = 1f / PPM; // pequeno recuo lateral

        Vector2[] rayStarts = new Vector2[]{
            new Vector2(center.x - halfWidth + margin, footY),
            new Vector2(center.x, footY),
            new Vector2(center.x + halfWidth - margin, footY)
        };

        for (Vector2 start : rayStarts) {
            Vector2 end = new Vector2(start.x, start.y - rayLength);

            rayCastHelper.castRay(start, end, data -> {
                groundTag = getTag(data.fixture());
                if (groundTag != null && groundTag.type() == ObjectType.ENVIRONMENT) {
                    onGround = true;
                }
            });

        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (rayCastHelper != null) {
            rayCastHelper.dispose();
        }
    }

    public RayCastHelper getRayCastHelper() {
        return rayCastHelper;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public abstract boolean canJump();

    public boolean isRunning() {
        return onGround && moveC.isMoving();
    }

    public boolean isIdle() {
        return onGround && !moveC.isMoving();
    }
}
