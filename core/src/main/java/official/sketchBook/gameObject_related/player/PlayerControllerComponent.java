package official.sketchBook.gameObject_related.player;

import com.badlogic.gdx.Input;
import official.sketchBook.components_related.base_component.ControllerComponent;
import official.sketchBook.components_related.toUse_component.MovementComponent;
import official.sketchBook.components_related.toUse_component.TimerComponent;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.util_related.enumerators.directions.Directions;
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
    private Directions lastDirectionPressed = Directions.STILL;

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
