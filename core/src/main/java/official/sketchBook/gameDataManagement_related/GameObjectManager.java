package official.sketchBook.gameDataManagement_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.room_related.model.PlayableRoom;
import official.sketchBook.room_related.worldGeneration_related.blueprint.RoomBlueprint;
import official.sketchBook.room_related.worldGeneration_related.generation.WorldGenerator;
import official.sketchBook.room_related.worldGeneration_related.generation.WorldGrid;
import official.sketchBook.room_related.worldGeneration_related.generation.WorldLayout;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.RoomType;

import static official.sketchBook.screen_related.PlayScreen.TILES_IN_HEIGHT;
import static official.sketchBook.screen_related.PlayScreen.TILES_IN_WIDTH;

public class GameObjectManager {

    private PlayableRoomManager manager;
    private PlayableRoom currentRoom;
    private WorldGenerator generator;

    //TODO: criar classe onde centralizamos a criação, salvamento, edição etc... de salas e mundos

    public GameObjectManager(World world) {
        this.generator = new WorldGenerator(5, 5);
        this.manager = new PlayableRoomManager(world, generator.getGrid());
        this.currentRoom = this.manager.createNewRoom();

        this.initRoom();
    }

    private void initRoom() {
        WorldLayout layout = new WorldLayout(5, 5);

        int[][] baseTileMap = initBaseTileMap();

        layout.setBlueprint(0, 0, new RoomBlueprint(
            baseTileMap,
            RoomType.SPAWN,
            "teste"
        ));

        generator.applyLayoutToGrid(layout);


        setCurrentRoom(0, 0);
        System.out.println(currentRoom.getRoomData().getTag());

    }

    public void setCurrentRoom(int x, int y) {
        currentRoom = manager.initRoom(currentRoom, x, y);

        PlayScreen.worldWidth = currentRoom.getRoomData().getWidthInP();
        PlayScreen.worldHeight = currentRoom.getRoomData().getHeightInP();
    }

    private int[][] initBaseTileMap() {
//        public static final int TILES_IN_WIDTH = 39;
//        public static final int TILES_IN_HEIGHT = 21;

        int[][] toReturn = new int[TILES_IN_HEIGHT][TILES_IN_WIDTH];

        for (int y = 0; y < TILES_IN_HEIGHT; y++) {
            for (int x = 0; x < TILES_IN_WIDTH; x++) {
                toReturn[y][x] = 0;

                boolean canCreate_1 = y >= TILES_IN_HEIGHT - 2;
                boolean canCreate_2 = y == TILES_IN_HEIGHT - 3 && x == TILES_IN_WIDTH - 10;
                boolean canCreate_3 = y == TILES_IN_HEIGHT - 10 && (x >= TILES_IN_WIDTH - 30 && x <= TILES_IN_WIDTH - 10 );

                if (canCreate_1 || canCreate_2 || canCreate_3) {
                    toReturn[y][x] = 1;
                }

            }
        }

        return toReturn;
    }

    public void syncObjectsBodies() {
        currentRoom.syncObjectsBodies();
        currentRoom.updateEntitiesRayCasts();
    }

    public void updateObjects(float delta) {
        currentRoom.updateObjects(delta);
    }

    public void renderObjects(SpriteBatch batch) {
        currentRoom.render(batch);
    }

    public void dispose() {
        currentRoom.dispose();
    }

    public void removeGameObject(PlayableRoom room, GameObject object) {
        room.removeObject(object);
    }

    public void addGameObject(PlayableRoom room, GameObject object) {
        room.addObject(object);
    }

    public PlayableRoom getCurrentRoom() {
        return currentRoom;
    }
}
