package official.sketchBook.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.FIXED_TIMESTAMP;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public class StickToSurfaceBehavior implements IEnterCollisionBehavior {

    private static final Vector2 tmpFinalPos = new Vector2();

    @Override
    public void onCollisionEnter(ProjectileControllerComponent controller, Contact contact, Object target) {
        if (target instanceof Entity && !controller.isApplyLockLogicToEntities()) return;
        tryStickToSurface(controller);
    }

    private static void tryStickToSurface(ProjectileControllerComponent controller) {
        Direction dir = controller.lastContactBeginData.getLastDirection();

        boolean shouldStick =
            controller.isStickOnCollision() ||
                (dir.isUp() && controller.isStickToCeiling()) ||
                (dir.isDown() && controller.isStickToGround()) ||
                (dir.isLeft() && controller.isStickToLeftWall()) ||
                (dir.isRight() && controller.isStickToRightWall());

        if (shouldStick) stick(controller, dir);
    }

    private static void stick(ProjectileControllerComponent controller, Direction dir) {
        Body body = controller.getProjectile().getBody();

        if (!controller.isSensorProjectile()) {
            controller.setSensorFixtureProperty(true);
        }

        body.setTransform(controller.lastContactBeginData.getObjectCollisionPos(), body.getAngle());
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setGravityScale(0);

        controller.setStuckToWall(true);
    }

    public static void resetProjectileState(ProjectileControllerComponent controller) {
        Body body = controller.getProjectile().getBody();

        if (!controller.isSensorProjectile()) {
            controller.setSensorFixtureProperty(false);
        }

        body.setGravityScale(controller.isAffectedByGravity() ? 1f : 0f);
        controller.setStuckToWall(false);
    }
}
