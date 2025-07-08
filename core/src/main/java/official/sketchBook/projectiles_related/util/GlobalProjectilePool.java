package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.room_related.model.PlayableRoom;

import java.util.HashMap;
import java.util.Map;

import static official.sketchBook.util_related.info.values.constants.ProjectileManagementConstants.*;

public class GlobalProjectilePool {

    /// Referência ao mundo físico
    private final World world;

    /// Mapa de pools de projéteis por tipo
    private final Map<Class<? extends Projectile>, ProjectilePool<? extends Projectile>> poolMap = new HashMap<>();

    /// Sala que possui essa pool global
    private final PlayableRoom roomOwner;

    /// Tempo de limpeza de pools
    private final TimerComponent poolCleanTimer;

    /// Tempo de destruição de pools
    private final TimerComponent poolDeleteTimer;

    public GlobalProjectilePool(PlayableRoom roomOwner) {
        this.world = roomOwner.getWorld();
        this.roomOwner = roomOwner;

        this.poolCleanTimer = new TimerComponent(cleanInterval);
        this.poolCleanTimer.start();

        this.poolDeleteTimer = new TimerComponent(deleteInterval);
        this.poolDeleteTimer.start();
    }

    /// Atualiza projéteis e realiza limpezas periódicas
    public void update(float delta) {
        if (poolCleanTimer.isFinished()) {
            cleanPools();
            poolCleanTimer.reset();
        }

        if (poolDeleteTimer.isFinished()) {
            deleteEmptyPools();
            poolDeleteTimer.reset();
        }

        //Atualizamos os temporizadores depois de tudo para garantir que não irá haver perda de tempo devido a
        //  um reset feito no tempo errado
        poolCleanTimer.update(delta);
        poolDeleteTimer.update(delta);

        updateProjectiles(delta);
    }

    /// Limpa os projéteis inativos de todas as pools
    private void cleanPools() {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            pool.destroyInactiveProjectiles();
        }
    }

    /// Limpa todas as pools sem projéteis ativos ou inativos
    private void deleteEmptyPools() {
        //Usa um iterador para percorrer os valores e eliminar os que devem ser eliminados
        var iterator = poolMap.entrySet().iterator();
        while (iterator.hasNext()) {
            //Obtemos o próximo valor com o iterador
            ProjectilePool<? extends Projectile> pool = iterator.next().getValue();

            //Verificamos se todas as listas, de ativos e inativos estão vazias antes de prosseguir
            if (pool.getActiveProjectiles().size ==0 && pool.getFreeCount() == 0) {
                iterator.remove();
            }
        }
    }


    /// Renderiza todos os projéteis ativos
    public void renderActiveProjectiles(SpriteBatch batch) {
        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            pool.renderAll(batch);
        }
    }

    /// Cria pool para o tipo especificado caso não exista
    @SuppressWarnings("unchecked")
    private <T extends Projectile> ProjectilePool<T> createPoolIfAbsent(Class<T> type) {
        return (ProjectilePool<T>) poolMap.computeIfAbsent(
            type,
            t -> new ProjectilePool<>(type, world)
        );
    }

    /// Retorna projétil requisitado, criando pool se necessário
    public <T extends Projectile> Projectile returnProjectileRequested(Class<T> type) {
        return createPoolIfAbsent(type).obtainFreeOrNew();
    }

    /// Atualiza lógica de todos os projéteis ativos
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

    public int getTotalActiveProjectiles() {
        int value = 0;

        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            value += pool.getActiveProjectiles().size;
        }

        return value;
    }

    public int getTotalWaitingProjectiles() {
        int value = 0;

        for (ProjectilePool<? extends Projectile> pool : poolMap.values()) {
            value += pool.getFreeCount();//Para cada pool adicionamos os valores da suas listas ativas aqui dentro
        }

        return value;
    }

    public int getTotalPools() {
        return poolMap.size();
    }

    /// Retorna sala que possui a pool
    public PlayableRoom getRoomOwner() {
        return roomOwner;
    }
}
