package official.sketchBook.components_related.toUse_component.object;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class JumpComponent extends Component {


    private boolean jumping, falling, jumpedFromGround, coyoteConsumed;
    private float jumpForce, fallSpeedAfterJCancel;
    private boolean enhancedGravity;
    private TimerComponent coyoteTimer;

    private Entity entity;

    private boolean prevOnGround;        // armazena onGround do frame anterior
    private boolean landedThisFrame;     // true somente no frame em que aterrissa

    private final TimerComponent landBuffer;

    public JumpComponent(Entity entity, float jumpForce, float fallSpeedAfterJCancel, float coyoteTimeTarget, boolean enhancedGravity) {
        this.entity = entity;
        this.coyoteTimer = new TimerComponent(coyoteTimeTarget);
        this.landBuffer = new TimerComponent(0.1f); // 100ms de buffer

        this.enhancedGravity = enhancedGravity;

        this.jumpForce = jumpForce / PPM;
        this.fallSpeedAfterJCancel = fallSpeedAfterJCancel / PPM;

        this.jumping = false;
        this.falling = false;
        this.jumpedFromGround = false;
        this.coyoteConsumed = false;
    }

    @Override
    public void update(float delta) {
        coyoteTimer.update(delta);
        landBuffer.update(delta);

        updateJump();
        applyEnhancedGravity();

        updateLandedFlag();
    }

    private void updateLandedFlag() {
        boolean currentlyOnGround = entity.isOnGround();

        // detecta aterrissagem: só se estava no ar ANTES e caiu (falling)
        landedThisFrame = !prevOnGround && currentlyOnGround;

        prevOnGround = currentlyOnGround;

        if (landedThisFrame) {
            landBuffer.reset();
            landBuffer.start();
        }

        landBuffer.resetByFinished();
    }


    private void applyEnhancedGravity() {
        if (!enhancedGravity) return;

        float defaultScale = 1f;
        float enhancedScale = 2f;

        if (jumpedFromGround && falling) {
            if (entity.getBody().getGravityScale() == defaultScale) {
                entity.getBody().setGravityScale(enhancedScale);
            }
        } else if ((!jumpedFromGround && !falling) || entity.isOnGround()) {
            if (entity.getBody().getGravityScale() > defaultScale) {
                entity.getBody().setGravityScale(defaultScale);
            }
        }
    }

    private void updateJump() {
        updateJumpingFallingFlags();
        updateJumpedFlag();
        updateCoyoteState();
    }

    //pula ou cancela um pulo
    public void jump(boolean cancel) {
        if (!cancel) {
            if (entity.canJump()) {

                //zera a velocidade vertical caso ela seja negativa antes de pular para evitar um pulo fraco
                if (entity.getBody().getLinearVelocity().y < 0) {
                    entity.getBody().setLinearVelocity(entity.getBody().getLinearVelocity().x, 0);
                }

                // Aplica o impulso inicial do pulo
                entity.getPhysicsC().applyTrajectoryImpulse(
                    jumpForce,
                    0
                );

                jumpedFromGround = true;
                jumping = true;
                falling = false;

                coyoteTimer.stop();
                coyoteTimer.reset();
            }
        } else {
            if (entity.getPhysicsC().getBody().getLinearVelocity().y > 0 && entity.getPhysicsC().getBody().getLinearVelocity().y > fallSpeedAfterJCancel) {

                entity.getPhysicsC().getBody().setLinearVelocity(
                    new Vector2(
                        entity.getPhysicsC().getBody().getLinearVelocity().x,
                        fallSpeedAfterJCancel
                    )
                );

            }
        }
    }

    //atualiza o estado do coyoteTimer
    private void updateCoyoteState() {

        //caso o jogador esteja a cair, não tenha pulado e o temporizador ainda não iniciou iniciamos ele,
        //mas isso apenas caso ainda não tenhamos usado o primeiro pulo do coyote
        /*
            isso porque se não fazer isso o temporizador do coyoteTiming irá ser executado várias vezes devido
            às circunstâncias semelhantes a usar o pulo do coyote e não fazer isso,
            fazendo o temporizador reiniciar toda a vez que estivermos a cair
        */

        if (falling && !jumpedFromGround && !coyoteTimer.isRunning() && !coyoteConsumed) {
            coyoteTimer.reset();
            coyoteTimer.start();

            coyoteConsumed = true;
        }

        //impede que o coyoteTiming exceda seus limites ao resetarmos quando tocamos no chão pulamos oou o tempo acabe
        if (coyoteTimer.isFinished() || entity.isOnGround() || jumpedFromGround) {
            coyoteTimer.stop();
            coyoteTimer.reset();
        }
    }

    //atualiza o estado referido a ter pulado ou não do chão
    private void updateJumpedFlag() {
        if (entity.isOnGround() && jumpedFromGround) {
            jumpedFromGround = false;

        } else if (!entity.isOnGround() && jumping) {
            jumpedFromGround = true;
        }
    }

    //atualiza se o jogador está caindo, ou pulando ou no chão
    private void updateJumpingFallingFlags() {
        if (entity.isOnGround()) {
            // O jogador está no chão, resetamos as flags de pulo e queda
            jumping = false;
            falling = false;

            coyoteConsumed = false;

        } else if (entity.getPhysicsC().getBody().getLinearVelocity().y > 0) {
            // O jogador está subindo (pulando)
            jumping = true;
            falling = false;
        } else if (!entity.isOnGround() && entity.getPhysicsC().getBody().getLinearVelocity().y < 0) {
            // O jogador está caindo
            falling = true;
            jumping = false;

        }

    }

    public boolean isEntityLanded() {
        return landBuffer.isRunning() && !landBuffer.isFinished();
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isFalling() {
        return falling;
    }

    public boolean isJumpedFromGround() {
        return jumpedFromGround;
    }

    public boolean isCoyoteConsumed() {
        return coyoteConsumed;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public float getFallSpeedAfterJCancel() {
        return fallSpeedAfterJCancel;
    }

    public TimerComponent getCoyoteTimer() {
        return coyoteTimer;
    }
}
