package official.sketchBook.components_related.toUse_component.entity;

import official.sketchBook.components_related.base_component.KeyBindedControllerComponent;
import official.sketchBook.gameObject_related.base_model.GameObject;
import official.sketchBook.gameObject_related.entities.Player;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.values.ControlKeys;
import official.sketchBook.weapon_related.base_model.BaseWeapon;

import static official.sketchBook.util_related.info.values.constants.SpeedRelatedConstants.Player.*;

public class PlayerControllerComponent extends KeyBindedControllerComponent {
    private final Player player;

    private float accelToApply;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Direction lastDirectionPressed = Direction.STILL;

    private boolean holdingUse = false;

    public PlayerControllerComponent(GameObject gameObject) {
        this.player = (Player) gameObject;

        // Vinculando teclas ao movimento
        bindKey(ControlKeys.move_left, this::moveLeft);
        bindKey(ControlKeys.move_right, this::moveRight);
        bindKey(ControlKeys.jump, this::jump);
        bindKey(ControlKeys.recharge, this::rechargeWeapon);
        bindKey(ControlKeys.use, this::use);
    }

    private void use(boolean pressed) {
        if (pressed) {
            if(player.getWeapon(BaseWeapon.class) != null){
                player.useWeapon();
            }
        }

//        holdingUse = pressed;

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
        updateHMovementValues();

        continueToUseWeapon();
    }

    private void continueToUseWeapon(){
        if(holdingUse){
            if(player.getWeapon(BaseWeapon.class) != null){
                player.useWeapon();
            }
        }
    }

    //atualiza as variaveis de movimentação em cada estado, se estivermos no ar
    private void updateHMovementValues() {

        float accel, decel, maxAccel;

        if (player.isOnGround()) {
            // se está no chão
            accel = GROUND_ACCEL;
            maxAccel = HORIZONTAL_WALK_MAX;
            decel = HORIZONTAL_WALK_DEC;
        } else {
            // se está no ar
            accel = AIR_ACCEL;
            maxAccel = HORIZONTAL_AIR_MAX;
            decel = HORIZONTAL_AIR_DEC;
        }


        this.accelToApply = accel * getAccelBoost();
        player.getMoveC().setxMaxSpeed(maxAccel);
        player.getMoveC().setDecelerationX(decel);

    }

    /// Obtém um valor de influência de aceleração
    private float getAccelBoost(){
        if (player.hasRangeWeapon() && player.getRangeWeapon().getShootStateManager().isShooting()) {
            return 0.1f;
        }

        return 1f;
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
