package official.sketchBook.components_related.integration_interfaces;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.info.values.GameObjectTag;

import static official.sketchBook.util_related.helpers.HelpMethods.getTag;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public interface GroundInteractableII extends RayCasterII{

    World getWorld();
    Body getBody();

    float getWidth();

    float getHeight();

    default void updateOnGroundValue() {
        if (getWorld() == null || getBody() == null) return;

        setOnGround(false);
        Vector2 center = getBody().getPosition();

        float halfWidth = (getWidth() / 2f) / PPM;
        float halfHeight = (getHeight() / 2f) / PPM;
        float footY = center.y - halfHeight + 2 / PPM; // Origem no "pé"

        float rayLength = 4f / PPM; // Alcance pequeno pra detectar chão

        float margin = 5f / PPM; // pequeno recuo lateral

        Vector2[] rayStarts = new Vector2[]{
            new Vector2(center.x - halfWidth + margin, footY),
            new Vector2(center.x, footY),
            new Vector2(center.x + halfWidth - margin, footY)
        };

        for (Vector2 start : rayStarts) {
            Vector2 end = new Vector2(start.x, start.y - rayLength);

            getRayCastHelper().castRay(start, end, data -> {
                GameObjectTag groundTag = getTag(data.fixture());
                if (groundTag != null && groundTag.type() == ObjectType.ENVIRONMENT) {
                    setOnGround(true);
                }
            });

        }
    }

    void setOnGround(boolean onGround);

}
