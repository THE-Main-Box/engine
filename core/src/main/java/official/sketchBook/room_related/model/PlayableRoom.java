package official.sketchBook.room_related.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.room_related.worldGeneration_related.connection.RoomNode;
import official.sketchBook.util_related.helpers.body.RoomBodyDataConversor;

import java.util.ArrayList;
import java.util.List;

public class PlayableRoom {
    private Room roomData;
    private RoomNode roomConnections;
    private final World world;
    private List<GameObject> gameObjects;
    private List<Body> nativeBodies;

    private boolean active;

    public PlayableRoom(World world) {
        this.world = world;
    }

    public void initialize(Room roomData, RoomNode roomConnections) {
        this.roomData = roomData;
        this.roomConnections = roomConnections;
        this.nativeBodies = RoomBodyDataConversor.convertTileListToBodyList(roomData.getTiles(), this.world);

        this.gameObjects = new ArrayList<>();

        this.active = true;
    }

    public void reset() {
        // destrói os corpos da sala anterior
        for (Body body : nativeBodies) {
            world.destroyBody(body);
        }
        nativeBodies.clear();

        for (GameObject object : gameObjects) {
            object.dispose();
        }
        gameObjects.clear();

        roomData = null;
        gameObjects = null;
        active = false;
    }

    public void syncObjectsBodies() {
        if (!active) return;

        for (GameObject object : gameObjects) {
            if (object.getPhysicsC() != null)
                object.getPhysicsC().syncBodyObjectPos();
        }
    }

    public void updateObjects(float delta) {
        if (!active) return;

        for (GameObject object : gameObjects) {
            object.update(delta);
        }
    }


    public void render(SpriteBatch batch) {
        if (!active) return;

        for (GameObject object : gameObjects) {
            object.render(batch);
        }
    }

    public void dispose() {
        if (!active) return;

        for (GameObject object : gameObjects) {
            object.dispose();
        }
    }

    public void addNativeBody(Body body) {
        this.nativeBodies.add(body);
    }

    public void removeNativeBody(Body body) {
        if (this.nativeBodies.remove(body)) {
            this.world.destroyBody(body);
        }
    }

    public void addObject(GameObject object) {
        this.gameObjects.add(object);
    }

    public void removeObject(GameObject object) {
        this.gameObjects.remove(object);
    }


    public boolean isActive() {
        return active;
    }

    public Room getRoomData() {
        return roomData;
    }

    public void setRoomData(Room roomData) {
        this.roomData = roomData;
    }

    public RoomNode getRoomConnections() {
        return roomConnections;
    }

    public void setRoomConnections(RoomNode roomConnections) {
        this.roomConnections = roomConnections;
    }

    public List<Body> getNativeBodies() {
        return nativeBodies;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public World getWorld() {
        return world;
    }
}
