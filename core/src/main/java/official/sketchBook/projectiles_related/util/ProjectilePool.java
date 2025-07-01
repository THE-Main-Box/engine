package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.projectiles_related.Projectile;

import java.util.ArrayList;
import java.util.List;

public class ProjectilePool<T extends Projectile> extends Pool<T> {
    /**
     * Guarda o tipo de projétil gerenciado por essa pool
     */
    private final Class<T> type;
    /**
     * Referência ao mundo físico para criação dos projéteis
     */
    private final World world;
    /**
     * Lista com todos os projéteis já criados por essa pool
     */
    private final List<T> allProjectiles;

    public ProjectilePool(Class<T> type, World world) {
        this.type = type;
        this.world = world;
        this.allProjectiles = new ArrayList<>();
    }

    /**
     * Destrói apenas projéteis que devem ser removidos permanentemente
     */
    public void destroyInactiveProjectiles() {
        for (int i = allProjectiles.size() - 1; i >= 0; i--) {
            T projectile = allProjectiles.get(i);
            if (
                projectile.getWorld() != null
                    && !projectile.getWorld().isLocked()
                    && !projectile.isActive()
            ) {
                projectile.destroy();
                allProjectiles.remove(i);
            }
        }
    }

    /**
     * Destrói todos os projéteis ativos e limpa a lista
     */
    public void destroyAllProjectiles() {
        for (int i = allProjectiles.size() - 1; i >= 0; i--) {
            T projectile = allProjectiles.get(i);
            projectile.destroy();
            allProjectiles.remove(i);
        }
    }

    /**
     * Cria um projétil e adiciona à lista de controle
     */
    @Override
    protected T newObject() {
        T projectile = ProjectileFactory.createByType(type, world);
        allProjectiles.add(projectile);
        return projectile;
    }

    /**
     * Retorna um projétil livre ou cria um se necessário
     */
    public T getFreeOrNew() {
        for (T projectile : allProjectiles) {
            if (!projectile.isActive()) {
                return projectile;
            }
        }
        return obtain();
    }

    /**
     * Retorna a lista com todos os projéteis gerenciados
     */
    public List<T> getAllProjectiles() {
        return allProjectiles;
    }
}
