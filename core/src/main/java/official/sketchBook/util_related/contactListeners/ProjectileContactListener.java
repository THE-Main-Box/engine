package official.sketchBook.util_related.contactListeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.info.values.FixtureType;
import official.sketchBook.util_related.registers.ProjectileCollisionRegister;

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

        FixtureType tagA = getTag(a);
        FixtureType tagB = getTag(b);

        if (tagA == null || tagB == null) return;

        if (tagA.type == ObjectType.PROJECTILE && tagA.owner instanceof Projectile projectile) {
            if (begin) handleBegin(projectile, tagB, contact);
            else handleEnd(projectile, tagB, contact);
        }

        if (tagB.type == ObjectType.PROJECTILE && tagB.owner instanceof Projectile projectile) {
            if (begin) handleBegin(projectile, tagA, contact);
            else handleEnd(projectile, tagA, contact);
        }
    }


    private void handleBegin(Projectile projectile, FixtureType other, Contact contact) {
        if (!projectile.isActive()) return;
        ProjectileControllerComponent controller = projectile.getControllerComponent();

        controller.lastCollisionWith = other;
        controller.lastCollisionContact = contact;
        controller.lastCollisionDirection = controller.getCollisionDirection(contact);
        controller.colliding = true;

        ProjectileCollisionRegister.registerCollision(controller);

    }

    private void handleEnd(Projectile projectile, FixtureType other, Contact contact) {
        if (!projectile.isActive()) return;

        ProjectileControllerComponent controller = projectile.getControllerComponent();

        controller.lastExitCollisionWith = other;
        controller.lastExitCollisionContact = contact;
        controller.lastExitCollisionDirection = controller.getCollisionDirection(contact);
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

    private FixtureType getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof FixtureType tag)) return null;
        return tag;
    }
}
