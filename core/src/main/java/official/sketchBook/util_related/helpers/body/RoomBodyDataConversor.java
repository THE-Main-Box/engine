package official.sketchBook.util_related.helpers.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.TileType;

import java.util.ArrayList;
import java.util.List;

public class RoomBodyDataConversor {

    private static final float DEF_DENS = 0.1f, DEF_FRIC = 1f, DEF_REST = 0f;

    //valida se existem tiles ao redor do mesmo tipo, se sim nós não criamos.
    //validamos também se são sólidos,
    // já que não podemos criar um corpo para uma tile caso esteja entre tiles sólidas do mesmo tipo
    private static boolean shouldCreateBody(int x, int y, TileType[][] tiles) {
        TileType current = tiles[y][x];

        // Ignora tiles não sólidos (não precisam de corpo físico)
        if (!current.isSolid()) return false;

        // Verifica se os tiles vizinhos são sólidos (não importa o tipo, só se são sólidos)
        boolean up = y + 1 < tiles.length && tiles[y + 1][x].isSolid();
        boolean down = y - 1 >= 0 && tiles[y - 1][x].isSolid();
        boolean left = x - 1 >= 0 && tiles[y][x - 1].isSolid();
        boolean right = x + 1 < tiles[0].length && tiles[y][x + 1].isSolid();

        // Se todos os lados forem sólidos, o tile está "encapsulado", então não criamos corpo
        boolean surrounded = up && down && left && right;

        // Criamos corpo apenas se não estiver totalmente rodeado por sólidos
        return !surrounded;
    }


    public static List<Body> convertTileListToBodyList(TileType[][] tiles, World world) {
        List<Body> bodies = new ArrayList<>();

        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {

                if (tiles[y][x].equals(TileType.BLOCK) && shouldCreateBody(x, y, tiles)) {
                    bodies.add(createBlockTileBody(x, y, world));
                }

            }
        }

        return bodies;
    }

    private static Body createBlockTileBody(int x, int y, World world) {
        float bX = (x * PlayScreen.TILES_DEFAULT_SIZE) - PlayScreen.TILES_DEFAULT_SIZE / 2f;
        float bY = (y * PlayScreen.TILES_DEFAULT_SIZE) - PlayScreen.TILES_DEFAULT_SIZE / 2f;

        return BodyCreatorHelper.createBox(
            world,
            new Vector2(
                bX,
                bY
            ),
            PlayScreen.TILES_DEFAULT_SIZE,
            PlayScreen.TILES_DEFAULT_SIZE,
            BodyDef.BodyType.StaticBody,
            DEF_DENS,
            DEF_FRIC,
            DEF_REST
        );
    }


}
