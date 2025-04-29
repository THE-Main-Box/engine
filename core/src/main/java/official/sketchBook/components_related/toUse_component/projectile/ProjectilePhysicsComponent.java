package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.components_related.base_component.BasePhysicsComponent;
import official.sketchBook.gameObject_related.GameObject;

public class ProjectilePhysicsComponent extends BasePhysicsComponent {
    private boolean affectedByGravity = false;

    public ProjectilePhysicsComponent(GameObject object) {
        super(object, object.getBody());
    }

    @Override
    public void update(float delta) {

    }

    public void setAffectedByGravity(boolean affected) {
        if (body != null) {
            body.setGravityScale(affected ? 1f : 0f);
        }
        this.affectedByGravity = affected;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }
}
