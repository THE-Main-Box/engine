package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.room_related.model.PlayableRoom;

import java.util.HashMap;
import java.util.Map;

public class GlobalProjectilePool {

    /// Mundo físico
    private final World world;
    /// Mapa onde está um conjunto de referências a pools com base no tipo de projétil que elas possuem como tipo
    private final Map<Class<? extends Projectile>, ProjectilePool<? extends Projectile>> poolMap = new HashMap<>();
    /// Sala dona da pool global
    private final PlayableRoom roomOwner;

    public GlobalProjectilePool(World world, PlayableRoom roomOwner) {
        this.world = world;
        this.roomOwner = roomOwner;
    }

    @SuppressWarnings("unchecked")
    public <T extends Projectile> Projectile returnProjectileRequested(Class<T> type) {
        return createPoolIfAbsent(type).getFreeOrNew();
    }

    private float cleanTimer = 0f;
    private final float CLEAN_INTERVAL = 0.4f; // a cada 400ms

    public void update(float delta) {
        cleanTimer += delta;
        if (cleanTimer >= CLEAN_INTERVAL) {
            cleanEmptyPools();
            cleanTimer = 0f;
        }

        updateProjectiles(delta);
    }

    /**
     * Adiciona uma pool nova caso não exista
     *
     * @param type tipo de projétil que queremos criar e adicionar
     */
    @SuppressWarnings("unchecked")
    private <T extends Projectile> ProjectilePool<T> createPoolIfAbsent(Class<T> type) {
        //valida se o objeto existe, então se não existir, a partir de um callBack adicionamos uma chave e um objeto
        return (ProjectilePool<T>) poolMap.computeIfAbsent(
            type,
            t -> new ProjectilePool<>(type, world)
        );
    }

    /// Limpa todas as pools ainda existentes
    public void killPool() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            pool.destroyAllProjectiles();
        }
    }

    public void syncProjectilesBodies() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            for (Projectile p : pool.getAllProjectiles()) {
                if (!p.isActive()) continue;

                p.getControllerComponent()
                    .getProjectile().getPhysicsComponent()
                    .syncBodyObjectPos();
            }
        }
    }

    public void updateProjectiles(float delta) {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            for (Projectile p : pool.getAllProjectiles()) {
                if (!p.isActive()) continue;

                p.update(delta);
            }
        }
    }

    public void cleanEmptyPools() {
        poolMap.entrySet().removeIf(entry -> {
            ProjectilePool<? extends Projectile> pool = entry.getValue();
            pool.destroyInactiveProjectiles();
            return pool.getAllProjectiles().isEmpty();
        });
    }

    public void dispose() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            for (Projectile proj : pool.getAllProjectiles()){
                proj.dispose();
            }
        }
    }

    public PlayableRoom getRoomOwner() {
        return roomOwner;
    }
}
