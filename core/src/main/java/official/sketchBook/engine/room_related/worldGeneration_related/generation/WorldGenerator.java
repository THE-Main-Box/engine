package official.sketchBook.engine.room_related.worldGeneration_related.generation;

import official.sketchBook.engine.util_related.utils.data_to_instance_related.world_gen_blueprint.RoomBlueprint;
import official.sketchBook.engine.room_related.worldGeneration_related.util.converter.RoomConverter;

public class WorldGenerator {

    private final WorldGrid grid;
    private final RoomConverter roomConverter;

    public WorldGenerator() {
        this.grid = new WorldGrid();
        this.roomConverter = new RoomConverter();
    }

    public WorldGenerator(int width, int height) {
        this.grid = new WorldGrid(width, height);
        this.roomConverter = new RoomConverter();
    }

    public void setDimensions(int width, int height){
        this.grid.updateDimensions(width, height);
    }

    public void applyLayoutToGrid(WorldLayout layout) {
        grid.emptyGridRooms();

        for (int y = 0; y < layout.getHeight(); y++) {
            for (int x = 0; x < layout.getWidth(); x++) {
                RoomBlueprint blueprint = layout.getBlueprint(x, y);
                if (blueprint != null) {
                    grid.setRoom(x, y, roomConverter.toObject(blueprint)); // Cria Room a partir do blueprint
                }
            }
        }

        grid.setSourceBlueprint(layout.getSourceBlueprint());
        grid.connectAdjacentRooms(); // conecta automaticamente
    }

    public WorldGrid getGrid() {
        return grid;
    }
}
