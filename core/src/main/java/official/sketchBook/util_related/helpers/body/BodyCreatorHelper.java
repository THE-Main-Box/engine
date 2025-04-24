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
}
