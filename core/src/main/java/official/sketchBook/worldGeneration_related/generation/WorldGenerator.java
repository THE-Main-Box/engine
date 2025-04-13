package official.sketchBook.worldGeneration_related.generation;

import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;
import official.sketchBook.worldGeneration_related.structure.Room;
import official.sketchBook.worldGeneration_related.structure.RoomBlueprint;
import official.sketchBook.worldGeneration_related.structure.WorldLayout;

import java.util.Random;

import static official.sketchBook.worldGeneration_related.util.TestWorldGen.createBasicTileMap;

public class WorldGenerator {

    private final WorldGrid grid;

    public WorldGenerator(int width, int height) {
        this.grid = new WorldGrid(width, height);
    }

    public void applyLayoutToGrid(WorldLayout layout) {
        for (int y = 0; y < layout.getHeight(); y++) {
            for (int x = 0; x < layout.getWidth(); x++) {
                RoomBlueprint blueprint = layout.getBlueprint(x, y);
                if (blueprint != null) {
                    grid.setRoom(x, y, blueprint.create()); // Cria Room a partir do blueprint
                }
            }
        }

        grid.connectAdjacentRooms(); // conecta automaticamente
    }

    private void placeRoom(int x, int y, RoomType type) {
        grid.setRoom(x, y, new Room(createBasicTileMap(3, 3, TileType.FLOOR), type));
    }

    public WorldGrid getGrid() {
        return grid;
    }
}
