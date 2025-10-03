package official.sketchBook.engine.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.engine.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.engine.gameObject_related.base_model.Entity;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;

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

        body.setTransform(controller.lastContactBeginData.getObjectCollisionPos(), controller.lastContactBeginData.angle());
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setGravityScale(0);

        controller.setStuckToWall(true);
        controller.getProjectile().onWallStuck();
    }

    public static void resetProjectileState(ProjectileControllerComponent controller) {
        Body body = controller.getProjectile().getBody();

        if (!controller.isSensorProjectile()) {
            controller.setSensorFixtureProperty(false);
        }

        body.setGravityScale(controller.isAffectedByGravity() ? 1f : 0f);
        controller.setStuckToWall(false);
        controller.getProjectile().onWallUnstuck();
    }
}
