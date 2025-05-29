package official.sketchBook.components_related.toUse_component.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import official.sketchBook.components_related.base_component.KeyBindedControllerComponent;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameDataManagement_related.GameObjectManager;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.gameObject_related.entities.Player;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.util.values.ControlKeys;
import official.sketchBook.util_related.info.util.values.SpeedRelatedVariables;
import official.sketchBook.util_related.registers.EmitterRegister;

import java.util.Objects;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class PlayerControllerComponent extends KeyBindedControllerComponent {
    private final Player player;

    private final float groundAccel = 50f / PPM;
    private final float airAccel = 10f / PPM;
    private float accelToApply;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Direction lastDirectionPressed = Direction.STILL;

    private TimerComponent jumpBufferTimer;

    public PlayerControllerComponent(GameObject gameObject) {
        this.player = (Player) gameObject;
        jumpBufferTimer = new TimerComponent(0.2f);

        // Vinculando teclas ao movimento
        bindKey(ControlKeys.move_left, this::moveLeft);
        bindKey(ControlKeys.move_right, this::moveRight);
        bindKey(ControlKeys.jump, this::jump);
        bindKey(Input.Keys.F, this::testShoot);

    }

    private void testShoot(boolean pressed) {
        if (EmitterRegister.getEmitter(player) == null) return;

        if (pressed) {

            Vector2 origin = new Vector2(
                (!player.isFacingForward() ? player.getX() - 4 : player.getX() + player.getWidth() + 4) / PPM,
                (player.getY() + player.getHeight() / 2) / PPM
            );

            Vector2 target = new Vector2(
                player.isFacingForward() ? 1000 / PPM : -1000 / PPM, -0
            );

            float timeToReach = 0.1f;

            shoot(origin, target, timeToReach);
        }
    }

    private void shoot(Vector2 origin, Vector2 target, float timeToReach) {
        Emitter e = EmitterRegister.getEmitter(player);

        if (e == null) return;

        Projectile proj = e.obtain(origin);

        if (proj == null) return;

        e.fire(proj, target.x, target.y, timeToReach);
    }

    private void jump(boolean pressed) {
        if (pressed) {

            jumpBufferTimer.reset();
            jumpBufferTimer.start();

        } else {
            if (player.getjComponent().isJumping()) {
                player.getjComponent().jump(true);

                jumpBufferTimer.stop();
                jumpBufferTimer.reset();
            }
        }

        if (pressed && !player.isOnGround()) {
            Vector2 origin = new Vector2(
                (!player.isFacingForward() ? player.getX() - 2 : player.getX() + player.getWidth() + 2) / PPM,
                (player.getY()) / PPM
            );

            Vector2 target = new Vector2(0, -1);

            float timeToReach = 0.1f;

            player.getBody().setLinearVelocity(
                player.getBody().getLinearVelocity().x,
                player.getBody().getLinearVelocity().y += 3
            );
            shoot(origin, target, timeToReach);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        updateJump(delta);
        updateMovement();
        updateAirMovementValues();

    }

    private void updateJump(float delta) {
        jumpBufferTimer.update(delta);

        jumpBufferTimer.resetByFinished();

        if (jumpBufferTimer.isRunning() && player.canJump()) {
            player.getjComponent().jump(false);  // Executa o pulo
            jumpBufferTimer.stop();  // Para o buffer após o pulo ser executado
            jumpBufferTimer.reset();
        }

    }

    //atualiza as variaveis de movimentação em cada estado, se estivermos no ar
    private void updateAirMovementValues() {

        float accel = 0, decel = 0, maxAccel = 0;

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
            player.setFacingForward(false);

        }
    }

    private void movePlayer(Direction directions) {
        switch (directions) {
            case LEFT:
                player.setFacingForward(false);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setxAccel(-accelToApply);
                break;
            case RIGHT:
                player.setFacingForward(true);
                player.getMoveC().setAcceleratingX(true);
                player.getMoveC().setxAccel(accelToApply);
                break;
            case STILL:
                player.getMoveC().setAcceleratingX(false);
                break;
            default:
                break;
        }
    }
}
