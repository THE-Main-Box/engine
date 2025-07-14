package official.sketchBook.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;

public interface IEnterCollisionBehavior {
    void onCollisionEnter(
        ProjectileControllerComponent controller,
        Contact contact,
        Object target
    );

}
