package official.sketchBook.engine.util_related.utils.general;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class HelpMethods {

    private static final Vector2 tmpRecoil = new Vector2(); // buffer estático para evitar criação a cada chamada
    private static final Vector2 tmpSpeed = new Vector2(); // buffer estático para evitar criação a cada chamada

    /**
     * Aplica uma velocidade de recuo ao corpo, somando-a à velocidade atual,
     * mas apenas se a projeção da velocidade atual na direção do recuo for menor que a velocidade de recuo desejada.
     *
     * @param body           O corpo que receberá o recuo
     * @param direction      Direção do recuo (vetor com magnitude qualquer; será normalizado)
     * @param recoilVelocity Velocidade de recuo desejada (em m/s)
     */
    public static void applyRecoil(Body body, Vector2 direction, float recoilVelocity) {
        if (body == null || direction == null || direction.isZero()) return;

        // Cria o vetor de recuo invertido sem criar novos objetos
        tmpRecoil.set(direction).scl(-recoilVelocity);

        tmpSpeed.set(body.getLinearVelocity());

        float newX = tmpSpeed.x;
        float newY = tmpSpeed.y;

        // --- EIXO X ---
        if (Math.signum(tmpRecoil.x) != Math.signum(tmpSpeed.x)) {
            newX += tmpRecoil.x; // direções opostas — reduz a velocidade
        } else if (Math.abs(tmpSpeed.x) < Math.abs(tmpRecoil.x)) {
            newX = tmpRecoil.x; // mesma direção, abaixo do limite
        }
        // caso contrário, não muda

        // --- EIXO Y ---
        if (Math.signum(tmpRecoil.y) != Math.signum(tmpSpeed.y)) {
            newY += tmpRecoil.y;
        } else if (Math.abs(tmpSpeed.y) < Math.abs(tmpRecoil.y)) {
            newY = tmpRecoil.y;
        }

        // Aplica a nova velocidade diretamente
        body.setLinearVelocity(newX, newY);
    }


    /// Obtém a tag presente em uma body dentro de uma fixture
    public static GameObjectTag getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof GameObjectTag tag)) return null;
        return tag;
    }
}
