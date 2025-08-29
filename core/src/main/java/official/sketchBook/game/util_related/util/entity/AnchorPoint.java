package official.sketchBook.game.util_related.util.entity;

public class AnchorPoint {
    private float x;
    private float y;

    public AnchorPoint() {
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }
}
