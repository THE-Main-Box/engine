package official.sketchBook.game.screen_related;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import official.sketchBook.engine.MainClass;
import official.sketchBook.engine.camera_related.CameraManager;
import official.sketchBook.engine.components_related.integration_interfaces.RayCasterII;
import official.sketchBook.engine.gameObject_related.base_model.GameObject;
import official.sketchBook.engine.gameState_related.model.State;
import official.sketchBook.engine.util_related.enumerators.states.GameState;
import official.sketchBook.game.gameState_related.models.Configuration;
import official.sketchBook.game.gameState_related.models.Menu;
import official.sketchBook.game.gameState_related.models.Paused;
import official.sketchBook.game.gameState_related.models.Playing;
import official.sketchBook.game.input_related.InputHandler;

import java.util.EnumMap;

import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Debug.showHitBox;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Debug.showRayCast;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.*;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Screen.*;

public class PlayScreen implements Screen {

    public int fps, ups;

    private long lastTime = System.nanoTime(); // Para calcular FPS e UPS
    private float accumulator = 0f;
    private int updates = 0;

    public static final int GAME_WIDTH = TILES_DEFAULT_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_DEFAULT_SIZE * TILES_IN_HEIGHT;
    public static float worldWidth, worldHeight;

    //rendering related
    public SpriteBatch batch;
    public SpriteBatch uiBatch;
    private final CameraManager gameCameraManager;
    private final CameraManager uiCameraManager;

    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    //game state related
    private Configuration configState;
    private Playing playingState;
    private Paused pausedState;
    private Menu menuState;

    private final EnumMap<GameState, State> stateMap = new EnumMap<>(GameState.class);

    public PlayScreen(MainClass game) {

        // Inicialização dos elementos principais
        gameCameraManager = new CameraManager(GAME_WIDTH * scale, GAME_HEIGHT * scale);
        this.batch = game.batch;

        // Configuração da câmera e do batch para o menu
        uiCameraManager = new CameraManager(Gdx.graphics.getWidth() / scale, Gdx.graphics.getHeight() / scale);
        this.uiBatch = game.uiBatch;

        //inicia os states e os salva dentro de um hashMap
        this.initState();

    }

    private void initState() {

        playingState = new Playing(this, gameCameraManager, uiCameraManager);
        configState = new Configuration(this, gameCameraManager, uiCameraManager);
        pausedState = new Paused(this, gameCameraManager, uiCameraManager);
        menuState = new Menu(this, gameCameraManager, uiCameraManager);

        stateMap.put(GameState.CONFIGURATION, configState);
        stateMap.put(GameState.PLAYING, playingState);
        stateMap.put(GameState.PAUSED, pausedState);
        stateMap.put(GameState.MENU, menuState);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputHandler(this));
        Gdx.graphics.setVSync(false);
    }

    @Override
    public void render(float delta) {
        // Acumula o tempo decorrido
        accumulator += delta;

        // Atualiza a lógica do jogo em passos fixos usando um segundo loop
        while (accumulator >= FIXED_TIMESTAMP) {
            updateGameStates(FIXED_TIMESTAMP); // Use FIXED_TIMESTEP para manter a lógica consistente
            accumulator -= FIXED_TIMESTAMP;
            updates++;
        }

        // Renderiza o jogo apenas se o tempo do FPS permitir
        draw();

        // Calcula e exibe FPS e UPS
        calculateFpsUps();
    }

    private void draw() {

        if (Gdx.graphics.getFramesPerSecond() != FPS_TARGET) {
            Gdx.graphics.setForegroundFPS(FPS_TARGET);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Configura a projeção da câmera
        this.drawGameStates(batch, uiBatch);

    }

    private void renderDebugVariables() {

        if (showHitBox) {
            debugRenderer.render(
                playingState.getWorld(),
                gameCameraManager.getCamera().combined.scl(PPM)
            );
        }

        if (showRayCast) {
            for (GameObject object : playingState.objectManager.getCurrentRoom().getGameObjects()) {
                if (object instanceof RayCasterII caster && caster.getRayCastHelper() != null) {
                    caster.getRayCastHelper().render(gameCameraManager.getCamera().combined.scl(PPM));
                }
            }
        }

        for (GameObject object : playingState.objectManager.getCurrentRoom().getGameObjects()) {
            if (object instanceof RayCasterII caster && caster.getRayCastHelper() != null) {
                caster.getRayCastHelper().clearRays();
            }
        }
    }

    private void updateGameStates(float delta) {
        State currentState = stateMap.get(GameState.state);
        if (currentState != null) {
            currentState.update(delta);
            currentState.updateUi(delta);
        }


        if (GameState.state.equals(GameState.QUIT)) {
            this.quitGame();
        }

    }

    private void drawGameStates(SpriteBatch batch, SpriteBatch uiBatch) {

        State currentState = stateMap.get(GameState.state);
        if (currentState != null) {
            currentState.draw(batch, uiBatch);
        }

        if (GameState.state.equals(GameState.PLAYING)) {
            renderDebugVariables();
        }

    }

    private void calculateFpsUps() {
        long now = System.nanoTime();
        if (now - lastTime >= 1_000_000_000) { // Se 1 segundo passou
            fps = Gdx.graphics.getFramesPerSecond();
            ups = updates;
            updates = 0;
            lastTime = now;
        }
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        // Atualiza o viewport com as dimensões ajustadas
        gameCameraManager.updateViewport(screenWidth, screenHeight);
        uiCameraManager.updateViewport(screenWidth, screenHeight);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (State state : stateMap.values()) {
            state.dispose();
        }

        debugRenderer.dispose();
    }

    private void quitGame() {
        System.out.println("Encerrando o jogo..."); // Mensagem informativa (opcional)
        // Finalize o aplicativo sem mensagens de erro
        Gdx.app.exit();
    }

    public EnumMap<GameState, State> getStateMap() {
        return stateMap;
    }

    public Configuration getConfigState() {
        return configState;
    }

    public Playing getPlayingState() {
        return playingState;
    }

    public Paused getPausedState() {
        return pausedState;
    }

    public Menu getMenuState() {
        return menuState;
    }

    public boolean handleKeyDown(int keyCode) {
        return stateMap.get(GameState.state).handleKeyDown(keyCode);
    }

    public boolean handleKeyUp(int keyCode) {
        return stateMap.get(GameState.state).handleKeyUp(keyCode);
    }

    public boolean handleTouchDown(int screenX, int screenY, int button) {
        return stateMap.get(GameState.state).handleTouchDown(screenX, screenY, button);
    }

    public boolean handleTouchDragged(int screenX, int screenY, int button) {
        return stateMap.get(GameState.state).handleTouchDragged(screenX, screenY, button);
    }

    public boolean handleTouchUp(int screenX, int screenY, int button) {
        return stateMap.get(GameState.state).handleTouchUp(screenX, screenY, button);
    }

    public boolean handleMouseMoved(int screenX, int screenY) {
        return stateMap.get(GameState.state).handleMouseMoved(screenX, screenY);
    }
}
