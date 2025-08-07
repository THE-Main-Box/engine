package official.sketchBook.components_related.toUse_component.object;

import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.interfaces.JumpCapable;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public class JumpComponent implements Component {


    private boolean jumping, falling, jumpedFromGround, coyoteConsumed;
    private float jumpForce, fallSpeedAfterJCancel;
    private boolean enhancedGravity;

    private JumpCapable JumpableObject;

    private boolean prevOnGround;        // armazena onGround do frame anterior
    private boolean landedThisFrame;     // true somente no frame em que aterrissa

    private final TimerComponent landBuffer;
    private TimerComponent coyoteTimer;
    private TimerComponent jumpBufferTimer;


    /**
     * Componente de pulo
     *
     * @param JumpableObject                entidade dona do componente
     * @param jumpForce             força de pulo da entidade
     * @param coyoteTimeTarget      tempo disponível para o jogador pular após começar a cair
     * @param fallSpeedAfterJCancel Força para terminar o pulo, quanto maior o número,
     *                              mais forte o pulo deve ser para poder parar ele
     * @param jumpBufferTime        tempo que o jogador tem para entrar no estado de pulo,
     *                              por pressionar o pulo uma única vez
     * @param landBufferTime        tempo de recuperação após aterrar no chão
     * @param enhancedGravity       a gravidade ficará mais forte na queda
     */
    public JumpComponent(
        JumpCapable JumpableObject,
        float jumpForce,
        float fallSpeedAfterJCancel,
        float coyoteTimeTarget,
        float jumpBufferTime,
        float landBufferTime,
        boolean enhancedGravity
    ) {
        this.JumpableObject = JumpableObject;
        this.coyoteTimer = new TimerComponent(coyoteTimeTarget);
        this.landBuffer = new TimerComponent(landBufferTime);
        this.jumpBufferTimer = new TimerComponent(jumpBufferTime);

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
        updateJump();
        applyEnhancedGravity();

        updateLandedFlag();

        jumpBufferTimer.update(delta);
        coyoteTimer.update(delta);
        landBuffer.update(delta);
    }

    private void updateLandedFlag() {
        boolean currentlyOnGround = JumpableObject.isOnGround();

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
        float enhancedScale = 1.3f;

        if (jumpedFromGround && falling) {
            if (JumpableObject.getBody().getGravityScale() == defaultScale) {
                JumpableObject.getBody().setGravityScale(enhancedScale);
            }
        } else if ((!jumpedFromGround && !falling) || JumpableObject.isOnGround()) {
            if (JumpableObject.getBody().getGravityScale() > defaultScale) {
                JumpableObject.getBody().setGravityScale(defaultScale);
            }
        }
    }

    private void updateJump() {
        updateJumpingFallingFlags();
        updateJumpedFlag();
        updateCoyoteState();

        updateJumpBasedOnBuffer();
    }

    private void updateJumpBasedOnBuffer() {
        jumpBufferTimer.resetByFinished();

        if (jumpBufferTimer.isRunning()) {
            if (JumpableObject.canJump()) {
                executeJump(false); // Executa o pulo real
                jumpBufferTimer.stop();
                jumpBufferTimer.reset();
            }
        }
    }

    public void jump(boolean cancel) {
        if (!cancel) {
            if (JumpableObject.isOnGround()) {//Se estivermos no chão já executamos o pulo
                executeJump(false);
            } else {//Caso precisemos executar um pulo, e não estejamos no chão, preparamos o buffer
                jumpBufferTimer.reset();
                jumpBufferTimer.start();
            }
        } else {
            if (isJumping()) {// cancelamos o pulo caso estejamos ainda no processo de salto
                executeJump(true);

                //Preparamos o buffer para o próximo uso
                jumpBufferTimer.stop();
                jumpBufferTimer.reset();

            }

        }

    }

    //pula ou cancela um pulo
    private void executeJump(boolean cancel) {
        if (!cancel) {
            if (JumpableObject.canJump()) {

                //zera a velocidade vertical caso ela seja negativa antes de pular para evitar um pulo fraco
                if (JumpableObject.getBody().getLinearVelocity().y < 0) {
                    JumpableObject.getBody().setLinearVelocity(JumpableObject.getBody().getLinearVelocity().x, 0);
                }

                // Aplica o impulso inicial do pulo
                JumpableObject.getPhysicsC().applyTrajectoryImpulse(
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
            if (JumpableObject.getPhysicsC().getBody().getLinearVelocity().y > 0 && JumpableObject.getPhysicsC().getBody().getLinearVelocity().y > fallSpeedAfterJCancel) {

                JumpableObject.getPhysicsC().getBody().setLinearVelocity(
                        JumpableObject.getPhysicsC().getBody().getLinearVelocity().x,
                        fallSpeedAfterJCancel
                );

            }
        }
    }

    /**
     * Atualiza o estado do coyoteTimer
     * caso o jogador esteja a cair, não tenha pulado e o temporizador ainda não iniciou iniciamos ele,
     * mas isso apenas caso ainda não tenhamos usado o primeiro pulo do coyote
     * isso porque se não fazer isso o temporizador do coyoteTiming irá ser executado várias vezes devido
     * às circunstâncias semelhantes a usar o pulo do coyote e não fazer isso,
     * fazendo o temporizador reiniciar toda a vez que estivermos a cair
     */
    private void updateCoyoteState() {

        if (falling && !jumpedFromGround && !coyoteTimer.isRunning() && !coyoteConsumed) {
            coyoteTimer.reset();
            coyoteTimer.start();

            coyoteConsumed = true;
        }

        //impede que o coyoteTiming exceda seus limites ao resetarmos quando tocamos no chão pulamos oou o tempo acabe
        if (coyoteTimer.isFinished() || JumpableObject.isOnGround() || jumpedFromGround || coyoteConsumed) {
            coyoteTimer.stop();
            coyoteTimer.reset();
        }
    }

    //atualiza o estado referido a ter pulado ou não do chão
    private void updateJumpedFlag() {
        if (JumpableObject.isOnGround() && jumpedFromGround) {
            jumpedFromGround = false;

        } else if (!JumpableObject.isOnGround() && jumping) {
            jumpedFromGround = true;
        }
    }

    //atualiza se o jogador está caindo, ou pulando ou no chão
    private void updateJumpingFallingFlags() {
        if (JumpableObject.isOnGround()) {
            // O jogador está no chão, resetamos as flags de pulo e queda
            jumping = false;
            falling = false;

            coyoteConsumed = false;

        } else if (JumpableObject.getPhysicsC().getBody().getLinearVelocity().y > 0) {
            // O jogador está subindo (pulando)
            jumping = true;
            falling = false;
        } else if (!JumpableObject.isOnGround() && JumpableObject.getPhysicsC().getBody().getLinearVelocity().y < 0) {
            // O jogador está caindo
            falling = true;
            jumping = false;

        }

    }

    public void setFallSpeedAfterJCancel(float fallSpeedAfterJCancel) {
        this.fallSpeedAfterJCancel = fallSpeedAfterJCancel;
    }

    public void setJumpForce(float jumpForce) {
        this.jumpForce = jumpForce;
    }

    public void setEnhancedGravity(boolean enhancedGravity) {
        this.enhancedGravity = enhancedGravity;
    }

    public boolean isJumpedFromGround() {
        return jumpedFromGround;
    }

    public boolean isCoyoteConsumed() {
        return coyoteConsumed;
    }

    public boolean isPrevOnGround() {
        return prevOnGround;
    }

    //Métodos auxiliares para o "CanJump()" de uma entidade//
    public boolean isCoyoteJumpAvailable() {
        return coyoteTimer.isRunning();
    }

    public boolean isOnGround(){
        return JumpableObject.isOnGround();
    }

    //Flags de estado//
    public boolean isEntityLanded() {
        return landBuffer.isRunning() && !landBuffer.isFinished();
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isEnhancedGravity() {
        return enhancedGravity;
    }

    public float getFallSpeedAfterJCancel() {
        return fallSpeedAfterJCancel;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public boolean isFalling() {
        return falling;
    }
}
