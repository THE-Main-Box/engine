package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.util_related.enumerators.directions.Direction;

public class DirectionHelper {
    private static final Vector2 delta = new Vector2();

    public static Direction getDirection(Vector2 origin, Vector2 target, float threshold) {
        delta.set(target).sub(origin);

        float dx = delta.x;
        float dy = delta.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > threshold ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > threshold ? Direction.UP : Direction.DOWN;
        }
    }
}
