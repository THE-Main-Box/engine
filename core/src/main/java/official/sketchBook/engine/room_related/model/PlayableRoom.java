package official.sketchBook.engine.room_related.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import official.sketchBook.engine.components_related.integration_interfaces.JumpCapableII;
import official.sketchBook.engine.components_related.integration_interfaces.MovementCapableII;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.components_related.integration_interfaces.RayCasterII;
import official.sketchBook.engine.gameObject_related.base_model.GameObject;
import official.sketchBook.engine.gameObject_related.base_model.PhysicalGameObject;
import official.sketchBook.engine.projectileRelated.util.GlobalProjectilePool;
import official.sketchBook.engine.room_related.worldGeneration_related.connection.RoomNode;
import official.sketchBook.engine.util_related.pools.PolishDamageDataPool;
import official.sketchBook.engine.util_related.utils.body.RoomBodyDataConversor;
import official.sketchBook.engine.util_related.utils.registers.EmitterRegister;
import official.sketchBook.engine.util_related.utils.registers.PolishDamageDataPoolRegister;
import official.sketchBook.engine.util_related.utils.registers.ProjectilePoolRegister;

import java.util.ArrayList;
import java.util.List;

public class PlayableRoom implements Poolable {
    private Room roomData;
    private RoomNode roomConnections;

    /// Pool de objetos do tipo projétil
    private GlobalProjectilePool projectilePool;

    /// Pool de dados polidos de dano
    private PolishDamageDataPool polishDamagePool;


    /*
     *OBS: os objetos que estão fora da árvore de GameObjects,
     *precisam ter a sua própria estrutura interna detro desta classe para lidar com suas atualizações,
     *  renderização, etc.
     *  Escolha feita justamente para garantir um controle fino e preciso
     *  dos dados dos objetos de diferentes árvores de objetos
     */

    /// Mundo físico
    private World world;

    /// Lista de objetos existentes da árvore de gameObjects
    private List<GameObject> gameObjects;

    /// Lista de corpos nativos existentes dentro da sala
    private List<Body> nativeBodies;

    /// Determina se a sala está ativa e pode ser usada
    private boolean active;

    /// Inicialização padrão pra cada sala jogável
    public PlayableRoom(World world) {
        this.world = world;
        this.projectilePool = new GlobalProjectilePool(this);
        this.polishDamagePool = new PolishDamageDataPool(this);
        this.gameObjects = new ArrayList<>();
    }

    /**
     * Inicia a sala jogável, passando os dados da construção dela (blueprints e conexões)
     *
     * @param roomData        Objeto de dados a respeito da estrutura da sala a ser criada
     * @param roomConnections Conexões entre as salas existentes
     */
    public void initialize(Room roomData, RoomNode roomConnections) {
        this.roomData = roomData;
        this.roomConnections = roomConnections;
        this.nativeBodies = RoomBodyDataConversor.buildTileMergedBodies(roomData.getTiles(), this.world);

        //Registra a pool na inicialização
        ProjectilePoolRegister.register(this, this.projectilePool);
        PolishDamageDataPoolRegister.register(this, this.polishDamagePool);

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
        polishDamagePool.cleanPool();

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
        this.polishDamagePool = null;

    }

    /// Atualiza todos os objetos que precisam de uma atualização extra depois do step do world
    public void updateObjectsAfterStep() {
        if (!active || gameObjects == null || gameObjects.isEmpty()) return;

        for (GameObject object : gameObjects) {
            syncObjectBody(object);
            updateObjectsRayCast(object);
        }

        this.projectilePool.syncProjectilesBodies();
    }

    /// Sincroniza os objetos a seus corpos com a conversão para pixel e metro
    public void syncObjectBody(GameObject object) {
        if (object instanceof MovementCapableII mObj && mObj.getPhysicsC() != null) {
            mObj.getPhysicsC().syncBodyObjectPos();
            mObj.onObjectBodySync();
        }
    }

    /// Atualiza os ray casts de todos os objetos que possuem a capacidade de usar um rayCast
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

        if (polishDamagePool != null) {
            this.polishDamagePool.update(delta);
        }
    }


    /// Renderiza todos os objetos
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
        object.setOwnerRoom(this);
    }

    public void removeObject(GameObject object) {
        this.gameObjects.remove(object);
        object.setOwnerRoom(null);
        if (object instanceof RangeWeaponWielderII wielder && EmitterRegister.getEmitter(wielder) != null) {
            EmitterRegister.unregister(wielder);
        }
    }

    /*TODO:Adicionar meio para obter objeto com base no id em vez da classe do objeto,
     * para lidar com multiplas instancias
     */

    public <T> T getObjectInListByClass(Class<T> objectClass) {
        for (GameObject go : this.gameObjects) {
            if (objectClass.isInstance(go)) {
                return objectClass.cast(go); // cast seguro
            }
        }
        return null;
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

    public PolishDamageDataPool getPolishDamagePool() {
        return polishDamagePool;
    }
}
