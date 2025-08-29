package official.sketchBook.game.util_related.util.contactListeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.engine.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.util_related.enumerators.type.ObjectType;
import official.sketchBook.engine.util_related.utils.RayCastUtils;
import official.sketchBook.engine.util_related.utils.general.GameObjectTag;
import official.sketchBook.engine.util_related.utils.registers.ProjectileCollisionRegister;

import static official.sketchBook.engine.util_related.utils.CollisionUtils.estimateProjectileContactPointWithRayCast;
import static official.sketchBook.engine.util_related.utils.CollisionUtils.getCollisionDirection;
import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getTag;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.FIXED_TIMESTAMP;

public class ProjectileContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        processContact(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        processContact(contact, false);
    }

    private void processContact(Contact contact, boolean begin) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        GameObjectTag tagA = getTag(a);
        GameObjectTag tagB = getTag(b);

        if (tagA == null || tagB == null) return;

        if (tagA.type() == ObjectType.PROJECTILE && tagA.owner() instanceof Projectile projectile) {
            if (begin) handleBegin(projectile, tagB, contact);
            else handleEnd(projectile, tagB, contact);
        }

        if (tagB.type() == ObjectType.PROJECTILE && tagB.owner() instanceof Projectile projectile) {
            if (begin) handleBegin(projectile, tagA, contact);
            else handleEnd(projectile, tagA, contact);
        }
    }


    private void handleBegin(Projectile projectile, GameObjectTag other, Contact contact) {
        if (!projectile.isActive()) return;
        ProjectileControllerComponent controller = projectile.getControllerComponent();
        RayCastUtils rayCastUtils = controller.getRayCastHelper();

        controller.lastContactBeginData.buff(
            getCollisionDirection(projectile, contact),
            estimateProjectileContactPointWithRayCast(projectile, FIXED_TIMESTAMP, rayCastUtils),
            other,
            contact
        );
        controller.colliding = true;

        ProjectileCollisionRegister.registerCollision(controller);

    }

    private void handleEnd(Projectile projectile, GameObjectTag other, Contact contact) {
        if (!projectile.isActive()) return;

        ProjectileControllerComponent controller = projectile.getControllerComponent();
        RayCastUtils rayCastUtils = controller.getRayCastHelper();

        controller.lastContactBeginData.buff(
            getCollisionDirection(projectile, contact),
            estimateProjectileContactPointWithRayCast(projectile, FIXED_TIMESTAMP, rayCastUtils),
            other,
            contact
        );

        controller.colliding = false;

        if (controller.isManageExitCollision()) {
            ProjectileCollisionRegister.registerCollisionExitCheck(controller);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
    }

}
