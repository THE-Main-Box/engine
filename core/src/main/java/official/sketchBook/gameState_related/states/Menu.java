package official.sketchBook.gameState_related.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.gameState_related.model.State;
import official.sketchBook.gameState_related.model.StateMethods;
import official.sketchBook.ui_related.Button;
import official.sketchBook.ui_related.StateButton;
import official.sketchBook.util_related.enumerators.states.GameState;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.info.paths.UISpritePaths;

import java.util.ArrayList;
import java.util.List;

public class Menu extends State implements StateMethods {

    private List<StateButton> buttons = new ArrayList<>();

    public Menu(PlayScreen game, CameraManager gameCameraManager, CameraManager uiCameraManager) {
        super(game, gameCameraManager, uiCameraManager);

        this.mult = 2;
        initData();
    }


    private void initData() {
        initBackGround(
            UISpritePaths.menu_BG,
            1,
            1,
            PlayScreen.GAME_WIDTH / 2,
            PlayScreen.GAME_HEIGHT / 2
        );
        initButtons();
    }

    private void initButtons() {
        int canvasWidth = 50;
        int canvasHeight = 25;

        int height = (int) (canvasHeight * mult);

        int yOffSet = (int) (height + 12 * mult);

        int x = (int) (menuX + 45 * mult);
        int y = (int) (menuY + 13 * mult);


        buttons.add(
            new StateButton(
                x,
                y + yOffSet * 2,
                canvasWidth,
                canvasHeight,
                mult,
                new Sprite(0, 0),
                GameState.PLAYING,
                "play"
            )
        );
        buttons.add(
            new StateButton(
                x,
                y + yOffSet,
                canvasWidth,
                canvasHeight,
                mult,
                new Sprite(0, 1),
                GameState.CONFIGURATION,
                "configuration"
            )
        );
        buttons.add(
            new StateButton(
                x,
                y,
                canvasWidth,
                canvasHeight,
                mult,
                new Sprite(0, 2),
                GameState.QUIT,
                "quit"
            )
        );

        for (Button button : buttons) {
            button.buttonSpriteSheet = new Texture(UISpritePaths.menu_BTNs);
        }
    }

    @Override
    public void update(float delta) {
//        game.getPausedState().resetEntitysMovement();
//        game.getPlayingState().getLevelManager().unloadCurrentPlayerAreaAndLevel();

        updateUi(delta);
    }

    @Override
    public void updateUi(float delta) {

        for (Button button : buttons) {
            button.update();
        }
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public void dispose() {
        super.dispose();

        for (Button button : buttons) {
            if (button.buttonSpriteSheet != null) {
                button.buttonSpriteSheet.dispose();  // Dispose the texture used by the buttons
            }
        }
    }

    @Override
    public void renderUi(SpriteBatch uiBatch) {
        //caso tenhamos um tocador de animações usamos ele para obter a sprite que deve ser renderizado atualmente
        uiBatch.draw(
            HelpMethods.obtainCurrentSpriteImage(
                aniPlayer.getCurrentSprite(),
                canvasWidth,
                canvasHeight,
                backGroundImage,
                false
            ),
            menuX,
            menuY,
            menuWidth,
            menuHeight
        );


        for (Button button : buttons) {
            button.render(uiBatch);
        }
    }

    @Override
    public boolean handleMouseMoved(int screenX, int screenY) {
        for (Button button : buttons) {
            button.setMouseOver(isMouseInsideButton(screenX, screenY, button));
            button.setMousePressed(false);
        }
        return true;
    }

    @Override
    public boolean handleTouchDown(int screenX, int screenY, int button) {
        for (Button b : buttons) {
            if (b.isMouseOver()) {
                b.setMousePressed(true);
                b.setMouseOver(false);
                break;
            }
        }

        return true;
    }

    @Override
    public boolean handleTouchUp(int screenX, int screenY, int button) {
        for (Button b : buttons) {
            if (b.isMousePressed()) {
                b.mousePressedEvent();
                b.resetBools();

                if (b instanceof StateButton stateB && stateB.getGameStateToChange() == GameState.CONFIGURATION) {
                    game.getConfigState().previousState = GameState.MENU;
                }

            } else {
                b.resetBools();
            }
        }

        return true;
    }

    @Override
    public boolean handleKeyDown(int keycode) {
        return false;
    }

    @Override
    public boolean handleKeyUp(int keycode) {
        return false;
    }
}
