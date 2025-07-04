package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public class RayCastHelper {
    private final World world;
    private ShapeRenderer shapeRenderer;

    // Lista para armazenar raios ativos
    private final List<Ray<Vector2>> activeRays;

    /**
     * Construtor recebe o mundo do Box2D e inicializa o ShapeRenderer para debug.
     */
    public RayCastHelper(World world) {
        this.world = world;
        this.shapeRenderer = new ShapeRenderer(); // Inicializa o ShapeRenderer para depuração
        this.activeRays = new ArrayList<>();
    }

    /**
     * Executa um RayCast e, se o debug estiver ativado, desenha o raio.
     *
     * @param start  Posição inicial do raio.
     * @param end    Posição final do raio.
     * @param action Ação a ser executada se houver colisão.
     */
    public void castRay(Vector2 start, Vector2 end, Consumer<RayCastData> action) {
        if (world == null || action == null) return;

        world.rayCast((fixture, point, normal, fraction) -> {
            action.accept(new RayCastData(fixture, point, normal, fraction));
            return 0; // Para o RayCast na primeira colisão encontrada
        }, start, end);

        addRay(start, end);

        activeRays.add(new Ray<>(start.cpy(), end.cpy())); // Guarda o raio para debug

    }

    public void addRay(Vector2 start, Vector2 end) {
        activeRays.add(new Ray<>(start, end)); // Adiciona o raio à lista
    }

    /**
     * desenhar o raio para debugging, apenas desenha a linha entre start e end.
     * @param projectionMatrix armazena a matriz como deve ser desenhada(precisa ser escalada conforme o PPM)
     */
    public void render(Matrix4 projectionMatrix) {
        if (activeRays.isEmpty()) return;

        shapeRenderer.setProjectionMatrix(projectionMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Ray<Vector2> ray : activeRays) {
            shapeRenderer.line(
                ray.start.x / PPM, ray.start.y / PPM,
                ray.end.x / PPM, ray.end.y / PPM
            );
        }

        shapeRenderer.end();
    }

    public void clearRays() {
        activeRays.clear(); // Limpa depois de desenhar
    }

    /**
     * Finaliza o ShapeRenderer após o uso.
     * Deve ser chamado quando você não precisar mais desenhar raios.
     */
    public void dispose() {
        shapeRenderer.dispose(); // Libera os recursos
    }

    /**
     * Classe interna para armazenar os dados do RayCast.
     *
     * @param fixture  O objeto físico (fixture) que o raycast atingiu
     * @param point    A posição no mundo onde o raycast colidiu
     * @param normal   A direção perpendicular (normal) da superfície atingida
     * @param fraction A fração do caminho percorrido pelo raycast até a colisão (de 0 a 1)
     */
    public record RayCastData(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
    }
}
