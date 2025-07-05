package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.projectiles_related.Projectile;

import static official.sketchBook.util_related.info.values.constants.ProjectileManagementConstants.maxToDestroyPerFrame;

public class ProjectilePool<T extends Projectile> extends Pool<T> {

    /// Referência ao mundo físico para criação dos projéteis
    private final World world;
    private final Class<T> type;
    private final Array<T> toDestroy;
    private final Array<T> activeProjectiles;

    public ProjectilePool(Class<T> type, World world) {
        this.type = type;
        this.world = world;
        this.toDestroy = new Array<>();
        this.activeProjectiles = new Array<>();
    }

    /// Atualiza todos os projéteis ativos
    public void updateAll(float delta) {
        for (T proj : activeProjectiles) {
            proj.update(delta);
        }
    }

    /// Renderiza todos os projéteis ativos
    public void renderAll(SpriteBatch batch) {
        for (T proj : activeProjectiles) {
            proj.render(batch);
        }
    }

    public void destroyAllProjectiles() {
        for (int i = 0; i < activeProjectiles.size; i++) {
            activeProjectiles.get(i).release();
        }
        destroyInactiveProjectiles();
    }

    public void destroyInactiveProjectiles() {
        if (world.isLocked()) return;//Valida se o world permite destruir

        int count = 0;
        for (int i = toDestroy.size - 1; i >= 0 && count < maxToDestroyPerFrame; i--) {

            toDestroy.get(i).destroy();
            toDestroy.removeIndex(i);
            count++;
        }

        clear(); // limpa objetos reciclados da pool
        for (T proj : toDestroy) {
            free(proj); // recoloca só os válidos destruídos
        }
    }

    /// Cria um projétil e adiciona à lista de controle
    @Override
    protected T newObject() {
        return ProjectileFactory.createByType(type, world);
    }

    /// Retorna um projétil livre ou cria um se necessário
    public T getFreeOrNew() {
        T proj = obtain();
        proj.setOwnerPool(this);
        toDestroy.removeValue(proj, true);
        return proj;
    }

    @SuppressWarnings("unchecked")
    public void free(Projectile projectile) {
        super.free((T) projectile);

        if (!toDestroy.contains((T) projectile, true)) {
            toDestroy.add((T) projectile);
        }

        activeProjectiles.removeValue((T) projectile, true);
    }

    @SuppressWarnings("unchecked")
    public void addToActive(Projectile proj) {
        this.activeProjectiles.add((T) proj);
    }

    public Array<T> getActiveProjectiles() {
        return activeProjectiles;
    }

    public Array<T> getToDestroy() {
        return toDestroy;
    }

    public Class<T> getType() {
        return type;
    }
}
