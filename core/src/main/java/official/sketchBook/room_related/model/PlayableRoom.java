package official.sketchBook.room_related.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.gameObject_related.MovableGameObject;
import official.sketchBook.projectiles_related.util.GlobalProjectilePool;
import official.sketchBook.room_related.worldGeneration_related.connection.RoomNode;
import official.sketchBook.util_related.helpers.body.RoomBodyDataConversor;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.util_related.registers.ProjectilePoolRegister;

import java.util.ArrayList;
import java.util.List;

public class PlayableRoom implements Poolable {
    private Room roomData;
    private RoomNode roomConnections;
    private GlobalProjectilePool projectilePool;

    private World world;

    private List<GameObject> gameObjects;
    private List<Body> nativeBodies;

    private boolean active;

    public PlayableRoom(World world) {
        this.world = world;
        this.projectilePool = new GlobalProjectilePool(world, this);
        this.gameObjects = new ArrayList<>();
    }

    public void initialize(Room roomData, RoomNode roomConnections) {
        this.roomData = roomData;
        this.roomConnections = roomConnections;
        this.nativeBodies = RoomBodyDataConversor.buildTileMergedBodies(roomData.getTiles(), this.world);

        //Registra a pool na inicialização
        ProjectilePoolRegister.register(this, this.projectilePool);

        this.active = true;
    }

    /// Reseta a sala e a prepara para se tornar outra
    public void reset() {
        this.dispose();//limpa dados

        if (nativeBodies != null) { //limpa e destrói todas as bodies nativas
            for (Body body : nativeBodies) {
                world.destroyBody(body);
            }
            nativeBodies.clear();
        }

        if (gameObjects != null) { //limpa a lista de gameObjects
            gameObjects.clear();
        }

        ProjectilePoolRegister.unregister(this);//Remove a sala dos registros para evitar uso indevido
        EmitterRegister.unregisterRoom(this);

        projectilePool.killPool();//destrói todos os projéteis dentro da pool global

        roomData = null;//limpa os dados da sala
        roomConnections = null; //limpam os dados da conexão
        active = false; //desativa a sala
    }

    /// Limpa e elimina todos os valores dentro da sala
    public void destroy() {
        reset();

        this.world = null;
        this.nativeBodies = null;
        this.gameObjects = null;
        this.projectilePool = null;

    }

    /// Sincroniza os corpos dos objetos com suas posições relativas
    public void syncObjectsBodies() {
        if (!active || gameObjects == null) return;

        for (GameObject object : gameObjects) {
            if (object instanceof MovableGameObject mObj && mObj.getPhysicsC() != null) {
                mObj.getPhysicsC().syncBodyObjectPos();
                mObj.updateSpritePos();
            }
        }

        this.projectilePool.syncProjectilesBodies();
    }

    /// Atualiza os ray casts de todas as entidades que possuem a capacidade de usar um rayCast
    public void updateEntitiesRayCasts() {
        if (!active || gameObjects == null) return;

        for (GameObject object : gameObjects) {
            if (object instanceof Entity entity && entity.getRayCastHelper() != null) {
                entity.updateRayCast();
            }
        }
    }

    /// Atualiza todos os objetos
    public void updateObjects(float delta) {
        if (!active) return;

        if (gameObjects != null) {
            for (GameObject object : gameObjects) {
                object.update(delta);
            }
        }

        if (projectilePool != null) {
            this.projectilePool.updateProjectiles(delta);
        }
    }


    /// Renderiza todos os GameObjects
    public void render(SpriteBatch batch) {
        if (!active) return;

        if (gameObjects != null) {
            for (GameObject object : gameObjects) {
                object.render(batch);
            }
        }

    }

    /// Realiza um dispose de tudo caso estejamos ativos
    public void dispose() {
        if (!active) return;

        if (gameObjects != null) {
            for (GameObject object : gameObjects) {
                object.dispose();
            }
        }

        if (projectilePool != null) {
            projectilePool.dispose();
        }

    }

//    public void addNativeBody(Body body) {
//        this.nativeBodies.add(body);
//    }
//
//    public void removeNativeBody(Body body) {
//        if (this.nativeBodies.remove(body)) {
//            this.world.destroyBody(body);
//        }
//    }

    public void addObject(GameObject object) {
        this.gameObjects.add(object);
    }

    public void removeObject(GameObject object) {
        this.gameObjects.remove(object);
        if (object instanceof Entity entity) {
            EmitterRegister.unregister(entity);
        }
    }

    public boolean isActive() {
        return active;
    }

    public Room getRoomData() {
        return roomData;
    }

    public RoomNode getRoomConnections() {
        return roomConnections;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public World getWorld() {
        return world;
    }
}
