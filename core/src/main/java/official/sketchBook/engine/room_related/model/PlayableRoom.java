package official.sketchBook.engine.room_related.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import official.sketchBook.engine.components_related.integration_interfaces.MovementCapableII;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.components_related.integration_interfaces.RayCasterII;
import official.sketchBook.engine.gameObject_related.base_model.GameObject;
import official.sketchBook.engine.gameObject_related.base_model.PhysicalGameObject;
import official.sketchBook.engine.projectileRelated.util.GlobalProjectilePool;
import official.sketchBook.engine.room_related.worldGeneration_related.connection.RoomNode;
import official.sketchBook.engine.util_related.utils.body.RoomBodyDataConversor;
import official.sketchBook.engine.util_related.utils.registers.EmitterRegister;
import official.sketchBook.engine.util_related.utils.registers.ProjectilePoolRegister;

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
        this.projectilePool = new GlobalProjectilePool(this);
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

    public void updateObjectsAfterStep() {
        if (!active || gameObjects == null || gameObjects.isEmpty()) return;

        for (GameObject object : gameObjects) {
            syncObjectBody(object);

            updateObjectsRayCast(object);
        }

        this.projectilePool.syncProjectilesBodies();
    }

    /// Sincroniza os corpos dos objetos com suas posições relativas
    public void syncObjectBody(GameObject object) {
        if (object instanceof MovementCapableII mObj && mObj.getPhysicsC() != null) {
            mObj.getPhysicsC().syncBodyObjectPos();
            mObj.onObjectBodySync();
        }
    }

    /// Atualiza os ray casts de todas as entidades que possuem a capacidade de usar um rayCast
    public void updateObjectsRayCast(GameObject object) {
        if (object instanceof RayCasterII castable && castable.getRayCastHelper() != null) {
            castable.updateRayCast();
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
            this.projectilePool.update(delta);
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

        if (projectilePool != null) {
            projectilePool.renderActiveProjectiles(batch);
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

    public void addObject(PhysicalGameObject object) {
        this.gameObjects.add(object);
        object.setOwnerRoom(this);
    }

    public void removeObject(PhysicalGameObject object) {
        this.gameObjects.remove(object);
        object.setOwnerRoom(null);
        if (object instanceof RangeWeaponWielderII wielder&& EmitterRegister.getEmitter(wielder) != null) {
            EmitterRegister.unregister(wielder);
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

    public GlobalProjectilePool getProjectilePool() {
        return projectilePool;
    }
}
