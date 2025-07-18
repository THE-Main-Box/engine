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

    public static Body createCapsule(World world, Vector2 position, float width, float height, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM);

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);

        float radius = width / 2f;
        float halfHeight = height / 2f;

        // 1. Bottom circle
        CircleShape bottomCircle = createCircleShape(radius, 0, -halfHeight + radius);
        body.createFixture(createFixture(bottomCircle, density, friction, restitution));
        bottomCircle.dispose();

        // 2. Top circle
        CircleShape topCircle = createCircleShape(radius, 0, halfHeight - radius);
        body.createFixture(createFixture(topCircle, density, friction, restitution));
        topCircle.dispose();

        // 3. Vertical edges (laterais)
        Vector2 leftStart = new Vector2(-radius / PPM, (-halfHeight + radius) / PPM);
        Vector2 leftEnd   = new Vector2(-radius / PPM, (halfHeight - radius) / PPM);
        Vector2 rightStart = new Vector2(radius / PPM, (-halfHeight + radius) / PPM);
        Vector2 rightEnd   = new Vector2(radius / PPM, (halfHeight - radius) / PPM);

        createEdge(body, leftStart, leftEnd, density, friction, restitution);
        createEdge(body, rightStart, rightEnd, density, friction, restitution);

        return body;
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
