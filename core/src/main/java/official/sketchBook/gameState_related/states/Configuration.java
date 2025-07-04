package official.sketchBook.gameState_related.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.gameState_related.model.State;
import official.sketchBook.gameState_related.model.StateMethods;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.ui_related.Button;
import official.sketchBook.ui_related.On_OffButton;
import official.sketchBook.ui_related.Slider;
import official.sketchBook.ui_related.StateButton;
import official.sketchBook.util_related.enumerators.states.GameState;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.info.paths.UISpritePaths;

import java.util.ArrayList;
import java.util.List;

import static official.sketchBook.screen_related.PlayScreen.GAME_HEIGHT;
import static official.sketchBook.screen_related.PlayScreen.GAME_WIDTH;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Sound.*;

public class Configuration extends State implements StateMethods {

    private List<Button> buttons = new ArrayList<>();
    private List<Slider> sliders = new ArrayList<>();

    public GameState previousState;

    public Configuration(PlayScreen game, CameraManager gameCameraManager, CameraManager uiCameraManager) {
        super(game, gameCameraManager, uiCameraManager);
        this.multi = 2;
        initData();
    }

    private void initData() {
        this.initBackGround(
            UISpritePaths.config_BG,
            1,
            1,
            GAME_WIDTH / 2,
            GAME_HEIGHT / 2
        );
        this.initButtons();
        this.initSliders();
    }


    private void initButtons() {
        initReturnButton();
        initAudioButtons();

        for (Button button : buttons) {
            if (button.buttonSpriteSheet == null) {
                button.buttonSpriteSheet = new Texture(UISpritePaths.config_BTNs);
            }
        }

    }

    private void initReturnButton() {

        if (previousState == null)
            previousState = GameState.MENU;

        int canvasWidth = 20;
        int canvasHeight = 20;

        int x = (int) (menuX + 16 * multi);
        int y = (int) (menuY + 65 * multi);

        buttons.add(
            new StateButton(
                x,
                y,
                canvasWidth,
                canvasHeight,
                multi,
                new Sprite(0, 2),
                previousState,
                "return"
            )
        );
    }

    private void initAudioButtons() {

        int canvasWidth = 20;
        int canvasHeight = 20;

//        int width = (int) (canvasWidth * Game.SCALE * mult);
        int height = (int) (canvasHeight * multi);

        int x = (int) (menuX + 111 * multi);
        int y = (int) (menuY + 36 * multi);

        int yOffSet = (int) (height + 9 * multi);

        buttons.add(
            new On_OffButton(
                x,
                y + yOffSet,
                canvasWidth,
                canvasHeight,
                !soundEffectsMute,
                multi,
                new Sprite(0, 0),
                "sfx_mute"
            )
        );

        buttons.add(
            new On_OffButton(
                x,
                y,
                canvasWidth,
                canvasHeight,
                !soundMute,
                multi,
                new Sprite(0, 0),
                "vol_mute"
            )
        );
    }

    private void initSliders() {
        int canvasWidth = 50;
        int canvasHeight = 5;

        int height = (int) (canvasHeight * multi);

        int yOffSet = (int) (height + 24 * multi);

        int x = (int) (menuX + 69 * multi);
        int y = (int) (menuY + 43 * multi);


        sliders.add(
            new Slider(
                x,
                y + yOffSet,
                canvasWidth,
                canvasHeight,
                multi,
                new Sprite(0, 0),
                "sfx_volume",
                uiCameraManager
            )
        );

        sliders.add(
            new Slider(
                x,
                y,
                canvasWidth,
                canvasHeight,
                multi,
                new Sprite(0, 0),
                "sound_volume",
                uiCameraManager
            )
        );

        for (Slider slider : sliders) {

            if (slider.getType().equals("sfx_volume")) {
                slider.setTickPositionFromValue(soundEffectsVolume);
                slider.setTurnedOn(!soundEffectsMute);  // Garanta que o estado do mute é refletido
            }

            if (slider.getType().equals("sound_volume")) {
                slider.setTickPositionFromValue(soundVolume);
                slider.setTurnedOn(!soundMute);  // Garanta que o estado do mute é refletido
            }
        }
    }


    @Override
    public void update(float delta) {

    }

    @Override
    public void updateUi(float delta) {
        updateButtons();
        updateSliders();
    }

    private void updateSliders() {
        for (Slider slider : sliders) {
            if (slider.getType().equals("sfx_volume")) {
                if (soundEffectsMute == slider.isTurnedOn()) {
                    slider.setTurnedOn(!soundEffectsMute);
                }
                if (slider.getValue() != soundEffectsVolume) {
                    soundEffectsVolume = slider.getValue();
                }

            }
            if (slider.getType().equals("sound_volume")) {

                if (soundMute == slider.isTurnedOn()) {
                    slider.setTurnedOn(!soundMute);
                }

                if (slider.getValue() != soundVolume) {
                    soundVolume = slider.getValue();
                }

            }

            slider.update();
        }
    }

    private void updateButtons() {

        for (Button button : buttons) {

            switch (button.getType()) {
                case "return":
                    if (button instanceof StateButton menuButton && menuButton.getGameStateToChange() != previousState) {
                        menuButton.setGameStateToChange(previousState);
                    }
                    break;
                case "vol_mute":
                    if (button instanceof On_OffButton soundButton
                        && soundMute == soundButton.isTurnedOn()
                    ) {
                        soundMute = !soundButton.isTurnedOn();
                    }
                    break;
                case "sfx_mute":
                    if (button instanceof On_OffButton soundButton
                        && soundEffectsMute == soundButton.isTurnedOn()
                    ) {
                        soundEffectsMute = !soundButton.isTurnedOn();
                    }
                    break;
            }

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

        for (Slider slider : sliders) {
            slider.getTickSpriteSheet().dispose();
            slider.getButtonSpriteSheet().dispose();
        }
        // Dispose other resources if needed, e.g., Sounds, Music, etc.
    }


    @Override
    public void renderUi(SpriteBatch uiBatch) {

        uiBatch.draw(
            HelpMethods.obtainCurrentSpriteImage(
                aniPlayer.getCurrentSprite(),
                canvasWidth,
                canvasHeight,
                backGroundImage,
                false,
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

        for (Slider slider : sliders) {
            slider.render(uiBatch);
        }
    }

    public boolean handleTouchDragged(int screenX, int screenY, int touch) {
        for (Slider slider : sliders) {
            if (slider.isMousePressed()) {
                slider.changeXTickPos(screenX);
            }
        }
        return true;
    }

    @Override
    public boolean handleMouseMoved(int screenX, int screenY) {
        for (Button button : buttons) {
            button.setMouseOver(isMouseInsideButton(screenX, screenY, button));
        }

        return true;
    }

    @Override
    public boolean handleTouchDown(int screenX, int screenY, int button) {
        for (Button b : buttons) {
            if (isMouseInsideButton(screenX, screenY, b)) {
                b.setMouseOver(false);
                b.setMousePressed(true);

                break;
            }
        }

        for (Slider slider : sliders) {
            if (isMouseInsideButton(screenX, screenY, slider)) {
                slider.setMousePressed(true);
                break;
            }
        }


        return true;
    }

    @Override
    public boolean handleTouchUp(int screenX, int screenY, int button) {
        for (Button b : buttons) {
            if (isMouseInsideButton(screenX, screenY, b) && b.isMousePressed()) {
                b.mousePressedEvent();
                b.resetBools();
            } else {
                b.resetBools();
            }
        }

        for (Slider slider : sliders) {
            slider.resetBools();
        }


        return true;
    }

    @Override
    public boolean handleKeyDown(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            System.out.println(
                "sound volume: " + soundVolume + " | mute: " + soundMute +
                    " | sfx volume: " + soundEffectsVolume + " | mute: " + soundEffectsMute
            );
        }


        return true;
    }

    @Override
    public boolean handleKeyUp(int keycode) {
        return false;
    }
}
