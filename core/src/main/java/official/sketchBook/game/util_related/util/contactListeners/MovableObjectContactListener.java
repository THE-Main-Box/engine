package official.sketchBook.game.util_related.util.contactListeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.engine.components_related.integration_interfaces.MovementCapableII;
import official.sketchBook.engine.util_related.utils.general.ContactActions;

import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getTag;

public class MovableObjectContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        handle(contact);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    ///TODO: verificar antes se devemos aplicar o efeito de block ou não
    private void handle(Contact contact) {
        if (contact == null) return;

        // Tentativa segura de obter MovementCapableII via GameObjectTag/HelpMethods.getTag
        MovementCapableII movable = detectMovableObject(contact);
        // Preserva comportamento original de aplicar fricção:
        ContactActions.handleBlockedMovement(contact, movable);
    }

    private MovementCapableII detectMovableObject(Contact contact) {
        if (contact == null) return null;

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa == null || fb == null) return null;

        // usa helper getTag, que você já tem (retorna GameObjectTag)
        var tagA = getTag(fa);
        var tagB = getTag(fb);

        if (tagA != null && tagA.owner() instanceof MovementCapableII) return (MovementCapableII) tagA.owner();
        if (tagB != null && tagB.owner() instanceof MovementCapableII) return (MovementCapableII) tagB.owner();

        return null;
    }
}
