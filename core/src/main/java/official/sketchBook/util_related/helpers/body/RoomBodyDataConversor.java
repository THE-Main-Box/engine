package official.sketchBook.util_related.helpers.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class RoomBodyDataConversor {

    //valida se existem tiles ao redor do mesmo tipo, se sim nós não criamos.
    //validamos também se são sólidos,
    // já que não podemos criar um corpo para uma tile caso esteja entre tiles sólidas do mesmo tipo
    private static boolean shouldCreateBody(int x, int y, TileType[][] tiles) {
        TileType current = tiles[y][x];

        // Verifica se os tiles vizinhos são do mesmo tipo
        boolean up = y + 1 < tiles.length && tiles[y + 1][x] == current;
        boolean down = y - 1 >= 0 && tiles[y - 1][x] == current;
        boolean left = x - 1 >= 0 && tiles[y][x - 1] == current;
        boolean right = x + 1 < tiles[0].length && tiles[y][x + 1] == current;

        // Se todos os lados forem do mesmo tipo, o tile está "encapsulado"
        boolean surrounded = up && down && left && right;

        // Criamos corpo apenas se não estiver totalmente rodeado por tiles do mesmo tipo
        return !surrounded;
    }

    //converte a lista de tiles em uma lista de bodies como tiles unicas
    public static List<Body> convertTileListToBodyList(TileType[][] tiles, World world) {
        List<Body> bodies = new ArrayList<>();
        int rows = tiles.length;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < tiles[0].length; x++) {

                if (tiles[y][x] == TileType.BLOCK && shouldCreateBody(x, y, tiles)) {
                    createBodies(bodies, TileType.BLOCK, world, x, y, rows);
                }

            }
        }

        return bodies;
    }

    private static void createBodies(List<Body> bodies, TileType type, World world, int x, int y, int rows) {
        if (Objects.requireNonNull(type) == TileType.BLOCK) {
            Body body = createBoxBody(world, type, x, y, 1, 1, rows);
            body.setUserData(TileType.BLOCK);
            bodies.add(body);
        }
    }

    //cria uma body quadrada
    private static Body createBoxBody(World world, TileType type, int x, int y, int width, int height, int totalRows) {
        float tileSize = PlayScreen.TILES_DEFAULT_SIZE;
        int invY = (totalRows - 1) - y;

        float worldX = (x + width / 2f) * tileSize;
        float worldY = (invY - (height / 2f - 0.5f)) * tileSize;

        return BodyCreatorHelper.createBox(
            world,
            new Vector2(worldX, worldY),
            width * tileSize,
            height * tileSize,
            BodyDef.BodyType.StaticBody,
            type.getDensity(),
            type.getFriction(),
            type.getRestituition()
        );
    }

    public static List<Body> buildMergedBodies(TileType[][] tiles, World world) {
        int rows = tiles.length;
        int cols = tiles[0].length;
        boolean[][] visited = new boolean[rows][cols];
        List<Body> bodies = new ArrayList<>();

        //percorre a lista bidimensional
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                //verifica se nós ainda não visitamos essa tile e se ela é sólida
                if (!visited[y][x] && tiles[y][x].isSolid()) {
                    TileType currentType = tiles[y][x];  // Guarda o tipo do tile atual

                    // Tenta criar o maior retângulo possível a partir de (x, y) somente com o mesmo tipo de tile
                    int width = 1;
                    int height = 1;

                    // Primeiro, acha a largura, mas apenas com tiles do mesmo tipo
                    while (x + width < cols && tiles[y][x + width] == currentType && !visited[y][x + width]) {
                        width++;
                    }

                    // Agora acha a altura, checando se todas as colunas da linha seguinte têm o mesmo tipo de tile
                    boolean done = false;
                    while (!done && y + height < rows) {
                        for (int dx = 0; dx < width; dx++) {
                            // Se a tile não for do mesmo tipo ou já foi visitada, finaliza
                            if (tiles[y + height][x + dx] != currentType || visited[y + height][x + dx]) {
                                done = true;
                                break;
                            }
                        }
                        if (!done) height++;
                    }

                    // Marca todas as tiles como visitadas
                    for (int dy = 0; dy < height; dy++) {
                        for (int dx = 0; dx < width; dx++) {
                            visited[y + dy][x + dx] = true;
                        }
                    }

                    // Cria o corpo físico somente para tiles do mesmo tipo
                    Body body = createBoxBody(world, currentType, x, y, width, height, rows);
                    body.setUserData(currentType);
                    bodies.add(body);
                }
            }
        }

        return bodies;
    }

}
