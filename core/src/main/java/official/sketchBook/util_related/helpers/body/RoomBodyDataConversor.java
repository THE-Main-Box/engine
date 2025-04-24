package official.sketchBook.util_related.helpers.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.TileType;

import java.util.ArrayList;
import java.util.List;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class RoomBodyDataConversor {

    private static final float DEF_DENS = 0.1f, DEF_FRIC = 1f, DEF_REST = 0f;

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
                    bodies.add(createBoxBody(world, x, y, 1, 1, rows));
                }
            }
        }

        return bodies;
    }

    //cria uma body quadrada
    private static Body createBoxBody(World world, int x, int y, int width, int height, int totalRows) {
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
            DEF_DENS,
            DEF_FRIC,
            DEF_REST
        );
    }

    //criam corpos unidos
    public static List<Body> buildMergedBodies(TileType[][] tiles, World world) {
        int rows = tiles.length;
        int cols = tiles[0].length;
        boolean[][] visited = new boolean[rows][cols];
        List<Body> bodies = new ArrayList<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!visited[y][x] && tiles[y][x].isSolid()) {
                    // Tenta criar o maior retângulo possível a partir de (x, y)
                    int width = 1;
                    int height = 1;

                    // Primeiro, acha a largura
                    while (x + width < cols && tiles[y][x + width].isSolid() && !visited[y][x + width]) {
                        width++;
                    }

                    // Agora acha a altura, checando se todas as colunas da linha seguinte têm a mesma largura
                    boolean done = false;
                    while (!done && y + height < rows) {
                        for (int dx = 0; dx < width; dx++) {
                            if (!tiles[y + height][x + dx].isSolid() || visited[y + height][x + dx]) {
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

                    // Cria o corpo físico
                    bodies.add(createBoxBody(world, x, y, width, height, rows));
                }
            }
        }

        return bodies;
    }
}
