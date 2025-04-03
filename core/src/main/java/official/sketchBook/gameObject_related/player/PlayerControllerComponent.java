package official.sketchBook.gameObject_related.player;

import com.badlogic.gdx.Input;
import official.sketchBook.components_related.base_component.ControllerComponent;
import official.sketchBook.components_related.toUse_component.MovementComponent;
import official.sketchBook.components_related.toUse_component.TimerComponent;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.util_related.enumerators.directions.Directions;
import official.sketchBook.util_related.info.util.values.ControlKeys;

public class PlayerControllerComponent extends ControllerComponent {
    private final Player player;

    private final float groundAccel = 50f;
    private final float airAccel = 25f;
    private float speedToApply;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Directions lastDirectionPressed = Directions.STILL;

    private TimerComponent jumpBufferTimer = new TimerComponent(0.2f);

    public PlayerControllerComponent(GameObject gameObject) {
        super();
        this.player = (Player) gameObject;

        // Vinculando teclas ao movimento
        bindKey(ControlKeys.move_left, this::moveLeft);
        bindKey(ControlKeys.move_right, this::moveRight);
        bindKey(ControlKeys.jump, this::jump);

    }

    private void jump(boolean pressed) {
        if (pressed) {

            jumpBufferTimer.reset();
            jumpBufferTimer.start();

        } else {

//            if (player.isJumping()) {
//                player.jump(true);
//
//                jumpBufferTimer.stop();
//                jumpBufferTimer.reset();
//
//            }

        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
//        jumpBufferTimer.update(delta);
//
//        jumpBufferTimer.resetByFinished();
//
//        if (jumpBufferTimer.isRunning() && player.canJump()) {
//            player.jump(false);  // Executa o pulo
//            jumpBufferTimer.stop();  // Para o buffer após o pulo ser executado
//            jumpBufferTimer.reset();
//        }
//
//        updateMovement();
//        updateAirMovementValues();


    }

    //atualiza as variaveis de movimentação em cada estado, se estivermos no ar
    private void updateAirMovementValues() {
//        if (player.isOnGround()) {
//            player.getMoveC().setxMaxSpeed(SpeedRelatedVariables.PLAYER_HORIZONTAL_WALK);
//            player.getMoveC().setDecelerationX(SpeedRelatedVariables.PLAYER_HORIZONTAL_WALK_DEC);
//
//            this.speedToApply = groundAccel;
//        } else {
//            player.getMoveC().setxMaxSpeed(SpeedRelatedVariables.PLAYER_HORIZONTAL_AIR);
//            player.getMoveC().setDecelerationX(SpeedRelatedVariables.PLAYER_HORIZONTAL_AIR_DEC);
//            this.speedToApply = airAccel;
//        }
//
//        if (player.isAirHabilityUsed()) {
//            player.getMoveC().setxMaxSpeed(SpeedRelatedVariables.PLAYER_HORIZONTAL_AIR_HABILITY_USED);
//            player.getMoveC().setDecelerationX(SpeedRelatedVariables.PLAYER_HORIZONTAL_AIR_DEC);
//
//            this.speedToApply = groundAccel;
//        }

    }

    private void updateMovement() {
        if (leftPressed && !rightPressed) {
            movePlayer(Directions.LEFT);
        } else if (!leftPressed && rightPressed) {
            movePlayer(Directions.RIGHT);
        } else if (!leftPressed) {
            movePlayer(Directions.STILL);
        } else {
            movePlayer(lastDirectionPressed); // Continua na última direção pressionada
        }

    }

    private void moveLeft(boolean pressed) {
        leftPressed = pressed;
        if (pressed) {
            lastDirectionPressed = Directions.LEFT;
        }
    }

    private void moveRight(boolean pressed) {
        rightPressed = pressed;
        if (pressed) {
            lastDirectionPressed = Directions.RIGHT;
        }
    }

    private void movePlayer(Directions directions) {
        switch (directions) {
            case LEFT:
                player.getComponent(MovementComponent.class).setAcceleratingX(true);
                player.setFacingForward(false);
                player.getComponent(MovementComponent.class).setxAccel(-speedToApply);
                break;
            case RIGHT:
                player.getComponent(MovementComponent.class).setAcceleratingX(true);
                player.setFacingForward(true);
                player.getComponent(MovementComponent.class).setxAccel(speedToApply);
                break;
            case STILL:
                player.getComponent(MovementComponent.class).setAcceleratingX(false);
                break;
            default:
                break;
        }
    }
}
