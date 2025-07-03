package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.room_related.model.PlayableRoom;

import java.util.HashMap;
import java.util.Map;

public class GlobalProjectilePool {
    /**
     * Referência ao mundo físico
     */
    private final World world;
    /**
     * Mapa de pools de projéteis por tipo
     */
    private final Map<Class<? extends Projectile>, ProjectilePool<? extends Projectile>> poolMap = new HashMap<>();
    /**
     * Sala que possui essa pool global
     */
    private final PlayableRoom roomOwner;

    public GlobalProjectilePool(World world, PlayableRoom roomOwner) {
        this.world = world;
        this.roomOwner = roomOwner;
    }

    /**
     * Timer para controle de limpeza periódica
     */
    private float cleanTimer = 0f;
    /**
     * Intervalo entre limpezas de pools
     */
    private final float CLEAN_INTERVAL = 1f;

    /**
     * Atualiza projéteis e realiza limpezas periódicas
     */
    public void update(float delta) {
        cleanTimer += delta;
        if (cleanTimer >= CLEAN_INTERVAL) {
            cleanTimer = 0f;
        }
        updateProjectiles(delta);
    }

    /**
     * Renderiza todos os projéteis ativos
     */
    public void renderActiveProjectiles(SpriteBatch batch) {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            pool.renderAll(batch);
        }
    }

    /**
     * Cria pool para o tipo especificado caso não exista
     */
    @SuppressWarnings("unchecked")
    private <T extends Projectile> ProjectilePool<T> createPoolIfAbsent(Class<T> type) {
        return (ProjectilePool<T>) poolMap.computeIfAbsent(
            type,
            t -> new ProjectilePool<>(type, world)
        );
    }

    /**
     * Retorna projétil requisitado, criando pool se necessário
     */
    @SuppressWarnings("unchecked")
    public <T extends Projectile> Projectile returnProjectileRequested(Class<T> type) {
        return createPoolIfAbsent(type).getFreeOrNew();
    }

    /**
     * Atualiza lógica de todos os projéteis ativos
     */
    private void updateProjectiles(float delta) {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            pool.updateAll(delta);
        }
    }

    /// Destrói todos os projéteis de todas as pools
    public void killPool() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            pool.destroyAllProjectiles();
        }
    }

    /// Sincroniza posição física dos projéteis ativos
    public void syncProjectilesBodies() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            for (Projectile p : pool.getActiveProjectiles()) {
                p.getControllerComponent()
                    .getProjectile().getPhysicsComponent()
                    .syncBodyObjectPos();
            }
        }
    }

    /// Libera recursos de todos os projéteis
    public void dispose() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            for (Projectile proj : pool.getActiveProjectiles()) {
                proj.dispose();
            }
        }
    }

    /// Retorna sala que possui a pool
    public PlayableRoom getRoomOwner() {
        return roomOwner;
    }

    public int getTotalActiveProjectiles() {
        int value = 0;

        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            value = pool.getActiveProjectiles().size;
        }

        return value;
    }

    public int getTotalWaitingProjectiles() {
        int value = 0;

        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            value = pool.getToDestroy().size;
        }

        return value;
    }

    public int getTotalPools() {
        return poolMap.size();
    }
}
