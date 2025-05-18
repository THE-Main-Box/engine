package official.sketchBook.input_related;

import com.badlogic.gdx.InputProcessor;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.states.GameState;

public class InputHandler implements InputProcessor {

    private PlayScreen playScreen;

    public InputHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        return playScreen.handleKeyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return playScreen.handleKeyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        // Não utilizado por enquanto
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return playScreen.handleTouchDown(screenX, screenY, button);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return playScreen.handleTouchUp(screenX, screenY, button);
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // Evento não utilizado no momento
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return playScreen.handleTouchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return playScreen.handleMouseMoved(screenX, screenY);

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Evento de rolagem do mouse, para uso futuro
        return false;
    }

}
