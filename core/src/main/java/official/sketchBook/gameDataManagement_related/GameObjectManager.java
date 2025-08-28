package official.sketchBook.gameDataManagement_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.base_model.PhysicalGameObject;
import official.sketchBook.gameObject_related.entities.Dummy;
import official.sketchBook.gameObject_related.entities.Player;
import official.sketchBook.room_related.model.PlayableRoom;
import official.sketchBook.room_related.worldGeneration_related.blueprint.RoomBlueprint;
import official.sketchBook.room_related.worldGeneration_related.generation.WorldGenerator;
import official.sketchBook.room_related.worldGeneration_related.generation.WorldLayout;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.registers.ProjectilePoolRegister;

import java.util.ArrayList;
import java.util.List;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Screen.*;

public class GameObjectManager {

    private PlayableRoomManager manager;
    private PlayableRoom currentRoom;
    private WorldGenerator generator;

    //TODO: criar classe onde centralizamos a criação, salvamento, edição etc... de salas e mundos

    public Player player;

    public GameObjectManager(World world) {
        this.generator = new WorldGenerator(5, 5);
        this.manager = new PlayableRoomManager(world, generator.getGrid());

        this.currentRoom = this.manager.createNewRoom();

        this.initRoom();

        Dummy dumDum = new Dummy(
            100,
            100,
            12,
            28,
            false,
            false,
            world
        );
        currentRoom.addObject(dumDum);

        this.player = new Player(
            60,
            100,
            16,
            16,
            true,
            false,
            world
        );
        currentRoom.addObject(player);

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
        System.out.println(ProjectilePoolRegister.getPool(currentRoom).getRoomOwner().getRoomData().getTag());

    }

    public void setCurrentRoom(int x, int y) {
        currentRoom = manager.initRoom(currentRoom, x, y);

        PlayScreen.worldWidth = currentRoom.getRoomData().getWidthInP();
        PlayScreen.worldHeight = currentRoom.getRoomData().getHeightInP();
    }

    private int[][] initBaseTileMap() {
//        TILES_IN_WIDTH = 39;
//        TILES_IN_HEIGHT = 21;

        int[][] toReturn = new int[TILES_IN_HEIGHT][TILES_IN_WIDTH];

        for (int y = 0; y < TILES_IN_HEIGHT; y++) {
            for (int x = 0; x < TILES_IN_WIDTH; x++) {
                toReturn[y][x] = 0;

                List<Boolean> canCreate = new ArrayList<>();
                canCreate.add(y >= TILES_IN_HEIGHT - 2); // chão
                canCreate.add(y == 0); //teto
                canCreate.add(x == 0);//parede esquerda
                canCreate.add(x == TILES_IN_WIDTH - 1); // parede direita
                canCreate.add(x == TILES_IN_WIDTH - 10 && y >= TILES_IN_HEIGHT-4);//parede de testes


                for (boolean value : canCreate) {
                    if (value) {
                        toReturn[y][x] = 1;
                        break;
                    }
                }

            }
        }

        return toReturn;
    }

    public void updateObjectsAfterStep() {
        currentRoom.updateObjectsAfterStep();
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

    public void removeGameObject(PlayableRoom room, PhysicalGameObject object) {
        room.removeObject(object);
    }

    public void addGameObject(PlayableRoom room, PhysicalGameObject object) {
        room.addObject(object);
    }

    public PlayableRoom getCurrentRoom() {
        return currentRoom;
    }
}
