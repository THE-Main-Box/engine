package official.sketchBook.util_related.helpers;

import official.sketchBook.util_related.enumerators.directions.Direction;

public class DirectionHelper {
    public static Direction getDirection(float originX, float originY, float targetX, float targetY, float threshold) {
        float dx = targetX - originX;
        float dy = targetY - originY;

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > threshold ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > threshold ? Direction.UP : Direction.DOWN;
        }
    }
}
