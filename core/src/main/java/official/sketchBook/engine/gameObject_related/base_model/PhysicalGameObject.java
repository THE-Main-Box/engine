package official.sketchBook.engine.gameObject_related.base_model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.components_related.integration_interfaces.physic.PhysicalObjectII;

public abstract class PhysicalGameObject extends GameObject implements PhysicalObjectII {

    //physics related
    protected Body body;
    protected World world;

    protected float defFric, defRest, defDens;

    protected short maskBit;
    protected short categoryBit;

    public PhysicalGameObject(float x, float y, float width, float height, boolean xAxisInverted, boolean yAxisInverted, World world) {
        super(x, y, width, height, xAxisInverted, yAxisInverted);

        this.world = world;

        setBodyDefValues();
        createBody();

    }

    protected abstract void setBodyDefValues();

    // Métodos para inicializar a física e a renderização (implementados nas subclasses)
    protected abstract void createBody();

    public void dispose() {
        super.dispose();
    }

    public float getDefFric() {
        return defFric;
    }

    public float getDefRest() {
        return defRest;
    }

    public float getDefDens() {
        return defDens;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public short getCategoryBit() {
        return categoryBit;
    }

    public void setCategoryBit(short categoryBit) {
        this.categoryBit = categoryBit;
    }

    public short getMaskBit() {
        return maskBit;
    }

    public void setMaskBit(short maskBit) {
        this.maskBit = maskBit;
    }
}
