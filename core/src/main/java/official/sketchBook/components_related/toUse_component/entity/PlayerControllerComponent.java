package official.sketchBook.components_related.toUse_component.entity;

import official.sketchBook.components_related.base_component.KeyBindedControllerComponent;
import official.sketchBook.gameObject_related.base_model.GameObject;
import official.sketchBook.gameObject_related.entities.Player;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.util.values.ControlKeys;
import official.sketchBook.util_related.info.util.values.SpeedRelatedVariables;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class PlayerControllerComponent extends KeyBindedControllerComponent {
    private final Player player;

    private final float groundAccel = 70f / PPM;
    private final float airAccel = 10f / PPM;
    private float accelToApply;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Direction lastDirectionPressed = Direction.STILL;

    public PlayerControllerComponent(GameObject gameObject) {
        this.player = (Player) gameObject;

        // Vinculando teclas ao movimento
        bindKey(ControlKeys.move_left, this::moveLeft);
        bindKey(ControlKeys.move_right, this::moveRight);
        bindKey(ControlKeys.jump, this::jump);
        bindKey(ControlKeys.recharge, this::rechargeWeapon);
        bindKey(ControlKeys.use, this::useWeapon);
    }

    private void useWeapon(boolean pressed){
        if(pressed){
            player.useWeapon();
        }
    }

    private void rechargeWeapon(boolean pressed) {
        if (pressed) {
            player.rechargeWeapon();
        }
    }

    private void jump(boolean pressed) {
        player.getjComponent().jump(!pressed);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        updateMovement();
        updateAirMovementValues();

    }

    //atualiza as variaveis de movimentação em cada estado, se estivermos no ar
    private void updateAirMovementValues() {

        float accel, decel, maxAccel;

        if (player.isOnGround()) {
            // se está no chão
            accel = groundAccel;
            maxAccel = SpeedRelatedVariables.PLAYER_HORIZONTAL_WALK_MAX;
            decel = SpeedRelatedVariables.PLAYER_HORIZONTAL_WALK_DEC;
        } else {
            // se está no ar
            accel = airAccel;
            maxAccel = SpeedRelatedVariables.PLAYER_HORIZONTAL_AIR_MAX;
            decel = SpeedRelatedVariables.PLAYER_HORIZONTAL_AIR_DEC;
        }

        this.accelToApply = accel;
        player.getMoveC().setxMaxSpeed(maxAccel);
        player.getMoveC().setDecelerationX(decel);

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
            player.setFacingForward(false);
        }
    }

    private void moveRight(boolean pressed) {
        rightPressed = pressed;
        if (pressed) {
            lastDirectionPressed = Direction.RIGHT;
            player.setFacingForward(true);
        }

    }

    private void movePlayer(Direction directions) {
        switch (directions) {
            case LEFT:
                player.setFacingForward(false);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setMoving(true);
                player.getMoveC().setxAccel(-accelToApply);
                break;
            case RIGHT:
                player.setFacingForward(true);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setMoving(true);
                player.getMoveC().setxAccel(accelToApply);
                break;
            case STILL:
                player.getMoveC().setAcceleratingX(false);
                player.getMoveC().setMoving(false);
                break;
            default:
                break;
        }
    }
}
