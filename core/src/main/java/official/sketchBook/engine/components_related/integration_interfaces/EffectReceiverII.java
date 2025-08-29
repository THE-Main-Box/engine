package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.toUse_component.object.EffectReceiverComponent;

public interface EffectReceiverII {
    Body getBody();
    EffectReceiverComponent getEffectManager();
}
