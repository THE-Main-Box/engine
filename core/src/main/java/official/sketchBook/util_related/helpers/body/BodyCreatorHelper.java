package official.sketchBook.util_related.helpers.body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public class BodyCreatorHelper {

    public static Body createBox(World world, Vector2 position, float width, float height, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM); // ← Conversão

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2f) / PPM, (height / 2f) / PPM); // ← Conversão

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public static Body createCircle(World world, Vector2 position, float radius, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM); // ← Conversão

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius / PPM); // ← Conversão

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public static Body createPolygon(World world, Vector2 position, Vector2[] vertices, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM); // ← Conversão

        Body body = world.createBody(bodyDef);

        Vector2[] scaledVertices = new Vector2[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            scaledVertices[i] = new Vector2(vertices[i].x / PPM, vertices[i].y / PPM); // ← Conversão
        }

        PolygonShape shape = new PolygonShape();
        shape.set(scaledVertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    // ======= AQUI: CRIAÇÃO DE CÁPSULA =======
    public static Body createCapsule(World world, Vector2 position, float width, float height, BodyDef.BodyType type, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x / PPM, position.y / PPM);

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);

        float radius = (width / 2f) / PPM;
        float halfHeight = (height / 2f) / PPM;

        // 1. Circle bottom
        CircleShape bottomCircle = new CircleShape();
        bottomCircle.setRadius(radius);
        bottomCircle.setPosition(new Vector2(0, -halfHeight + radius));

        FixtureDef bottomFixture = new FixtureDef();
        bottomFixture.shape = bottomCircle;
        bottomFixture.density = density;
        bottomFixture.friction = friction;
        bottomFixture.restitution = restitution;

        body.createFixture(bottomFixture);
        bottomCircle.dispose();

        // 2. Circle top
        CircleShape topCircle = new CircleShape();
        topCircle.setRadius(radius);
        topCircle.setPosition(new Vector2(0, halfHeight - radius));

        FixtureDef topFixture = new FixtureDef();
        topFixture.shape = topCircle;
        topFixture.density = density;
        topFixture.friction = friction;
        topFixture.restitution = restitution;

        body.createFixture(topFixture);
        topCircle.dispose();

        // 3. Vertical edges (laterais)
        createEdge(body, new Vector2(-radius, -halfHeight + radius), new Vector2(-radius, halfHeight - radius), density, friction, restitution);
        createEdge(body, new Vector2(radius, -halfHeight + radius), new Vector2(radius, halfHeight - radius), density, friction, restitution);

        return body;
    }

    private static void createEdge(Body body, Vector2 start, Vector2 end, float density, float friction, float restitution) {
        EdgeShape edge = new EdgeShape();
        edge.set(start, end);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = edge;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);
        edge.dispose();
    }
}
