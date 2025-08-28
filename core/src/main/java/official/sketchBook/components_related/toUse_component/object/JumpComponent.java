package official.sketchBook.components_related.toUse_component.object;

import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.integration_interfaces.JumpCapableII;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public class JumpComponent implements Component {

    /// Flag de estado de pulo
    private boolean jumping, falling, jumpedFromGround, coyoteConsumed;
    /// Valor de impulso de pulo
    private float jumpForce, fallSpeedAfterJCancel;

    private float defGravityScale, enhancedGravityScale;

    /// Flag para determinar se estamos lidando com uma gravidade aprimorada ou não
    private boolean enhancedGravity;

    private boolean superJump;

    /// Objeto que pode saltar
    private final JumpCapableII jumpableObject;

    /// Buffer para dizer se estávamos no chão no último frame
    private boolean prevOnGround;
    /// Buffer para dizer se aterrissamos no chão neste frame
    private boolean landedThisFrame;

    /// Valida se estamos no estado de aterrissagem
    private final TimerComponent landBuffer;
    /// Valida se podemos usar o coyoteTime
    private final TimerComponent coyoteTimer;
    /// Valida se podemos usar um pulo mesmo após termos saído do chão
    private final TimerComponent jumpBufferTimer;

    /**
     * Componente de pulo
     *
     * @param JumpableObject        entidade dona do componente
     * @param jumpForce             força de pulo da entidade
     * @param coyoteTimeTarget      tempo disponível para o jogador pular após começar a cair
     * @param fallSpeedAfterJCancel Força para terminar o pulo, quanto maior o número,
     *                              mais forte o pulo deve ser para poder parar ele
     * @param jumpBufferTime        tempo que o jogador tem para entrar no estado de pulo,
     *                              por pressionar o pulo uma única vez
     * @param landBufferTime        tempo de recuperação após aterrar no chão
     *
     */
    public JumpComponent(
        JumpCapableII JumpableObject,
        float jumpForce,
        float fallSpeedAfterJCancel,
        float coyoteTimeTarget,
        float jumpBufferTime,
        float landBufferTime,
        float defGravityScale,
        float enhancedGravityScale,
        boolean superJump
    ) {
        this.jumpableObject = JumpableObject;
        this.coyoteTimer = new TimerComponent(coyoteTimeTarget);
        this.landBuffer = new TimerComponent(landBufferTime);
        this.jumpBufferTimer = new TimerComponent(jumpBufferTime);

        //Se a gravidade aplicada for diferente que a gravidade especial
        this.enhancedGravity = defGravityScale != enhancedGravityScale;
        this.superJump = superJump;

        this.jumpForce = jumpForce / PPM;
        this.fallSpeedAfterJCancel = fallSpeedAfterJCancel / PPM;

        this.defGravityScale = defGravityScale;
        this.enhancedGravityScale = enhancedGravityScale;

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
        boolean currentlyOnGround = jumpableObject.isOnGround();

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


        if (jumpedFromGround && falling) {
            if (jumpableObject.getBody().getGravityScale() == defGravityScale) {
                jumpableObject.getBody().setGravityScale(enhancedGravityScale);
            }
        } else if (jumpableObject.isOnGround()) {
            if (jumpableObject.getBody().getGravityScale() != defGravityScale) {
                jumpableObject.getBody().setGravityScale(defGravityScale);
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
            if (jumpableObject.canJump()) {
                executeJump(false); // Executa o pulo real
                jumpBufferTimer.stop();
                jumpBufferTimer.reset();
            }
        }
    }

    public void jump(boolean cancel) {
        if (!cancel) {
            if (jumpableObject.isOnGround()) {//Se estivermos no chão já executamos o pulo
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
            if (jumpableObject.canJump()) {

                //zera a velocidade vertical caso ela seja negativa antes de pular para evitar um pulo fraco
                //ou zeramos para evitar um superPulo sem a intenção
                if (jumpableObject.getPhysicsC().getTmpVel().y < 0 ||
                    jumpableObject.getPhysicsC().getTmpVel().y > 0 && !superJump
                ) {
                    jumpableObject.getBody().setLinearVelocity(jumpableObject.getPhysicsC().getTmpVel().x, 0);
                }

                // Aplica o impulso inicial do pulo
                jumpableObject.getPhysicsC().applyTrajectoryImpulse(
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
            if (jumpableObject.getPhysicsC().getTmpVel().y > fallSpeedAfterJCancel) {

                jumpableObject.getPhysicsC().getBody().setLinearVelocity(
                    jumpableObject.getPhysicsC().getTmpVel().x,
                    fallSpeedAfterJCancel
                );

            }
        }
    }

    /**
     * Atualiza o estado do coyoteTimer
     * caso o objeto esteja a cair, não tenha pulado e o temporizador ainda não iniciou iniciamos ele,
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
        if (coyoteTimer.isFinished() || jumpableObject.isOnGround() || jumpedFromGround || coyoteConsumed) {
            coyoteTimer.stop();
            coyoteTimer.reset();
        }
    }

    ///atualiza o estado referido a ter pulado ou não do chão
    private void updateJumpedFlag() {
        if (jumpableObject.isOnGround() && jumpedFromGround) {
            jumpedFromGround = false;

        } else if (!jumpableObject.isOnGround() && jumping) {
            jumpedFromGround = true;
        }
    }

    ///atualiza se o objeto está caindo, ou pulando ou no chão
    private void updateJumpingFallingFlags() {
        boolean onGround = jumpableObject.isOnGround();
        float vy = jumpableObject.getPhysicsC().getTmpVel().y;

        if (onGround) {
            jumping = false;
            falling = false;
            coyoteConsumed = false;
        } else if (vy > 0) {
            jumping = true;
            falling = false;
        } else if (vy < 0) {
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

    public boolean isOnGround() {
        return jumpableObject.isOnGround();
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

    public boolean isLandedThisFrame() {
        return landedThisFrame;
    }

    public boolean isFalling() {
        return falling;
    }
}
