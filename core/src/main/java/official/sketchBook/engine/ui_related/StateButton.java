package official.sketchBook.engine.ui_related;

import official.sketchBook.engine.animation_related.Sprite;
import official.sketchBook.engine.util_related.enumerators.states.GameState;

public class StateButton extends Button {

    private GameState gameStateToChange;

    public StateButton(
        float xPos,
        float yPos,
        int buttonCanvasWidth,
        int buttonCanvasHeight,
        float multiplier,
        Sprite buttonSprite,
        GameState gameState,
        String type
    ) {
        super(xPos, yPos, buttonCanvasWidth, buttonCanvasHeight, multiplier, buttonSprite, type);
        this.gameStateToChange = gameState;
    }

    @Override
    public void mousePressedEvent() {
        applyGameState();
    }

    public void applyGameState() {
        if (gameStateToChange != null)
            GameState.state = this.gameStateToChange;
    }

    public GameState getGameStateToChange() {
        return gameStateToChange;
    }

    public void setGameStateToChange(GameState gameStateToChange) {
        this.gameStateToChange = gameStateToChange;
    }
}
