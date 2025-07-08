package official.sketchBook.util_related.contactListeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.info.values.FixtureType;

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

        if (tagA.type == FixtType.PROJECTILE && tagA.owner instanceof Projectile p) {
            if (begin) handleBegin(p, tagB, contact);
            else handleEnd(p, tagB, contact);
        }

        if (tagB.type == FixtType.PROJECTILE && tagB.owner instanceof Projectile p) {
            if (begin) handleBegin(p, tagA, contact);
            else handleEnd(p, tagA, contact);
        }
    }

    private void handleBegin(Projectile projectile, FixtureType other, Contact contact) {
        if (!projectile.isActive()) return;

        //Chama os métodos desejados relativamente ao contato executado após lidar com a direção da colisão em si
        switch (other.type) {
            case ENVIRONMENT -> {
                projectile.getControllerComponent().enviromentCollisionDirection
                    = projectile.getControllerComponent().getCollisionDirection(contact);

                projectile.getControllerComponent().updateAxisStatesByCollision(
                    projectile.getControllerComponent().enviromentCollisionDirection
                );

                projectile.getControllerComponent().onHitEnvironment(other.owner, contact);
            }
            case ENTITY -> {

                projectile.getControllerComponent().entityCollisionDirection
                    = projectile.getControllerComponent().getCollisionDirection(contact);

                projectile.getControllerComponent().updateAxisStatesByCollision(
                    projectile.getControllerComponent().entityCollisionDirection
                );

                projectile.getControllerComponent().onHitEntity((Entity) other.owner, contact);
            }
            case PROJECTILE -> {

                projectile.getControllerComponent().projectileCollisionDirection
                    = projectile.getControllerComponent().getCollisionDirection(contact);

                projectile.getControllerComponent().updateAxisStatesByCollision(
                    projectile.getControllerComponent().projectileCollisionDirection
                );

                projectile.getControllerComponent().onHitProjectile((Projectile) other.owner, contact);
            }
        }

    }

    private void handleEnd(Projectile projectile, FixtureType other, Contact contact) {
        if (!projectile.isActive()) return;

        switch (other.type) {
            case ENVIRONMENT -> {
                projectile.getControllerComponent().onLeaveEnvironment(other.owner, contact);
                projectile.getControllerComponent().enviromentCollisionDirection = Direction.STILL;
            }
            case ENTITY -> {
                projectile.getControllerComponent().onLeaveEntity((Entity) other.owner, contact);
                projectile.getControllerComponent().entityCollisionDirection = Direction.STILL;
            }
            case PROJECTILE -> {
                projectile.getControllerComponent().onLeaveProjectile((Projectile) other.owner, contact);
                projectile.getControllerComponent().projectileCollisionDirection = Direction.STILL;
            }
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
