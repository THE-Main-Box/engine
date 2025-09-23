package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.util_related.enumerators.type.ObjectType;
import official.sketchBook.engine.util_related.utils.general.GameObjectTag;

import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getTag;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;

public interface GroundInteractableII extends RayCasterII {

    // Vetores reutilizáveis para cálculos de raycast (evita alocação constante)
    Vector2 rayStart = new Vector2();
    Vector2 rayEnd   = new Vector2();
    Vector2 center   = new Vector2();

    // Buffer de resultado temporário para os raycasts
    boolean[] hitBuffer = {false};

    World getWorld();
    Body getBody();

    float getWidth();   // largura total do corpo em pixels
    float getHeight();  // altura total do corpo em pixels

    void setOnGround(boolean onGround);

    /**
     * Atualiza o valor de "em pé" (onGround) do personagem usando 3 raycasts:
     * - Pé esquerdo (borda esquerda da body)
     * - Pé direito (borda direita da body)
     * - Pé central (meio da body)
     *
     * Se qualquer um dos rays encontrar um objeto do tipo ENVIRONMENT,
     * o personagem será considerado "em pé".
     */
    default void updateOnGroundValue() {
        Body body = getBody();
        if (getWorld() == null || body == null) return;

        setOnGround(false);

        // Centro da body (posição física no mundo)
        center.set(body.getPosition());

        // Metade da largura e altura da body, já convertidos para metros (Box2D usa metros, não pixels)
        final float halfWidth  = getWidth()  * 0.5f / PPM;
        final float halfHeight = getHeight() * 0.5f / PPM;

        // Altura da posição dos pés (um pouco acima do limite inferior da body, para não colidir com o próprio chão do Box2D)
        final float footY = center.y - halfHeight + 1f / PPM; // 1 pixel acima da base do corpo

        // Distância do raycast para baixo (profundidade para checar o chão)
        final float rayLength = 4f / PPM;

        // Margem horizontal para ajustar os pontos de checagem (positivo = mais dentro, negativo = mais fora)
        final float margin = -1 / PPM;

        hitBuffer[0] = false;

        // Pés do personagem (borda esquerda, centro e borda direita)
        float[] xOffsets = {
            -halfWidth + margin,  // Pé esquerdo (limite lateral da body)
            0f,                  // Pé central (meio do corpo)
            halfWidth - margin   // Pé direito (limite lateral da body)
        };

        // Faz raycast em cada pé
        for (float dx : xOffsets) {
            if (castRay(center.x + dx, footY, rayLength)) {
                setOnGround(true);
                break; // já achou chão, não precisa testar mais
            }
        }
    }

    /**
     * Realiza um raycast para baixo a partir de (startX, startY).
     * @return true se colidiu com ENVIRONMENT, false caso contrário.
     */
    private boolean castRay(float startX, float startY, float rayLength) {
        rayStart.set(startX, startY);
        rayEnd.set(startX, startY - rayLength);

        hitBuffer[0] = false;

        getRayCastHelper().castRay(rayStart, rayEnd, data -> {
            GameObjectTag tag = getTag(data.fixture());
            if (tag != null && tag.type() == ObjectType.ENVIRONMENT) {
                hitBuffer[0] = true;
            }
        });

        return hitBuffer[0];
    }
}
