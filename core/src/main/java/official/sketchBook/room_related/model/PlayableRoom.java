package official.sketchBook.room_related.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.gameObject_related.MovableGameObject;
import official.sketchBook.room_related.worldGeneration_related.connection.RoomNode;
import official.sketchBook.util_related.helpers.body.RoomBodyDataConversor;

import java.util.ArrayList;
import java.util.List;

public class PlayableRoom implements Poolable {
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
        this.nativeBodies = RoomBodyDataConversor.buildTileMergedBodies(roomData.getTiles(), this.world);
//        this.nativeBodies = RoomBodyDataConversor.convertTileListToBodyList(roomData.getTiles(), this.world);
        this.gameObjects = new ArrayList<>();

        this.active = true;
    }

    public void reset() {
        this.dispose();

        if (nativeBodies != null) {
            for (Body body : nativeBodies) {
                world.destroyBody(body);
            }
            nativeBodies.clear();
        }

        if (gameObjects != null) {
            gameObjects.clear();
        }

        roomData = null;
        roomConnections = null;
        gameObjects = null;
        active = false;
    }

    public void syncObjectsBodies() {
        if (!active) return;

        for (GameObject object : gameObjects) {
            if (object instanceof MovableGameObject mObj && mObj.getPhysicsC() != null) {
                mObj.getPhysicsC().syncBodyObjectPos();
            }
        }
    }

    public void updateEntitiesRayCasts(){
        for(GameObject object : gameObjects){
            if(object instanceof Entity entity && entity.getRayCastHelper() != null){
                entity.updateRayCast();
            }
        }
    }

    public void updateObjects(float delta) {
        if (!active) return;

        if (gameObjects != null) {
            for (GameObject object : gameObjects) {
                object.update(delta);
            }
        }

    }


    public void render(SpriteBatch batch) {
        if (!active) return;

        if (gameObjects != null) {
            for (GameObject object : gameObjects) {
                object.render(batch);
            }
        }

    }

    public void dispose() {
        if (!active) return;

        if (gameObjects != null) {
            for (GameObject object : gameObjects) {
                object.dispose();
            }
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
