package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.util_related.enumerators.types.FactionTypes;

public abstract class Enemy extends Entity {
    public Enemy(float x, float y, float width, float height, boolean xAxisInverted,boolean yAxisInverted , World world) {
        super(x, y, width, height, xAxisInverted, yAxisInverted, world);
    }
}
