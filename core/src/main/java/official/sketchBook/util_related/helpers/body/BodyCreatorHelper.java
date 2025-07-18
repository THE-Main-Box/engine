package official.sketchBook.util_related.helpers.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


public class BodyCreatorHelper {

    /**
     * Criação de uma body Quadrangular
     * @param world mundo físico a ser instanciado
     * @param width largura do corpo
     * @param height altura do corpo
     * @param type tipo do corpo a ser criado
     * @param restitution restituição de movimento ao colidir
     * @param density densidade do corpo
     * @param friction fricção do corpo com outros corpos
     * @param position posição do corpo*/
    public static Body createBox(World world, Vector2 position, float width, float height, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM); // ← Conversão

        Body body = world.createBody(bodyDef);

        PolygonShape shape = createBoxShape(width, height, 0, 0);

        body.createFixture(createFixture(shape, density, friction, restitution));

        shape.dispose();


        return body;
    }

    /**
     * Criamos um body com uma fixture circular
     *
     * @param world       mundo físico de onde a body será instanciada
     * @param position    posição na qual a body estará presente (será convertida para metros automaticamente)
     * @param radius      raio da body
     * @param type        tipo do corpo a ser criado
     * @param density     densidade do corpo em questão
     * @param friction    fricção do corpo com o mundo ao redor
     * @param restitution o quanto que a body poderá saltar após uma colisão, uma restituição de movimento
     */
    public static Body createCircle(World world, Vector2 position, float radius, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef(); //Criamos os valores padrão para o corpo
        bodyDef.type = type; //Definimos o tipo do corpo
        bodyDef.position.set(position.x / PPM, position.y / PPM); //Definimos a posição dentro do world

        Body body = world.createBody(bodyDef);//Criamos a body

        CircleShape shape = createCircleShape(radius, 0, 0);

        body.createFixture(createFixture(shape, density, friction, restitution));

        shape.dispose();

        return body;
    }

    /**
     * Criamos uma shape de círculo
     *
     * @param radius raio do circulo
     * @param rX     posição relativa à body no eixo X
     * @param rY     posição relativa à body no eixo Y
     */
    public static CircleShape createCircleShape(float radius, float rX, float rY) {
        CircleShape shape = new CircleShape();

        shape.setRadius(radius / PPM);
        shape.setPosition(new Vector2(rX / PPM, rY / PPM));

        return shape;
    }

    /**
     * Cria um corpo em forma de cápsula (capsule body), que pode ser vertical ou horizontal
     * dependendo da relação entre largura e altura fornecida.
     *
     * A cápsula é formada por dois círculos e dois segmentos de borda conectando-os.
     * - Se largura > altura: a cápsula é horizontal (círculos laterais)
     * - Se altura >= largura: a cápsula é vertical (círculos superior/inferior)
     *
     * @param world        Mundo físico Box2D
     * @param position     Posição central do corpo em pixels
     * @param width        Largura total do corpo em pixels
     * @param height       Altura total do corpo em pixels
     * @param type         Tipo de corpo (estático, dinâmico, cinemático)
     * @param density      Densidade física da fixture
     * @param friction     Fricção da fixture
     * @param restitution  Restituição (elasticidade) da fixture
     * @return             Um corpo cápsula configurado e pronto para uso
     */
    public static Body createCapsule(World world, Vector2 position, float width, float height, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM);

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true); // Impede a rotação do corpo (útil para personagens)

        // Determinar tipo de cápsula com base nas proporções
        boolean isHorizontal = width > height;

        if (isHorizontal) {
            // === CAPSULA HORIZONTAL ===
            float radius = height / 2f;
            float halfWidth = width / 2f;

            // Círculo esquerdo
            createCircleFixture(body, radius, -halfWidth + radius, 0, density, friction, restitution);

            // Círculo direito
            createCircleFixture(body, radius, halfWidth - radius, 0, density, friction, restitution);

            // Segmentos horizontais (topo e base)
            createEdge(body,
                new Vector2((-halfWidth + radius) / PPM, radius / PPM),
                new Vector2((halfWidth - radius) / PPM, radius / PPM),
                density, friction, restitution
            );
            createEdge(body,
                new Vector2((-halfWidth + radius) / PPM, -radius / PPM),
                new Vector2((halfWidth - radius) / PPM, -radius / PPM),
                density, friction, restitution
            );

        } else {
            // === CAPSULA VERTICAL ===
            float radius = width / 2f;
            float halfHeight = height / 2f;

            // Círculo inferior
            createCircleFixture(body, radius, 0, -halfHeight + radius, density, friction, restitution);

            // Círculo superior
            createCircleFixture(body, radius, 0, halfHeight - radius, density, friction, restitution);

            // Segmentos verticais (laterais)
            createEdge(body,
                new Vector2(-radius / PPM, (-halfHeight + radius) / PPM),
                new Vector2(-radius / PPM, (halfHeight - radius) / PPM),
                density, friction, restitution
            );
            createEdge(body,
                new Vector2(radius / PPM, (-halfHeight + radius) / PPM),
                new Vector2(radius / PPM, (halfHeight - radius) / PPM),
                density, friction, restitution
            );
        }

        return body;
    }

    /**
     * Cria e anexa uma fixture circular a uma body com offset.
     *
     * @param body         Body de destino
     * @param radius       Raio da shape (em pixels)
     * @param offsetX      Offset relativo no eixo X (em pixels)
     * @param offsetY      Offset relativo no eixo Y (em pixels)
     */
    private static void createCircleFixture(Body body, float radius, float offsetX, float offsetY, float density, float friction, float restitution) {
        CircleShape shape = createCircleShape(radius, offsetX, offsetY);
        body.createFixture(createFixture(shape, density, friction, restitution));
        shape.dispose();
    }


    private static void createEdge(Body body, Vector2 start, Vector2 end, float density, float friction, float restitution) {
        EdgeShape edge = new EdgeShape();
        edge.set(start, end);

        body.createFixture(createFixture(edge, density, friction, restitution));
        edge.dispose();
    }

    /**
     * Cria uma shape de círculo
     *
     * @param height altura da fixture em pixels
     * @param width  largura da fixture em pixels
     * @param rX     posição relativa à body no eixo X
     * @param rY     posição relativa à body no eixo Y
     */
    public static PolygonShape createBoxShape(float width, float height, float rX, float rY) {
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(
            (width / 2) / PPM,
            (height / 2) / PPM,
            new Vector2(rX / PPM, rY / PPM),
            0
        );

        return shape;
    }

    /**
     * Encapsulamento da criação de uma fixture
     *
     * @param friction    Fricção da fixture
     * @param density     Densidade da fixture
     * @param restitution Restituição da fixture
     * @param shape       Formato do corpo da fixture
     */
    public static FixtureDef createFixture(Shape shape, float density, float friction, float restitution) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        return fixtureDef;
    }
}
