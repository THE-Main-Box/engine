package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.WorldManifold;

public class ContactActions {

    public void applyDefaultFrictionLogic(Contact contact) {
        if (contact == null) return; // Evita NullPointerException

        WorldManifold worldManifold = contact.getWorldManifold();
        float normalX = worldManifold.getNormal().x;
        float normalY = worldManifold.getNormal().y;

        // Calculando o ângulo da colisão com base na direção do vetor normal
        float angle = (float) Math.atan2(normalY, normalX); // Obtém o ângulo entre o vetor normal e o eixo X

        // Convertendo o ângulo para graus
        float angleInDegrees = (float) Math.toDegrees(angle);

        // Limiar de ângulo e margem de erro
        float lateralThreshold = 45f; // Limiar de ângulo para considerar uma colisão lateral (em graus)
        float marginOfError = 5f; // Margem de erro para evitar variações pequenas

        // Ajustando o limiar com a margem de erro
        if (Math.abs(angleInDegrees) < (lateralThreshold + marginOfError)) {
            // Se o ângulo estiver dentro do limiar, trata como uma colisão lateral e zera a fricção
            contact.setFriction(0);
        }
    }


}
