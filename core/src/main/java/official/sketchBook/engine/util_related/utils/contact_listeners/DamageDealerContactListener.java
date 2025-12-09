package official.sketchBook.engine.util_related.utils.contact_listeners;

import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageDealerII;
import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageReceiverII;
import official.sketchBook.engine.util_related.pools.PolishDamageDataPool;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.PolishDamageData;
import official.sketchBook.game.gameState_related.models.Playing;

import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getFromBodyTag;

public class DamageDealerContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        handle(contact);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    private void handle(Contact contact) {
        DamageDealerII dealer = detectDamageDealer(contact);
        if (dealer == null || !dealer.isDamageAble()) return; //se não houver um causador de dano envolvido ignoramos

        DamageReceiverII receiver = detectDamageReceiver(contact);
        if (receiver == null) return; //se não houver um receptor pro dano, ignoramos

        PolishDamageDataPool pool = Playing.objectManager.getCurrentRoom().getPolishDamagePool();
        PolishDamageData data = pool.obtain();


        pool.initPDD(
            receiver,
            dealer,
            dealer.getDamageData(),
            data
        );

        System.out.println(
            "objeto: " + dealer.getClass().getSimpleName()
                + " causou dano a: " + receiver.getClass().getSimpleName()
                + "| " + receiver.getDamageReceiveC().getHealth()
            + " | " + data.getDamageData().getAmount() * data.getDamageData().getAmountMod()
        );

        receiver.getDamageReceiveC().damage(data);
    }

    private DamageDealerII detectDamageDealer(Contact contact) {
        if (contact == null) return null;

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa == null || fb == null) return null;

        // usa helper getTag, que você já tem (retorna GameObjectTag)
        var tagA = getFromBodyTag(fa);
        var tagB = getFromBodyTag(fb);

        if (tagA != null && tagA.owner() instanceof DamageDealerII) return (DamageDealerII) tagA.owner();
        if (tagB != null && tagB.owner() instanceof DamageDealerII) return (DamageDealerII) tagB.owner();

        return null;
    }

    private DamageReceiverII detectDamageReceiver(Contact contact) {
        if (contact == null) return null;

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa == null || fb == null) return null;

        // usa helper getTag, que você já tem (retorna GameObjectTag)
        var tagA = getFromBodyTag(fa);
        var tagB = getFromBodyTag(fb);

        if (tagA != null && tagA.owner() instanceof DamageReceiverII) return (DamageReceiverII) tagA.owner();
        if (tagB != null && tagB.owner() instanceof DamageReceiverII) return (DamageReceiverII) tagB.owner();

        return null;
    }
}
