package official.sketchBook.engine.util_related.utils.contact_listeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.engine.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.util_related.enumerators.type.ObjectType;
import official.sketchBook.engine.util_related.utils.general.GameObjectTag;
import official.sketchBook.engine.util_related.utils.registers.ProjectileCollisionRegister;

import static official.sketchBook.engine.util_related.utils.CollisionUtils.*;
import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getFromBodyTag;

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

        GameObjectTag tagA = getFromBodyTag(a);
        GameObjectTag tagB = getFromBodyTag(b);

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

        controller.lastContactBeginData.buff(
            getCollisionDirection(projectile, contact),
            estimateProjectileContactPoint(projectile),
            other,
            contact,
            projectile.getBody().getAngle()
        );
        controller.colliding = true;

        ProjectileCollisionRegister.registerCollision(controller);

    }

    private void handleEnd(Projectile projectile, GameObjectTag other, Contact contact) {
        if (!projectile.isActive()) return;

        ProjectileControllerComponent controller = projectile.getControllerComponent();

        controller.lastContactBeginData.buff(
            getCollisionDirection(projectile, contact),
            estimateProjectileContactPoint(projectile),
            other,
            contact,
            projectile.getBody().getAngle()
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
