package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.WorldManifold;

public class ContactActions {

    public void applyDefaultFrictionLogic(Contact contact) {
        if (contact == null) return; // Evita NullPointerException

        WorldManifold worldManifold = contact.getWorldManifold();
        if (worldManifold == null) return; // Precaução extra

        float normalX = worldManifold.getNormal().x;
        float normalY = worldManifold.getNormal().y;

        // Se o normal é quase para os lados (lateral forte), reduzir atrito
        if (Math.abs(normalX) > 0.8f && Math.abs(normalY) < 0.5f) {
            // Batendo mais de lado do que de cima
            contact.setFriction(0f); // Sem atrito nas paredes
        }
        // Se o normal é para cima (chão forte)
        else if (normalY > 0.8f) {
            contact.setFriction(0.8f); // Chão normal, atrito alto para não escorregar
        }
        else {
            // Casos meio inclinados (tipo quinas), colocar um atrito médio
            contact.setFriction(0.4f);
        }
    }


}
