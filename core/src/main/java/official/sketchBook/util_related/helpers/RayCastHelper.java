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

import static official.sketchBook.PlayScreen.PPM;

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
        activeRays = new ArrayList<>();
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
    }

    public void addRay(Vector2 start, Vector2 end) {
        activeRays.add(new Ray<>(start, end)); // Adiciona o raio à lista
    }

    /**
     * desenhar o raio para debugging, apenas desenha a linha entre start e end.
     */
    public void renderRays(Matrix4 projectionMatrix) {
        // Configura a matriz de projeção do shapeRenderer
        shapeRenderer.setProjectionMatrix(projectionMatrix);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        // Desenha cada raio da lista ativa
        for (Ray<Vector2> ray : activeRays) {
            // Aqui, assumindo que ray.start e ray.end já estão em pixels:
            float startX = ray.start.x / PPM;
            float startY = ray.start.y / PPM;
            float endX = ray.end.x / PPM;
            float endY = ray.end.y / PPM;
            shapeRenderer.line(startX, startY, endX, endY);

        }

//        System.out.println(activeRays.size());

        shapeRenderer.end();

        clearRays();
    }


    public void clearRays() {
        activeRays.clear(); // Limpa os raios ativos
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
     */
    public static class RayCastData {
        public final Fixture fixture;
        public final Vector2 point;
        public final Vector2 normal;
        public final float fraction;

        public RayCastData(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            this.fixture = fixture;
            this.point = point;
            this.normal = normal;
            this.fraction = fraction;
        }
    }
}
