package official.sketchBook.gameState_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
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
import java.util.Arrays;
import java.util.List;

public class Paused extends State implements StateMethods {

    private List<StateButton> buttons = new ArrayList<>();

    public Paused(PlayScreen game, CameraManager gameCameraManager, CameraManager uiCameraManager) {
        super(game, gameCameraManager, uiCameraManager);
        mult = 2;
        initData();
    }

    private void initData() {
        initBackGround(
            UISpritePaths.pause_BG,
            2,
            1,
            PlayScreen.GAME_WIDTH / 2,
            PlayScreen.GAME_HEIGHT / 2
            );

        initIdleBackGroundAnimation();

        initButtons();
    }

    @Override
    protected void initIdleBackGroundAnimation() {
        this.aniPlayer = new ObjectAnimationPlayer();

        aniPlayer.addAnimation("idle", Arrays.asList(
            new Sprite(0, 0, 500),
            new Sprite(1, 0, 500)
        ));

        aniPlayer.setAnimation("idle");
    }

    private void initButtons() {
        int canvasWidth = 20;
        int canvasHeight = 20;

        int width = (int) (canvasWidth * mult);

        int xOffSet = (int) (width + 9 * mult);

        int x = (int) (menuX + 31 * mult);
        int y = (int) (menuY + 11* mult);


        buttons.add(
            new StateButton(
                x,
                y,
                canvasWidth,
                canvasHeight,
                mult,
                new Sprite(0, 2),
                GameState.CONFIGURATION,
                "configurations"
            )
        );
        buttons.add(
            new StateButton(
                x + xOffSet,
                y,
                canvasWidth,
                canvasHeight,
                mult,
                new Sprite(0, 1),
                GameState.MENU,
                "menu"
            )
        );
        buttons.add(
            new StateButton(
                x + xOffSet * 2,
                y,
                canvasWidth,
                canvasHeight,
                mult,
                new Sprite(0, 0),
                GameState.PLAYING,
                "return"
            )
        );

        for (Button button : buttons) {
            button.buttonSpriteSheet = new Texture(UISpritePaths.pause_BTNs);
        }
    }

    @Override
    public void update(float delta) {
//        resetEntitysMovement();
        updateUi(delta);
    }

    @Override
    public void updateUi(float delta) {
        aniPlayer.update(delta);

        for (Button button : buttons) {
            button.update();
        }
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
    public void render(SpriteBatch batch) {
        game.getPlayingState().render(batch);
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
                    game.getConfigState().previousState = GameState.PAUSED;
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
