package official.sketchBook.components_related.integration_interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.components_related.toUse_component.object.JumpComponent;

public interface JumpCapableII {
    /// Verifica se o objeto permite um salto
    boolean canJump();
    /// Garante que haverá um componente de pulo
    JumpComponent getJumpC();

    /// Garante que estamos com um objeto físico
    BasePhysicsComponent getPhysicsC();

    Body getBody();

    /// função interna para determinar se estamos no chão ou não, geralmente de entidades
    boolean isOnGround();

    /// Abstrai o uso da função de pulo
    default void jump(boolean cancelJump){
        getJumpC().jump(cancelJump);
    }

}
