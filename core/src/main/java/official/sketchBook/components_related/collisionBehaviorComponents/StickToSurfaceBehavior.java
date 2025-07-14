package official.sketchBook.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.gameObject_related.base_model.GameObject;
import official.sketchBook.util_related.enumerators.directions.Direction;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public class StickToSurfaceBehavior implements IEnterCollisionBehavior {

    @Override
    public void onCollisionEnter(ProjectileControllerComponent controller, Contact contact, Object target) {
        if (target instanceof Entity && !controller.isApplyLockLogicToEntities()) return;
        handleCollision(controller, target);
    }

    private static void handleCollision(ProjectileControllerComponent controller, Object target) {
        Direction dir = controller.lastContactBeginData.getLastDirection();

        if (controller.isStickOnCollision() ||
            (dir.isUp() && controller.isStickToCeiling()) ||
            (dir.isDown() && controller.isStickToGround()) ||
            ((dir.isLeft() || dir.isRight()) && controller.isStickToWall())) {

            stick(controller, dir, target);
        }
    }

    private static void stick(ProjectileControllerComponent controller, Direction dir, Object target) {
        Body body = controller.getProjectile().getBody();

        if (!controller.isSensorProjectile()) {
            body.getFixtureList().forEach(f -> f.setSensor(true));
        }

        if (target instanceof GameObject go) {
            float radius = controller.getProjectile().getRadius();

            float finalX = go.getX();
            float finalY = go.getY();

            if (dir.isRight()) {
                finalX += go.getWidth() + radius;
            } else if (dir.isLeft()) {
                finalX -= radius;
            }

            if (dir.isDown()) {
                finalY += go.getHeight() - radius;
            } else if (dir.isUp()) {
                finalY -= radius;
            }

            body.setTransform(finalX / PPM, finalY / PPM, 0);
        }

        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setAwake(false);
    }
}
