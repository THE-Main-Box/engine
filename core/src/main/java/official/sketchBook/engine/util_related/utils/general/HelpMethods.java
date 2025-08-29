package official.sketchBook.engine.util_related.utils.general;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class HelpMethods {

    /**
     * Aplica uma velocidade de recuo ao corpo, somando-a à velocidade atual,
     * mas apenas se a projeção da velocidade atual na direção do recuo for menor que a velocidade de recuo desejada.
     *
     * @param body             O corpo que receberá o recuo
     * @param direction        Direção do recuo (vetor com magnitude qualquer; será normalizado)
     * @param recoilVelocity   Velocidade de recuo desejada (em m/s)
     */
    public static void applyRecoil(Body body, Vector2 direction, float recoilVelocity) {
        if (body == null || direction == null || direction.isZero()) return;


        // Cria o vetor de recuo invertido: -dir * velocidade
        Vector2 recoil = new Vector2(direction).scl(-recoilVelocity);

        // Velocidade atual do corpo
        Vector2 bodySpeed = body.getLinearVelocity();

        // Resultado final a aplicar
        Vector2 newVelocity = new Vector2(bodySpeed);

        // --- EIXO X ---
        float recoilX = recoil.x;
        float bodyX = bodySpeed.x;

        if (Math.signum(recoilX) != Math.signum(bodyX)) {
            // Direções opostas — reduz a velocidade
            newVelocity.x = bodyX + recoilX;
        } else if (Math.abs(bodyX) < Math.abs(recoilX)) {
            // Mesma direção, mas abaixo do limite
            newVelocity.x = recoilX;
        }
        // Caso contrário, já estamos no limite ou acima — não muda nada

        // --- EIXO Y ---
        float recoilY = recoil.y;
        float bodyY = bodySpeed.y;

        if (Math.signum(recoilY) != Math.signum(bodyY)) {
            newVelocity.y = bodyY + recoilY;
        } else if (Math.abs(bodyY) < Math.abs(recoilY)) {
            newVelocity.y = recoilY;
        }

        // Aplica a nova velocidade calculada
        body.setLinearVelocity(0,0);
        body.setLinearVelocity(newVelocity);
    }


    /// Obtém a tag presente em uma body dentro de uma fixture
    public static GameObjectTag getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof GameObjectTag tag)) return null;
        return tag;
    }
}
