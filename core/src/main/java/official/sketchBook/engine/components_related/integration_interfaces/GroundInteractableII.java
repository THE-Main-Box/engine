package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.util_related.enumerators.type.ObjectType;
import official.sketchBook.engine.util_related.utils.general.GameObjectTag;

import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getTag;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;

public interface GroundInteractableII extends RayCasterII {

    // Vetores pré-alocados para raycasts
    Vector2 rayStart = new Vector2();
    Vector2 rayEnd = new Vector2();
    Vector2 center = new Vector2();

    // Flag temporária para o raycast
    boolean[] hitBuffer = {false};

    World getWorld();
    Body getBody();

    float getWidth();
    float getHeight();

    void setOnGround(boolean onGround);

    default void updateOnGroundValue() {
        if (getWorld() == null || getBody() == null) return;

        setOnGround(false);

        center.set(getBody().getPosition());

        float hw = getWidth() / 2f / PPM;
        float hh = getHeight() / 2f / PPM;
        float footY = center.y - hh + 2 / PPM;

        float rayLength = 4f / PPM;
        float margin = 5f / PPM;

        hitBuffer[0] = false;

        // Esquerda
        if (castRay(center.x - hw + margin, footY, rayLength)) return;
        // Centro
        if (castRay(center.x, footY, rayLength)) return;
        // Direita
        castRay(center.x + hw - margin, footY, rayLength);
    }

    private boolean castRay(float startX, float startY, float rayLength) {
        rayStart.set(startX, startY);
        rayEnd.set(startX, startY - rayLength);

        hitBuffer[0] = false;

        getRayCastHelper().castRay(rayStart, rayEnd, data -> {
            GameObjectTag tag = getTag(data.fixture());
            if (tag != null && tag.type() == ObjectType.ENVIRONMENT) {
                setOnGround(true);
                hitBuffer[0] = true;
            }
        });

        return hitBuffer[0];
    }
}
