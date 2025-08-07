package official.sketchBook.components_related.toUse_component.entity;

import official.sketchBook.components_related.base_component.KeyBindedControllerComponent;
import official.sketchBook.gameObject_related.base_model.PhysicalGameObject;
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


    public PlayerControllerComponent(PhysicalGameObject physicalGameObject) {
        this.player = (Player) physicalGameObject;

        // Vinculando teclas ao movimento
        bindKey(ControlKeys.dir_left, this::moveLeft);
        bindKey(ControlKeys.dir_right, this::moveRight);

        bindKey(ControlKeys.dir_up, this::dirUp);
        bindKey(ControlKeys.dir_down, this::dirDown);

        bindKey(ControlKeys.jump, this::jump);

        bindKey(ControlKeys.recharge, this::rechargeWeapon);

        bindKey(ControlKeys.use, this::normalUse);
        bindKey(ControlKeys.secondaryUse, this::secondaryItemUse);
    }

    private void dirUp(boolean pressed) {
        player.getWeaponWC().setAimingUp(pressed);
    }

    private void dirDown(boolean pressed) {
        player.getWeaponWC().setAimingDown(pressed);
    }

    private void secondaryItemUse(boolean pressed) {
        if (pressed) {
            if (player.getWeaponWC().getWeapon(BaseWeapon.class) != null) {
                player.getWeaponWC().secondaryWeaponUse();
            }
        }
    }

    private void normalUse(boolean pressed) {
        if (pressed) {
            if (player.getWeaponWC().getWeapon(BaseWeapon.class) != null) {
                player.getWeaponWC().primaryWeaponUse();
            }
        }

    }

    private void rechargeWeapon(boolean pressed) {
        if (pressed) {
            player.rechargeWeapon();
        }
    }

    private void jump(boolean pressed) {
        player.jump(!pressed);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        updateMovement();
        updateHorizontalMovementValues();

    }

    //atualiza as variaveis de movimentação em cada estado, se estivermos no ar
    private void updateHorizontalMovementValues() {

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
            player.setxAxisInverted(false);
        }
    }

    private void moveRight(boolean pressed) {
        rightPressed = pressed;
        if (pressed) {
            lastDirectionPressed = Direction.RIGHT;
            player.setxAxisInverted(true);
        }

    }

    private void movePlayer(Direction directions) {
        switch (directions) {
            case LEFT:
                player.setxAxisInverted(false);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setMoving(true);
                player.getMoveC().setxAccel(-accelToApply);
                break;
            case RIGHT:
                player.setxAxisInverted(true);
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
