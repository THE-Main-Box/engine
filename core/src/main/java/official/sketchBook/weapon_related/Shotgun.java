package official.sketchBook.weapon_related;

import com.badlogic.gdx.graphics.Texture;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.projectiles.SlugProjectile;
import official.sketchBook.projectiles_related.projectiles.TestProjectile;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.info.paths.WeaponsSpritePath;
import official.sketchBook.util_related.registers.EmitterRegister;

public class Shotgun extends RangeWeapon{

    public Shotgun(Entity owner) {
        this.owner = owner;
        this.projectileEmitter = EmitterRegister.getEmitter(owner);

        if (this.projectileEmitter == null) {
            throw new IllegalArgumentException("Entity must have an Emitter registered to use Shotgun");
        }

        updateProjectileIndex(1);
        this.initSpriteSheet();
    }

    private void initSpriteSheet(){
        this.spriteDataHandler = new SpriteSheetDataHandler(
            owner.getX(),
            owner.getY(),
            0,
            0,
            3,
            3,
            owner.isFacingForward(),
            false,
            new Texture(WeaponsSpritePath.shotgun_path)
        );
    }

    private void updateProjectileIndex(int projectileIndex){
        if(projectileIndex == 1){
            configProjectileTypeOnEmitter(SlugProjectile.class);
        } else if (projectileIndex == 2) {
            configProjectileTypeOnEmitter(TestProjectile.class);
        }
    }
}
