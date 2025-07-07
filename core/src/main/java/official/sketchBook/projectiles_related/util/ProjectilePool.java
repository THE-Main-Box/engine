package official.sketchBook.projectiles_related.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import official.sketchBook.customComponents_related.CustomPool;
import official.sketchBook.projectiles_related.Projectile;

import static official.sketchBook.util_related.info.values.constants.ProjectileManagementConstants.maxToDestroyPerFrame;

public class ProjectilePool<T extends Projectile> extends CustomPool<T> {

    /// Referência ao mundo físico para criação dos projéteis
    private final World world;
    private final Class<T> type;
    private final Array<T> activeProjectiles;

    public ProjectilePool(Class<T> type, World world) {
        this.type = type;
        this.world = world;
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
        destroyAllInactiveProjectiles();
    }

    /// Destrói todos os projéteis inativos ainda existente
    public void destroyAllInactiveProjectiles(){
        super.clear();
    }

    /// Destruímos os projéteis inativos com um limite de contagem
    public void destroyInactiveProjectiles() {
        if (world.isLocked()) return;//Valida se o world permite destruir

        /*Percorremos a lista de cima pra baixo com um contador para evitar de iterar mais do que preciso*/
        int count = 0;
        for(int i = getFreeCount() -1; i >= 0 && count < maxToDestroyPerFrame; i--){
            super.discard(freeObjects.get(i));
            count ++;
        }
    }

    /// Cria um projétil e adiciona à lista de controle
    @Override
    protected T newObject() {
        return ProjectileFactory.createByType(type, world);
    }

    /// Acessamos um valor desejado e iniciar os valores necessários
    public T obtainFreeOrNew() {
        T proj = obtain();
        proj.setOwnerPool(this);
        return proj;
    }

    /// Removemos da lista de projéteis ativos e realocamos para dentro da lista de inativos
    @SuppressWarnings("unchecked")
    public void free(Projectile projectile) {
        super.free((T) projectile);
        activeProjectiles.removeValue((T) projectile, true);
    }

    @SuppressWarnings("unchecked")
    public void addToActive(Projectile proj) {
        this.activeProjectiles.add((T) proj);
    }

    public Array<T> getActiveProjectiles() {
        return activeProjectiles;
    }

    public Class<T> getType() {
        return type;
    }
}
