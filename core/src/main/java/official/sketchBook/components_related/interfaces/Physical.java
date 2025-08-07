package official.sketchBook.components_related.interfaces;

import com.badlogic.gdx.physics.box2d.Body;

public interface Physical {
    Body getBody();
    float getX();
    float getY();

    float getWidth();
    float getHeight();

    void setX(float x);
    void setY(float y);
}
