package official.sketchBook.worldGeneration_related.util;

import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;
import official.sketchBook.util_related.helpers.IO.RoomBlueprintIO;
import official.sketchBook.util_related.helpers.IO.WorldLayoutBlueprintIO;
import official.sketchBook.worldGeneration_related.generation.WorldGenerator;
import official.sketchBook.worldGeneration_related.generation.WorldGrid;
import official.sketchBook.worldGeneration_related.connection.RoomConnection;
import official.sketchBook.worldGeneration_related.connection.RoomNode;
import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;
import official.sketchBook.worldGeneration_related.model.RoomCell;
import official.sketchBook.worldGeneration_related.generation.WorldLayout;

import java.util.ArrayList;
import java.util.List;

public class TestWorldGen {
    public static void main(String[] args) {

        int width = 5, height = 5;

        WorldLayout worldLayout = new WorldLayout(width, height);
        WorldGenerator generator = new WorldGenerator(width, height);

        TileType[][] baseMap = createBasicTileMap(PlayScreen.TILES_IN_WIDTH, PlayScreen.TILES_IN_HEIGHT, TileType.EMPTY);

        RoomBlueprint spawn = new RoomBlueprint(baseMap, RoomType.SPAWN, "spawn");
        RoomBlueprint corridor = new RoomBlueprint(baseMap, RoomType.NORMAL, "corridor");
        RoomBlueprint boss = new RoomBlueprint(baseMap, RoomType.BOSS, "boss");

        worldLayout.setBlueprint(0,0, spawn);
        worldLayout.setBlueprint(1,0, corridor);
        worldLayout.setBlueprint(2,0, corridor);
        worldLayout.setBlueprint(1,1, boss);

        generator.applyLayoutToGrid(worldLayout);

        printAsciiMap(generator.getGrid());

        // salva as salas e o mundo
        RoomBlueprintIO blueprintIO = new RoomBlueprintIO();
        WorldLayoutBlueprintIO wBlueprintIO = new WorldLayoutBlueprintIO();
        WorldLayoutConverter converter = new WorldLayoutConverter(blueprintIO);

        blueprintIO.save(spawn.getTag(), spawn);
        blueprintIO.save(corridor.getTag(), corridor);
        blueprintIO.save(boss.getTag(), boss);

        wBlueprintIO.save("world_01", converter.toBlueprint(worldLayout));


        System.out.println("teste de loading de dados");
        //obtém as salas e o mundo e os converte novamente para um objeto
        var worldB = wBlueprintIO.load("world_01");

        WorldLayout worldLayout1 = converter.convert(worldB);

        generator = new WorldGenerator(worldLayout.getWidth(), worldLayout1.getHeight());

        // 4. Aplica o layout no gerador
        generator.applyLayoutToGrid(worldLayout1);

        // 5. Teste: imprime o mapa e conexões
        TestWorldGen.printAsciiMap(generator.getGrid());


    }

    public static void printWorldConnections(WorldGrid world){
        // Teste: printar as conexões da sala central
        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) { //percorre a lista de celulas de salas

                RoomNode node = world.getNode(x, y); //obtém o nó da celula

                //se houver um nó, existe uma sala, portanto iteramos sobre ela
                if (node != null && node.getRoom() != null) {

                    System.out.println("Sala em (" + x + ", " + y + ") com ID #" + node.getId() + " esta conectada com:");
                    for (RoomConnection connection : node.getConnections()) { //mostra as conexões da sala atual
                        RoomNode connected = connection.getTarget();
                        RoomCell connectedCell = findCellByNode(world, connected);
                        if (connectedCell != null) {
                            System.out.println("- (" + connectedCell.getX() + ", " + connectedCell.getY() + ") ID #" + connected.getId());
                        }
                    }

                    System.out.println(); // linha em branco pra separar
                }

            }
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
