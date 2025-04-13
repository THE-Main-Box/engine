package official.sketchBook.gameObject_related.player;

import official.sketchBook.components_related.base_component.ControllerComponent;
import official.sketchBook.components_related.toUse_component.TimerComponent;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.util.values.ControlKeys;
import official.sketchBook.util_related.info.util.values.SpeedRelatedVariables;

import static official.sketchBook.PlayScreen.PPM;

public class PlayerControllerComponent extends ControllerComponent {
    private final Player player;

    private final float groundAccel = 50f / PPM;
    private final float airAccel = 25f / PPM;
    private float speedToApply;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Direction lastDirectionPressed = Direction.STILL;

    private TimerComponent jumpBufferTimer = new TimerComponent(0.2f);

    public PlayerControllerComponent(GameObject gameObject) {
        super();
        this.player = (Player) gameObject;

        // Vinculando teclas ao movimento
        bindKey(ControlKeys.move_left, this::moveLeft);
        bindKey(ControlKeys.move_right, this::moveRight);

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        updateMovement();
        updateAirMovementValues();

    }

    //atualiza as variaveis de movimentação em cada estado, se estivermos no ar
    private void updateAirMovementValues() {
        speedToApply = groundAccel;

        player.getMoveC().setxMaxSpeed(SpeedRelatedVariables.PLAYER_HORIZONTAL_WALK);
        player.getMoveC().setDecelerationX(SpeedRelatedVariables.PLAYER_HORIZONTAL_WALK_DEC);

        this.speedToApply = groundAccel;

    }

    private void updateMovement() {
        if (leftPressed && !rightPressed) {
            movePlayer(Direction.LEFT);
        } else if (!leftPressed && rightPressed) {
            movePlayer(Direction.RIGHT);
        } else if (!leftPressed) {
            movePlayer(Direction.STILL);
        } else {
            movePlayer(lastDirectionPressed); // Continua na última direção pressionada
        }

    }

    private void moveLeft(boolean pressed) {
        leftPressed = pressed;
        if (pressed) {
            lastDirectionPressed = Direction.LEFT;
        }
    }

    private void moveRight(boolean pressed) {
        rightPressed = pressed;
        if (pressed) {
            lastDirectionPressed = Direction.RIGHT;
        }
    }

    private void movePlayer(Direction directions) {
        switch (directions) {
            case LEFT:
                player.setFacingForward(false);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setxAccel(-speedToApply);
                break;
            case RIGHT:
                player.setFacingForward(true);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setxAccel(speedToApply);
                break;
            case STILL:
                player.getMoveC().setAcceleratingX(false);
                break;
            default:
                break;
        }
    }
}
