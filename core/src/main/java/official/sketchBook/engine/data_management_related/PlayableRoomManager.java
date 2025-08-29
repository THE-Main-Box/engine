package official.sketchBook.engine.data_management_related;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.room_related.model.PlayableRoom;
import official.sketchBook.engine.room_related.worldGeneration_related.generation.WorldGrid;

public class PlayableRoomManager {
    private final World world;
    private final WorldGrid grid;

    public PlayableRoomManager(World world, WorldGrid grid) {
        this.world = world;
        this.grid = grid;
    }

    public PlayableRoom createNewRoom() {
        return new PlayableRoom(world);
    }

    public PlayableRoom initRoom(PlayableRoom toInit, int x, int y) {
        toInit.initialize(
            grid.getRoom(x, y),
            grid.getNode(x, y)
        );

        return toInit;
    }

}
