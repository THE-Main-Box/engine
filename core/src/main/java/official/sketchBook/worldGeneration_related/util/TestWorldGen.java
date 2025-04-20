package official.sketchBook.worldGeneration_related.util;

import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;
import official.sketchBook.util_related.helpers.IO.RoomBlueprintIO;
import official.sketchBook.worldGeneration_related.connection.RoomConnection;
import official.sketchBook.worldGeneration_related.connection.RoomNode;
import official.sketchBook.worldGeneration_related.generation.WorldGenerator;
import official.sketchBook.worldGeneration_related.generation.WorldGrid;
import official.sketchBook.worldGeneration_related.generation.WorldLayout;
import official.sketchBook.worldGeneration_related.model.RoomCell;
import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;
import official.sketchBook.worldGeneration_related.util.data_manager.RoomManager;
import official.sketchBook.worldGeneration_related.util.data_manager.WorldManager;

public class TestWorldGen {
    public static void main(String[] args) {

        int width = 5, height = 5;

        int[][] baseBpTileMap = createBasicIntegerTileMap(width, height, TileType.EMPTY);

        RoomManager roomManager = new RoomManager();
        WorldManager worldManager = new WorldManager(); // o manager gerencia os dados do world, como salvar e carregar
        WorldGenerator generator = new WorldGenerator(width, height);//o gerador gera o mundo

        //o layout é o modelo inBetween, que será convertido para uma grid
        WorldLayout worldLayout = new WorldLayout(width, height);

        //blueprints de salas
        RoomBlueprint corridor = new RoomBlueprint(baseBpTileMap, RoomType.NORMAL, "corredor");
        RoomBlueprint boss = new RoomBlueprint(baseBpTileMap, RoomType.BOSS, "chefe");
        RoomBlueprint spawn = new RoomBlueprint(baseBpTileMap, RoomType.SPAWN, "spawn");

        //salvando os blueprints
        roomManager.saveBlueprint(corridor);
        roomManager.saveBlueprint(boss);
        roomManager.saveBlueprint(spawn);

        worldLayout.setBlueprint(3, 3, spawn);
        worldLayout.setBlueprint(4, 3, corridor);
        worldLayout.setBlueprint(4, 4, boss);

        generator.applyLayoutToGrid(worldLayout);
        printAsciiMap(generator.getGrid());

        worldManager.saveWorld("test_01", worldLayout);

        worldLayout.resetBlueprints();
        worldLayout.setBlueprint(3, 0, spawn);
        worldLayout.setBlueprint(4, 1, corridor);
        worldLayout.setBlueprint(4, 4, boss);


        worldManager.saveWorld("test_02", worldLayout);

        worldLayout.resetBlueprints();
        worldLayout.setBlueprint(2, 0, spawn);
        worldLayout.setBlueprint(0, 1, corridor);
        worldLayout.setBlueprint(1, 4, boss);


        worldManager.saveWorld("test_03", worldLayout);


        System.out.println("testing rooms saved");

        for (String name : roomManager.listRooms()) {
            System.out.println("sala: " + roomManager.loadRoom(name).getTag() + " salva e carregada com sucesso");
        }

        for (String name : worldManager.listWorlds()) {
            var world = worldManager.loadWorld(name);
            System.out.println("layout: " + world.getSourceBlueprint().getTag() + " salvo e carregado corretamente");
            generator.applyLayoutToGrid(world);
            printAsciiMap(generator.getGrid());
        }
    }

    public static void printAsciiMap(WorldGrid world) {
        int width = world.getWidth();
        int height = world.getHeight();

        System.out.println("======= ASCII MAP =======");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RoomNode node = world.getNode(x, y);
                if (node != null) {
                    System.out.printf("[%02d]", node.getId()); // mostra o ID com 2 dígitos
                } else {
                    System.out.print("[  ]"); // vazio
                }
            }
            System.out.println(); // nova linha a cada linha do grid
        }
    }


    public static TileType[][] createBasicTileMap(int width, int height, TileType fillType) {
        TileType[][] tileMap = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tileMap[y][x] = fillType;
            }
        }
        return tileMap;
    }

    public static int[][] createBasicIntegerTileMap(int width, int height, TileType fillType) {
        int[][] tileMap = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tileMap[y][x] = fillType.getId();
            }
        }
        return tileMap;
    }

    // auxiliador para descobrir em que célula está o node
    public static RoomCell findCellByNode(WorldGrid grid, RoomNode target) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                RoomNode node = grid.getNode(x, y);
                if (node == target) {
                    return grid.getCell(x, y);
                }
            }
        }
        return null;
    }
}
