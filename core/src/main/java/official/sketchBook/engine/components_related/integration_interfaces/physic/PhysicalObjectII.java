package official.sketchBook.engine.components_related.integration_interfaces.physic;

import com.badlogic.gdx.physics.box2d.Body;

public interface PhysicalObjectII {
    Body getBody();
    float getX();
    float getY();

    float getWidth();
    float getHeight();

    void setX(float x);
    void setY(float y);
}
