package official.sketchBook.worldGeneration_related.generation;

import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;

public class WorldGenerator {

    private final WorldGrid grid;

    public WorldGenerator(int width, int height) {
        this.grid = new WorldGrid(width, height);
    }

    public void applyLayoutToGrid(WorldLayout layout) {
        grid.emptyGridRooms();

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

    public WorldGrid getGrid() {
        return grid;
    }
}
