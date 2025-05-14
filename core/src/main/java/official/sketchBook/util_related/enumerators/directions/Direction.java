package official.sketchBook.util_related.enumerators.directions;

public enum Direction {
    UP,
    LEFT,
    DOWN,
    RIGHT,
    STILL,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT,
    UP_DOWN,
    LEFT_RIGHT;

    public boolean isDown() {
        return this == DOWN || this == DOWN_LEFT || this == DOWN_RIGHT;
    }

    public boolean isUp() {
        return this == UP || this == UP_LEFT || this == UP_RIGHT;
    }

    public boolean isLeft() {
        return this == LEFT || this == DOWN_LEFT || this == UP_LEFT;
    }

    public boolean isRight() {
        return this == RIGHT || this == DOWN_RIGHT || this == UP_RIGHT;
    }

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case UP_LEFT -> DOWN_RIGHT;
            case UP_RIGHT -> DOWN_LEFT;
            case DOWN_LEFT -> UP_RIGHT;
            case DOWN_RIGHT -> UP_LEFT;
            case UP_DOWN -> LEFT_RIGHT;
            case LEFT_RIGHT -> UP_DOWN;
            case STILL -> STILL;
        };
    }
}
