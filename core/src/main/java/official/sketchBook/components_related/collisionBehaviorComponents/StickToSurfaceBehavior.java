package official.sketchBook.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.FIXED_TIMESTAMP;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public class StickToSurfaceBehavior implements IEnterCollisionBehavior {

    // Vetores temporários para evitar alocação (thread-safe se usados localmente apenas)
    private static final Vector2 tmpCorrection = new Vector2();
    private static final Vector2 tmpFinalPos = new Vector2();

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

            stick(controller, dir);
        }
    }

    private static void stick(ProjectileControllerComponent controller, Direction dir) {
        // Evita múltiplas chamadas encadeadas
        var projectile = controller.getProjectile();
        var body = projectile.getBody();
        var radius = projectile.getRadius();

        Vector2 contactPos = controller.lastContactBeginData.getObjectCollisionPos();
        Vector2 velocity = body.getLinearVelocity();

        if (!controller.isSensorProjectile())
            body.setActive(false);

        // Calcula correção com base no tempo fixo
        tmpCorrection.set(velocity).scl(-FIXED_TIMESTAMP);

        // Posição corrigida para retroceder um pouco
        tmpFinalPos.set(contactPos).add(tmpCorrection);

        // Ajusta conforme a direção da colisão
        switch (dir) {
            case LEFT -> tmpFinalPos.x += radius / PPM;
            case RIGHT -> tmpFinalPos.x -= radius / PPM;
            case UP -> tmpFinalPos.y -= radius / PPM;
            case DOWN -> tmpFinalPos.y += radius / PPM;
        }

        // Imobiliza o corpo e garante que a gravidade seja desativada
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setGravityScale(0);

        body.setTransform(tmpFinalPos, body.getAngle());

        // Futuro: considerar usar body.setType(BodyDef.BodyType.KinematicBody)
    }

    public static void resetProjectileState(ProjectileControllerComponent controller){
        Body body = controller.getProjectile().getBody();

        if(!controller.isSensorProjectile()){
            body.setActive(true);
        }
        if(controller.isAffectedByGravity()){
            body.setGravityScale(1);
        }
    }

}
