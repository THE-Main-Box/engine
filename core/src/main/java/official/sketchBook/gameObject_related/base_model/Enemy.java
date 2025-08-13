package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends DamageAbleEntity{
    public Enemy(float x, float y, float width, float height, boolean xAxisNormal,boolean yAxisNormal , World world) {
        super(x, y, width, height, xAxisNormal, yAxisNormal, world);
    }
}
