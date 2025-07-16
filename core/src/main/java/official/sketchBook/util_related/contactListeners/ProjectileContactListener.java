package official.sketchBook.util_related.contactListeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.info.values.GameObjectTag;
import official.sketchBook.util_related.registers.ProjectileCollisionRegister;

import static official.sketchBook.util_related.helpers.HelpMethods.*;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.FIXED_TIMESTAMP;

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

        controller.lastContactBeginData.buff(
            getCollisionDirection(projectile, contact),
            estimateContactPoint(projectile, FIXED_TIMESTAMP),
            other,
            contact
        );
        controller.colliding = true;

        ProjectileCollisionRegister.registerCollision(controller);

    }

    private void handleEnd(Projectile projectile, GameObjectTag other, Contact contact) {
        if (!projectile.isActive()) return;

        ProjectileControllerComponent controller = projectile.getControllerComponent();

        controller.lastContactBeginData.buff(
            getCollisionDirection(projectile, contact),
            estimateContactPoint(projectile, FIXED_TIMESTAMP),
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
