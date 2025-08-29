package official.sketchBook.game.gameDataManagement_related;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.data_management_related.GameObjectManagerBase;
import official.sketchBook.game.entities.Dummy;
import official.sketchBook.game.entities.Player;
import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.LayoutParam;
import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.RoomBlueprint;
import official.sketchBook.game.util_related.enumerators.types.RoomType;

import java.util.ArrayList;
import java.util.List;

import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Screen.TILES_IN_HEIGHT;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Screen.TILES_IN_WIDTH;

public class GameObjectManager extends GameObjectManagerBase {


    //TODO: criar classe onde centralizamos a criação, salvamento, edição etc... de salas e mundos

    public Player player;

    public GameObjectManager(World world) {
        super(world);
        this.initWorldGrid(1, 1);

        int[][] baseTileMap = initBaseTileMap();

        //Adiciona mais um layout
        tmpLayoutList.add(
            new LayoutParam(
                0,
                0,
                new RoomBlueprint(
                    baseTileMap,
                    RoomType.SPAWN,
                    "teste"
                )
            )
        );

        this.initRoom(tmpLayoutList);

        this.setCurrentRoom(0,0);

        Dummy dumDum = new Dummy(
            100,
            100,
            12,
            28,
            true,
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
                canCreate.add(x == TILES_IN_WIDTH - 10 && y >= TILES_IN_HEIGHT - 4);//parede de testes


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

}
