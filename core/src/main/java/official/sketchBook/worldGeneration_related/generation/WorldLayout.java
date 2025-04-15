package official.sketchBook.worldGeneration_related.generation;

import official.sketchBook.worldGeneration_related.model.RoomBlueprint;

public class WorldLayout {
    private final RoomBlueprint[][] blueprintGrid;
    private final int width, height;

    public WorldLayout(int width, int height) {
        this.width = width;
        this.height = height;
        this.blueprintGrid = new RoomBlueprint[height][width];
    }

    public void setBlueprint(int x, int y, RoomBlueprint blueprint) {
        if (isInBounds(x, y)) {
            blueprintGrid[y][x] = blueprint;
        }
    }

    public RoomBlueprint getBlueprint(int x, int y) {
        return isInBounds(x, y) ? blueprintGrid[y][x] : null;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
