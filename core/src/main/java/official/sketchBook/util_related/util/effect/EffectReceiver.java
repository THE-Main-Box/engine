package official.sketchBook.util_related.util.effect;

import com.badlogic.gdx.physics.box2d.Body;

public interface EffectReceiver{
    Body getBody();
    EffectManager getEffectManager();
}
