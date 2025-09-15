package official.sketchBook.engine.util_related.enumerators.directions;

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

    /// Verifica se tem a direção baixo
    public boolean isDown() {
        return this == DOWN || this == DOWN_LEFT || this == DOWN_RIGHT;
    }

    /// Verifica se tem a direção cima
    public boolean isUp() {
        return this == UP || this == UP_LEFT || this == UP_RIGHT;
    }

    /// Verifica se tem a direção esquerda
    public boolean isLeft() {
        return this == LEFT || this == DOWN_LEFT || this == UP_LEFT;
    }

    /// Verifica se possui a direção direita
    public boolean isRight() {
        return this == RIGHT || this == DOWN_RIGHT || this == UP_RIGHT;
    }

    /// Verifica se é uma diagonal
    public boolean isDiagonal() {
        return this == DOWN_LEFT || this == DOWN_RIGHT || this == UP_RIGHT || this == UP_LEFT;
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
