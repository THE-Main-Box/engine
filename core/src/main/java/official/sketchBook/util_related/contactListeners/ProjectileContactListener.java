package official.sketchBook.util_related.contactListeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.ContactActions;
import official.sketchBook.util_related.info.util.values.FixtureType;

public class ProjectileContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        handleContact(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        handleContact(contact, false);
    }

    private void handleContact(Contact contact, boolean isBegin) {
        Fixture[] fixtures = extractFixtures(contact);
        if (fixtures == null) return;

        Fixture projectileFix = fixtures[0];
        Fixture otherFix = fixtures[1];

        FixtureType otherTag = getTag(otherFix);
        if (otherTag == null) return;

        Projectile projectile = getProjectile(projectileFix);
        if (projectile == null || !projectile.isActive()) return;

        Direction direction = ContactActions.getCollisionType(contact);
        updateCollisionFlags(projectile, direction, isBegin);

        if (isBegin)
            handleBeginCollision(projectile, otherTag, contact);
        else
            handleEndCollision(projectile, otherTag, contact);
    }

    /**
     * Retorna o projétil associado a uma fixture, ou null se não for um.
     */
    private Projectile getProjectile(Fixture fixture) {
        FixtureType tag = getTag(fixture);
        return tag != null && tag.owner instanceof Projectile ? (Projectile) tag.owner : null;
    }

    /**
     * Atualiza flags de colisão de acordo com a direção e o tipo de contato.
     */
    private void updateCollisionFlags(Projectile p, Direction d, boolean begin) {
        var c = p.getControllerComponent();
        if (c == null) return;

        if (begin) {
            if (d.isDown()) c.addDownContact();
            if (d.isUp()) c.addUpContact();
            if (d.isLeft()) c.addLeftContact();
            if (d.isRight()) c.addRightContact();
        } else {
            if (d.isDown()) c.removeDownContact();
            if (d.isUp()) c.removeUpContact();
            if (d.isLeft()) c.removeLeftContact();
            if (d.isRight()) c.removeRightContact();
        }
    }

    /**
     * Retorna as fixtures: [projétil, outro], ou null se nenhum for projétil.
     */
    private Fixture[] extractFixtures(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        boolean aIsProj = isProjectile(a);
        boolean bIsProj = isProjectile(b);

        if (!aIsProj && !bIsProj) return null;

        return new Fixture[]{aIsProj ? a : b, aIsProj ? b : a};
    }

    /**
     * Trata início da colisão com base no tipo da outra fixture.
     */
    private void handleBeginCollision(Projectile p, FixtureType other, Contact contact) {
        var ctrl = p.getControllerComponent();
        if (ctrl == null) return;

        switch (other.type) {
            case ENVIRONMENT -> ctrl.onHitEnvironment(other.owner, contact);
            case ENTITY -> ctrl.onHitEntity(other.owner, contact);
            case PROJECTILE -> handleProjectileCollision(ctrl, other, contact);
        }
    }

    private void handleProjectileCollision(ProjectileControllerComponent ctrl, FixtureType other, Contact contact) {
        if (!(other.owner instanceof Projectile otherProj)) return;

        var otherCtrl = otherProj.getControllerComponent();
        if (otherCtrl == null) return;

        // Primeiro, se um dos dois não quer colisão com projéteis, ignora
        if (!ctrl.isColideWithOtherProjectiles() || !otherCtrl.isColideWithOtherProjectiles()) return;

        boolean sameType = ctrl.getProjectile().getClass() == otherProj.getClass();

        // Se forem do mesmo tipo e alguém não quiser colidir com projéteis do mesmo tipo
        if (sameType && (!ctrl.isColideWithSameTypeProjectiles() || !otherCtrl.isColideWithSameTypeProjectiles())) return;

        // Se passou por tudo, pode processar colisão
        ctrl.onHitProjectile(other.owner, contact);
    }


    /**
     * Trata fim da colisão com base no tipo da outra fixture.
     */
    private void handleEndCollision(Projectile p, FixtureType other, Contact contact) {
        var ctrl = p.getControllerComponent();
        if (ctrl == null) return;

        switch (other.type) {
            case ENVIRONMENT -> ctrl.onLeaveEnvironment(other.owner, contact);
            case ENTITY -> ctrl.onLeaveEntity(other.owner, contact);
            case PROJECTILE -> ctrl.onLeaveProjectile(other.owner, contact);
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        FixtureType tagA = getTag(a);
        FixtureType tagB = getTag(b);
        if (tagA == null || tagB == null) return;

        // Verifica se ambos são projéteis
        if (tagA.type == FixtType.PROJECTILE && tagB.type == FixtType.PROJECTILE) {
            Projectile pA = (Projectile) tagA.owner;
            Projectile pB = (Projectile) tagB.owner;

            var cA = pA.getControllerComponent();
            var cB = pB.getControllerComponent();
            if (cA == null || cB == null) return;

            // Se qualquer um não quiser colidir com projéteis, cancela
            if (!cA.isColideWithOtherProjectiles() || !cB.isColideWithOtherProjectiles()) {
                contact.setEnabled(false);
                return;
            }

            // Se forem da mesma classe e qualquer um não quiser colidir com o mesmo tipo, cancela
            if (pA.getClass() == pB.getClass()) {
                if (!cA.isColideWithSameTypeProjectiles() || !cB.isColideWithSameTypeProjectiles()) {
                    contact.setEnabled(false);
                }
            }
        }
    }


    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    private boolean isProjectile(Fixture fixture) {
        FixtureType tag = getTag(fixture);
        return tag != null && tag.type == FixtType.PROJECTILE;
    }

    private FixtureType getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof FixtureType)) return null;
        return (FixtureType) fixture.getBody().getUserData();
    }

}
