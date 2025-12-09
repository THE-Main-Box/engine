package official.sketchBook.engine.components_related.integration_interfaces.move;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.engine.components_related.toUse_component.object.MovementComponent;

public interface MovementCapableII {
    /// Componente de movimento obrigatório
    MovementComponent getMoveC();
    /// sistema de física base obrigatório
    BasePhysicsComponent getPhysicsC();

    Body getBody();

    void onObjectBodySync();

}
