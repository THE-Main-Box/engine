package official.sketchBook.engine.util_related.utils.general;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class HelpMethods {

    private static final Vector2 tmpRecoil = new Vector2(); // buffer estático para evitar criação a cada chamada
    private static final Vector2 tmpSpeed = new Vector2(); // buffer estático para evitar criação a cada chamada


    /**
     * Aplica recuo como velocidade, limitado para não ultrapassar a magnitude desejada.
     * Mantém a pipeline atual.
     *
     * @param body           Corpo que receberá o recuo
     * @param direction      Direção do recuo (vetor qualquer)
     * @param maxRecoilSpeed Velocidade máxima de recuo a aplicar
     */
    public static void applyRecoil(Body body, Vector2 direction, float maxRecoilSpeed) {
        if (body == null || direction == null || direction.isZero()) return;

        // Cria vetor de recuo desejado já normalizado e invertido
        tmpRecoil.set(direction);
        float dirLen2 = tmpRecoil.len2();
        if (dirLen2 == 0f) return;
        tmpRecoil.scl(-maxRecoilSpeed / (float)Math.sqrt(dirLen2));

        // Velocidade atual
        tmpSpeed.set(body.getLinearVelocity());

        // Soma vetorial direta
        tmpSpeed.add(tmpRecoil);

        // Limita magnitude total sem criar vetor extra
        float speedLen2 = tmpSpeed.len2();
        float maxRecoilSpeed2 = maxRecoilSpeed * maxRecoilSpeed;
        if (speedLen2 > maxRecoilSpeed2) {
            float factor = maxRecoilSpeed / (float)Math.sqrt(speedLen2);
            tmpSpeed.scl(factor);
        }

        // Aplica direto mantendo pipeline
        body.setLinearVelocity(tmpSpeed);
    }





    /// Obtém a tag presente em uma body dentro de uma fixture
    public static GameObjectTag getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof GameObjectTag tag)) return null;
        return tag;
    }
}
