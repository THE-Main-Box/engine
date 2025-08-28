package official.sketchBook.gameObject_related.entities;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.base_model.Enemy;

public class Dummy extends Enemy {
    public Dummy(float x, float y, float width, float height, boolean xAxisNormal, boolean yAxisNormal, World world) {
        super(x, y, width, height, xAxisNormal, yAxisNormal, world);
    }

    @Override
    protected void setBodyDefValues() {

    }

    @Override
    protected void createBody() {

    }

    @Override
    public void onEnterNewRoom() {

    }
}
