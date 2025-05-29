package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.projectiles_related.Projectile;

import java.util.ArrayList;
import java.util.List;

public class ProjectilePool<T extends Projectile> extends Pool<T> {
    private final Class<T> type;
    private final World world;
    private final List<T> allProjectiles;

    public ProjectilePool(Class<T> type, World world) {
        this.type = type;
        this.world = world;
        this.allProjectiles = new ArrayList<>();
    }

    /// Destrói todos os projéteis inativos
    public void destroyInactiveProjectiles() {
        for (int i = allProjectiles.size() - 1; i >= 0; i--) {
            T projectile = allProjectiles.get(i);

            if (projectile.shouldBeDestroyedPermanently()) {
                // Destruir recursos associados
                projectile.destroy();
                allProjectiles.remove(i);
            }
        }
    }

    //destrói todos os projéteis existentes dentro da pool
    public void destroyAllProjectiles() {
        for (int i = allProjectiles.size() - 1; i >= 0; i--) {
            T projectile = allProjectiles.get(i);

            projectile.destroy();
            allProjectiles.remove(i);

        }

    }

    @Override
    protected T newObject() {
        T projectile = ProjectileFactory.createByType(type, world);
        allProjectiles.add(projectile);
        return projectile;
    }

    public T getFreeOrNew() {
        // Busca um projétil desativado
        for (T projectile : allProjectiles) {
            if (!projectile.isActive()) {
                return projectile;
            }
        }
        // Se nenhum estiver disponível, criamos um
        return obtain();
    }

    public List<T> getAllProjectiles() {
        return allProjectiles;
    }
}
