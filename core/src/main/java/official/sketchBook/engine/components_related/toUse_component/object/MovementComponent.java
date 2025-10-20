package official.sketchBook.engine.components_related.toUse_component.object;


import com.badlogic.gdx.math.Vector2;
import official.sketchBook.engine.components_related.base_component.Component;


public class MovementComponent implements Component {
    private float xSpeed, ySpeed;
    private float xAccel, yAccel;
    private float xMaxSpeed, yMaxSpeed;
    private float decelerationX, decelerationY;
    private final float weight;

    private boolean acceleratingX, acceleratingY;

    private boolean movingX = false;

    private boolean canApplyMovement = true;

    public MovementComponent(float weight) {
        this.weight = weight;
    }

    @Override
    public void update(float deltaTime) {
        applyAcceleration();
        applyDeceleration();
        limitSpeed();

    }

    private void applyAcceleration() {
        if (acceleratingX) xSpeed += xAccel / weight;
        if (acceleratingY) ySpeed += yAccel / weight;
    }

    private void applyDeceleration() {
        if (!acceleratingX) xSpeed = applyFriction(xSpeed, decelerationX);
        if (!acceleratingY) ySpeed = applyFriction(ySpeed, decelerationY);
    }

    private float applyFriction(float speed, float deceleration) {
        if (speed == 0 || deceleration == 0) return 0;

        // Se a velocidade é menor que o deceleration, zera
        if (Math.abs(speed) <= deceleration) return 0;

        return speed - deceleration * Math.signum(speed);
    }


    private void limitSpeed() {
        xSpeed = Math.max(-xMaxSpeed, Math.min(xSpeed, xMaxSpeed));
        ySpeed = Math.max(-yMaxSpeed, Math.min(ySpeed, yMaxSpeed));
    }

    // Métodos para definir velocidade sem alterar aceleração
    public final void setSpeed(float x, float y) {
        this.xSpeed = x;
        this.ySpeed = y;
    }

    public final void setSpeed(Vector2 speed) {
        this.xSpeed = speed.x;
        this.ySpeed = speed.y;
    }

    // Getters e Setters
    public boolean isMovingX() {
        return movingX;
    }

    public void setMovingX(boolean moving) {
        this.movingX = moving;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public float getxAccel() {
        return xAccel;
    }

    public void setxAccel(float xAccel) {
        this.xAccel = xAccel;
    }

    public float getyAccel() {
        return yAccel;
    }

    public void setyAccel(float yAccel) {
        this.yAccel = yAccel;
    }

    public float getxMaxSpeed() {
        return xMaxSpeed;
    }

    public void setxMaxSpeed(float xMaxSpeed) {
        this.xMaxSpeed = xMaxSpeed;
    }

    public float getyMaxSpeed() {
        return yMaxSpeed;
    }

    public void setyMaxSpeed(float yMaxSpeed) {
        this.yMaxSpeed = yMaxSpeed;
    }

    public void setDecelerationX(float decelerationX) {
        this.decelerationX = decelerationX;
    }

    public float getDecelerationX() {
        return decelerationX;
    }

    public float getDecelerationY() {
        return decelerationY;
    }

    public void setDecelerationY(float decelerationY) {
        this.decelerationY = decelerationY;
    }

    public boolean isAcceleratingX() {
        return acceleratingX;
    }

    public void setAcceleratingX(boolean acceleratingX) {
        this.acceleratingX = acceleratingX;
    }

    public boolean isAcceleratingY() {
        return acceleratingY;
    }

    public void setAcceleratingY(boolean acceleratingY) {
        this.acceleratingY = acceleratingY;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public boolean isCanApplyMovement() {
        return canApplyMovement;
    }

    public void setCanApplyMovement(boolean canApplyMovement) {
        this.canApplyMovement = canApplyMovement;
    }
}
