package official.sketchBook.util_related.contactListeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.ContactActions;
import official.sketchBook.util_related.info.util.values.FixtureType;

public class ProjectileContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Verifica se uma das fixtures é um projétil
        boolean aIsProjectile = isProjectile(fixA);
        boolean bIsProjectile = isProjectile(fixB);

        if (!aIsProjectile && !bIsProjectile) return;

        Fixture projectileFix = aIsProjectile ? fixA : fixB; //obtém a fixture do projétil
        Fixture otherFix = aIsProjectile ? fixB : fixA; //obtém a fixture oposta

        FixtureType otherTag = getTag(otherFix);
        if (otherTag == null) return;

        Direction direction = ContactActions.getCollisionType(contact);
        Projectile projectile = (Projectile) getTag(projectileFix).owner;

        updateCollisionFlagBy(projectile, true, direction);

        handleCollision(projectile, otherTag, contact);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        boolean aIsProjectile = isProjectile(fixA);
        boolean bIsProjectile = isProjectile(fixB);

        if (!aIsProjectile && !bIsProjectile) return; // se nenhum projétil for encontrado, nós não prosseguimos

        Fixture projectileFix = aIsProjectile ? fixA : fixB;//obtém a fixture do projétil
        Fixture otherFix = aIsProjectile ? fixB : fixA; //obtém a fixture oposta

        FixtureType otherTag = getTag(otherFix);
        if (otherTag == null) return;

        Projectile projectile = (Projectile) getTag(projectileFix).owner;

        Direction direction = ContactActions.getCollisionType(contact);

        updateCollisionFlagBy(projectile, false, direction);

        handleEndCollision(projectile, otherTag, contact);
    }

    private void handleCollision(Projectile projectile, FixtureType other, Contact contact) {
        if (!projectile.isActive()) return;

        var controller = projectile.getControllerComponent();

        switch (other.type) {
            case ENVIRONMENT:
                controller.onHitEnvironment(other.owner, contact);
                break;
            case ENTITY:
                controller.onHitEntity(other.owner, contact);
                break;
            case PROJECTILE:
                controller.onHitProjectile(other.owner, contact);
                break;
        }
    }

    private void handleEndCollision(Projectile projectile, FixtureType other, Contact contact) {
        if (!projectile.isActive()) return;

        var controller = projectile.getControllerComponent();

        switch (other.type) {
            case ENVIRONMENT:
                controller.onLeaveEnvironment(other.owner, contact);
                break;
            case ENTITY:
                controller.onLeaveEntity(other.owner, contact);
                break;
            case PROJECTILE:
                controller.onLeaveProjectile(other.owner, contact);
                break;
        }
    }


    private void updateCollisionFlagBy(Projectile projectile, boolean flag, Direction direction) {
        if (!projectile.isActive()) return;

        var controller = projectile.getControllerComponent();

        switch (direction) {
            case DOWN:
                if (flag) controller.addGroundContact();
                else controller.removeGroundContact();
                break;
            case DOWN_LEFT, DOWN_RIGHT:
                if (flag) {
                    controller.addGroundContact();
                    controller.addWallContact();
                } else {
                    controller.removeGroundContact();
                    controller.removeWallContact();
                }
                break;
            case UP:
                if (flag) controller.addCeilingContact();
                else controller.removeCeilingContact();
                break;
            case UP_LEFT, UP_RIGHT:
                if (flag) {
                    controller.addCeilingContact();
                    controller.addWallContact();
                } else {
                    controller.removeCeilingContact();
                    controller.removeWallContact();
                }
                break;
            case LEFT, RIGHT:
                if (flag) controller.addWallContact();
                else controller.removeWallContact();
                break;
            default:
                break;
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    private boolean isProjectile(Fixture fixture) {
        if (fixture == null || fixture.getUserData() == null) return false;
        var fixType = getTag(fixture);
        return fixType != null && fixType.type == FixtType.PROJECTILE; // ou use instanceof se tiver classes específicas
    }

    private FixtureType getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getUserData() instanceof FixtureType)) return null;
        return (FixtureType) fixture.getUserData();
    }
}
