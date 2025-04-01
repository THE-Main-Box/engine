package official.sketchBook.animation_related;

public class Sprite {
    private int indexX, indexY;
    private long duration;

    /*a duração segue essa seguinte formatação
     * "0.2" segundos equivale a "200" milisegundos
     * a segunda formatação é a que será usada na duração de um sprite*/

    public Sprite(int indexX, int indexY, long duration) {
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
