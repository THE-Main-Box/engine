package official.sketchBook.input_related;

import com.badlogic.gdx.InputProcessor;
import official.sketchBook.PlayScreen;
import official.sketchBook.util_related.enumerators.states.GameState;

public class InputHandler implements InputProcessor {

    private PlayScreen playScreen;

    public InputHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    public boolean keyDown(int i) {

        switch (GameState.state) {
            case PLAYING -> {
                playScreen.getPlayingState().handleKeyDown(i);
            }
            case MENU -> {

                playScreen.getMenuState().handleKeyDown(i);

            }
            case PAUSED -> {

                playScreen.getPausedState().handleKeyDown(i);

            }
            case CONFIGURATION -> {

                playScreen.getConfigState().handleKeyDown(i);

            }
            default -> {
            }

        }

        return true;
    }

    @Override
    public boolean keyUp(int i) {

        switch (GameState.state) {
            case PLAYING -> {
                playScreen.getPlayingState().handleKeyUp(i);
            }
            case MENU -> {

                playScreen.getMenuState().handleKeyUp(i);

            }
            case PAUSED -> {

                playScreen.getPausedState().handleKeyUp(i);

            }
            case CONFIGURATION -> {

                playScreen.getConfigState().handleKeyUp(i);

            }
            default -> {
            }

        }

        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {

        // screenX: a coordenada X do toque ou clique na tela (em pixels).
        // screenY: a coordenada Y do toque ou clique na tela (em pixels).
        // pointer: o identificador do dedo/touch quando em dispositivos de toque (geralmente 0 para o primeiro toque).
        // button: o botão pressionado (para mouse). Por exemplo, Input.Buttons.LEFT para o botão esquerdo.

        switch (GameState.state) {
            case PLAYING -> {
                playScreen.getPlayingState().handleTouchDown(i, i1, i3);
            }
            case MENU -> {

                playScreen.getMenuState().handleTouchDown(i, i1, i3);

            }
            case PAUSED -> {

                playScreen.getPausedState().handleTouchDown(i, i1, i3);

            }
            case CONFIGURATION -> {

                playScreen.getConfigState().handleTouchDown(i, i1, i3);

            }
            default -> {
            }

        }

        return true;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {

        // screenX: a coordenada X do toque ou clique na tela (em pixels).
        // screenY: a coordenada Y do toque ou clique na tela (em pixels).
        // pointer: o identificador do dedo/touch quando em dispositivos de toque (geralmente 0 para o primeiro toque).
        // button: o botão pressionado (para mouse). Por exemplo, Input.Buttons.LEFT para o botão esquerdo.

        switch (GameState.state) {
            case PLAYING -> {
                playScreen.getPlayingState().handleTouchUp(i, i1, i3);
            }
            case MENU -> {

                playScreen.getMenuState().handleTouchUp(i, i1, i3);

            }
            case PAUSED -> {

                playScreen.getPausedState().handleTouchUp(i, i1, i3);

            }
            case CONFIGURATION -> {

                playScreen.getConfigState().handleTouchUp(i, i1, i3);

            }
            default -> {
            }

        }

        return true;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {

        if(GameState.state == GameState.CONFIGURATION){
            playScreen.getConfigState().handleTouchDragged(i, i1, i2);
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        switch (GameState.state) {
            case PLAYING -> {
                playScreen.getPlayingState().handleMouseMoved(screenX, screenY);
            }
            case MENU -> {

                playScreen.getMenuState().handleMouseMoved(screenX, screenY);

            }
            case PAUSED -> {

                playScreen.getPausedState().handleMouseMoved(screenX, screenY);

            }
            case CONFIGURATION -> {

                playScreen.getConfigState().handleMouseMoved(screenX, screenY);

            }
            default -> {
            }

        }
        return true;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }


}
