package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.projectiles_related.Projectile;

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

        destroyInactiveProjectiles();
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

    /// Destrói apenas projéteis que podem ser removidos permanentemente (estão inativos)
    public void destroyInactiveProjectiles() {
        int maxToDestroyPerFrame = 5; // limite por frame
        int count = Math.min(toDestroy.size, maxToDestroyPerFrame);

        for (int i = 0; i < count; i++) {
            toDestroy.get(i).destroy();
        }

        // remove só os destruídos
        for (int i = 0; i < count; i++) {
            toDestroy.removeIndex(0);
        }

        clear(); // limpa objetos reciclados da pool
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

        toDestroy.add((T) projectile);
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
}
