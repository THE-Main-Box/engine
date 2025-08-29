package official.sketchBook.engine.components_related.collisionBehaviorComponents;

import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.engine.components_related.toUse_component.projectile.ProjectileControllerComponent;

public interface IEnterCollisionBehavior {
    /**
     * Trata a entrada de uma colisão
     *
     * @param controller controlador do projétil que chamou
     * @param contact    contato com o objeto
     * @param target     objeto que colidimos
     */
    void onCollisionEnter(
        ProjectileControllerComponent controller,
        Contact contact,
        Object target
    );

}
