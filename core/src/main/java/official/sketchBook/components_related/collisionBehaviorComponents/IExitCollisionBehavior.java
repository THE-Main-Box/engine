package official.sketchBook.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;

public interface IExitCollisionBehavior {
    /**
     * Trata a saída de uma colisão
     *
     * @param controller controlador do projétil que chamou
     * @param contact    contato com o objeto
     * @param target     objeto que colidimos
     */
    void onCollisionExit(
        ProjectileControllerComponent controller,
        Contact contact,
        Object target
    );
}
