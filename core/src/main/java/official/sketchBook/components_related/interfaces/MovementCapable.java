package official.sketchBook.components_related.interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.components_related.toUse_component.object.MovementComponent;

public interface MovementCapable {
    /// Componente de movimento obrigatório
    MovementComponent getMoveC();
    /// sistema de física base obrigatório
    BasePhysicsComponent getPhysicsC();

    Body getBody();

    void onObjectBodySync();

}
