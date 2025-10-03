package official.sketchBook.engine.components_related.toUse_component.entity;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.engine.components_related.base_component.KeyBindedControllerComponent;
import official.sketchBook.engine.gameObject_related.base_model.PhysicalGameObject;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;
import official.sketchBook.engine.weapon_related.base_model.BaseWeapon;
import official.sketchBook.game.entities.Player;

import static official.sketchBook.game.util_related.info.values.ControlKeys.*;
import static official.sketchBook.game.util_related.info.values.constants.SpeedRelatedConstants.Player.*;

public class PlayerControllerComponent extends KeyBindedControllerComponent {
    private final Player player;
    private final Vector2 aim;

    private float accelToApply;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Direction lastDirectionPressed = Direction.STILL;

    public boolean fullAuto = true;

    private boolean shootPressed = false;

    public PlayerControllerComponent(PhysicalGameObject physicalGameObject) {
        this.player = (Player) physicalGameObject;
        this.aim = player.getWeaponWC().getAim();

        bindKey(dir_down, this::down);
        bindKey(dir_up, this::up);

        bindKey(dir_left, this::moveLeft);
        bindKey(dir_right, this::moveRight);

        bindKey(jump, this::jump);

        bindKey(recharge, this::rechargeWeapon);

        bindKey(use, this::normalUse);
        bindKey(secondaryUse, this::secondaryItemUse);
    }

    private void up(boolean pressed) {
        player.getWeaponWC().aim(
            (int) aim.x,
            pressed ? 1 : 0
        );
    }

    private void down(boolean pressed) {
        player.getWeaponWC().aim(
            (int) aim.x,
            pressed ? -1 : 0
        );
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

            if (!fullAuto) {
                shoot();
            }

        }
        if (fullAuto) {
            shootPressed = pressed;
        }

    }


    private void shoot() {
        if (player.getWeaponWC().getWeapon(BaseWeapon.class) != null) {

            player.getWeaponWC().aim(
                player.isxAxisInverted() ? -1 : 1,//Determinamos se estamos mirando pra esquerda ou direita
                (int) aim.y//Atualizamos a direção de olhar para cima apenas quando apertarmos para isso
            );

            player.getWeaponWC().primaryWeaponUse();
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

        if(fullAuto && shootPressed){
            shoot();
        }

    }

    private boolean lastOnGround = true;

    private void updateHorizontalMovementValues() {
        boolean onGround = player.isOnGround();
        if (onGround != lastOnGround) {
            float accel = onGround ? GROUND_ACCEL : AIR_ACCEL;
            float maxSpeed = onGround ? HORIZONTAL_WALK_MAX : HORIZONTAL_AIR_MAX;
            float decel = onGround ? HORIZONTAL_WALK_DEC : HORIZONTAL_AIR_DEC;

            accelToApply = accel;
            var moveC = player.getMoveC();
            moveC.setxMaxSpeed(maxSpeed);
            moveC.setDecelerationX(decel);

            lastOnGround = onGround;
        }
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

    private void movePlayer(Direction direction) {
        switch (direction) {
            case LEFT:
                player.setxAxisInverted(true);
                updateMovementComponent(-accelToApply);
                break;
            case RIGHT:
                player.setxAxisInverted(false);
                updateMovementComponent(accelToApply);
                break;
            case STILL:
                updateMovementComponent(0);
                break;
        }
    }

    private void updateMovementComponent(float xAccel) {
        var moveC = player.getMoveC();

        boolean accelerating = xAccel != 0;
        boolean changedAccel = moveC.getxAccel() != xAccel;

        // Só atualiza se houver mudança real
        if (moveC.isAcceleratingX() != accelerating || changedAccel) {
            moveC.setAcceleratingX(accelerating);
            moveC.setMovingX(accelerating);
            moveC.setxAccel(xAccel);
        }
    }

}
