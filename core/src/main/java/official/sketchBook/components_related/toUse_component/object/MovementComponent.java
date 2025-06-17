package official.sketchBook.components_related.toUse_component.object;


import official.sketchBook.components_related.base_component.Component;

public class MovementComponent extends Component {
    private float xSpeed, ySpeed;
    private float xAccel, yAccel;
    private float xMaxSpeed, yMaxSpeed;
    private float decelerationX, decelerationY;
    private double weight;

    private boolean acceleratingX, acceleratingY;

    private boolean moving = false;


    public MovementComponent(double weight) {
        this.weight = weight;
    }

    @Override
    public void update(float deltaTime) {
        applyAcceleration();
        applyDeceleration();
        limitSpeed();

    }

    private void applyAcceleration() {
        if (acceleratingX) xSpeed += (float) (xAccel / weight);
        if (acceleratingY) ySpeed += (float) (yAccel / weight);
    }

    private void applyDeceleration() {
        if (!acceleratingX) xSpeed = applyFriction(xSpeed, decelerationX);
        if (!acceleratingY) ySpeed = applyFriction(ySpeed, decelerationY);
    }

    private float applyFriction(float speed, float deceleration) {
        if (Math.abs(speed) > 0) {
            float newSpeed = speed - Math.signum(speed) * deceleration;
            return (Math.signum(speed) != Math.signum(newSpeed)) ? 0 : newSpeed;
        }
        return 0;
    }

    private void limitSpeed() {
        xSpeed = Math.max(-xMaxSpeed, Math.min(xSpeed, xMaxSpeed));
        ySpeed = Math.max(-yMaxSpeed, Math.min(ySpeed, yMaxSpeed));
    }

    // Métodos para definir velocidade sem alterar aceleração
    public void setSpeed(float x, float y) {
        this.xSpeed = x;
        this.ySpeed = y;
    }

    // Getters e Setters
    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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

    public float getDecelerationX() {
        return decelerationX;
    }

    public void setDecelerationX(float decelerationX) {
        this.decelerationX = decelerationX;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
