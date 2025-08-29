package official.sketchBook.engine.room_related.worldGeneration_related.generation;

import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.LayoutParam;
import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.RoomBlueprint;
import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.WorldLayoutBlueprint;

import java.util.List;

public class WorldLayout {
    private final RoomBlueprint[][] blueprintGrid;
    private final int width, height;

    private WorldLayoutBlueprint sourceBlueprint;

    public WorldLayout(int width, int height) {
        this.width = width;
        this.height = height;
        this.blueprintGrid = new RoomBlueprint[height][width];
    }

    /**
     * Aplica um conjunto de layout
     */
    public void setBlueprintsByList(List<LayoutParam> params) {

        for (LayoutParam layout : params) {
            //Se as coordenadas estiverem fora do mapa, ignoramos
            if (layout.y() > blueprintGrid.length
                ||
                layout.x() > blueprintGrid[0].length
            ) {
                continue;
            }
            setBlueprint(layout.x(), layout.y(), layout.blueprint());

        }

    }

    /**
     * Determinamos a blueprint de uma celula numa grade
     *
     * @param x         Endereço horizontal da blueprint
     * @param y         Endereço vertical
     * @param blueprint Blueprint da sala
     */
    public void setBlueprint(int x, int y, RoomBlueprint blueprint) {
        if (isInBounds(x, y)) {
            blueprintGrid[y][x] = blueprint;
        }
    }

    public void resetBlueprints() {
        for (int y = 0; y < height; y++) { // Inicia as células da grid
            for (int x = 0; x < width; x++) {
                blueprintGrid[y][x] = null;
            }
        }
    }

    public void setSourceBlueprint(WorldLayoutBlueprint blueprint) {
        this.sourceBlueprint = blueprint;
    }

    public WorldLayoutBlueprint getSourceBlueprint() {
        return sourceBlueprint;
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

    public boolean hasSourceBlueprint() {
        return sourceBlueprint != null;
    }

}
