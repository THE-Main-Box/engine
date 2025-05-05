package official.sketchBook.animation_related;

public class Sprite {
    private int indexX, indexY;
    private float duration;

    public Sprite(int indexX, int indexY, float duration) {
        this.indexX = indexX;
        this.indexY = indexY;
        this.duration = duration;
    }

    public Sprite(int indexX, int indexY) {
        this.indexX = indexX;
        this.indexY = indexY;
        this.duration = -1;
    }

    public int getIndexX() {
        return indexX;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public float getDuration() {
        return duration;
    }

}
