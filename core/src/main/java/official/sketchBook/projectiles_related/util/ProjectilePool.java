package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.projectiles_related.Projectile;

public class ProjectilePool<T extends Projectile> extends Pool<T> {
    private final Class<T> type;
    private final World world;
    private final Array<T> allProjectiles;


    public ProjectilePool(Class<T> type, World world) {
        this.type = type;
        this.world = world;
        this.allProjectiles = new Array<>();
    }

    public void recycleInactiveProjectiles() {
        for (int i = allProjectiles.size - 1; i >= 0; i--) {
            T projectile = allProjectiles.get(i);

            if (projectile.shouldBeRecycled()) {
                // Destruir recursos associados
                projectile.destroy();
                allProjectiles.removeIndex(i);
            }
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

    public Array<T> getAllProjectiles() {
        return allProjectiles;
    }
}
