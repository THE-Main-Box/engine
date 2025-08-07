package official.sketchBook.components_related.interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.toUse_component.object.EffectManagerComponent;

public interface EffectReceiver{
    Body getBody();
    EffectManagerComponent getEffectManager();
}
