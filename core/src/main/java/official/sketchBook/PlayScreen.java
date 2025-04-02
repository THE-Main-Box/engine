package official.sketchBook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.gameState_related.Configuration;
import official.sketchBook.gameState_related.Menu;
import official.sketchBook.gameState_related.Paused;
import official.sketchBook.gameState_related.Playing;
import official.sketchBook.input_related.InputHandler;
import official.sketchBook.util_related.enumerators.states.GameState;

public class PlayScreen implements Screen {

    //update related
    public static final int VELOCITY_ITERATIONS = 8;
    public static final int POSITION_ITERATIONS = 3;
    private static final int FPS_TARGET = 60;
    private static final int UPS_TARGET = 60;
    public static final float FIXED_TIMESTEP = (float) 1 / UPS_TARGET; // Taxa fixa de UPS (60 Updates por segundo)
    private static final float MAX_FPS = (float) 1 / FPS_TARGET; // Taxa máxima de FPS (120 Frames por segundo)
    private float accumulator = 0f;
    private float frameTimer = 0f; // Temporizador para controlar o FPS
    private int frames = 0, updates = 0;
    public int fps, ups;
    private long lastTime = System.nanoTime(); // Para calcular FPS e UPS

    // game dimensions related
    public static float scale = 2f;
    public static final int TILES_IN_WIDTH = 39;
    public static final int TILES_IN_HEIGHT = 21;
    public static final int TILES_DEFAULT_SIZE = 16;
    public static final int GAME_WIDTH = TILES_DEFAULT_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_DEFAULT_SIZE * TILES_IN_HEIGHT;
    public static float worldWidth, worldHeight;
    public final static float PPM = 100;

    //rendering related
    public SpriteBatch batch;
    public SpriteBatch uiBatch;
    private final CameraManager gameCameraManager;
    private final CameraManager uiCameraManager;

    //game state related
    private final Configuration configState;
    private final Playing playingState;
    private final Paused pausedState;
    private final Menu menuState;

    //sound related
    public static boolean soundEfectsMute = false;
    public static boolean soundMute = false;
    public static int soundEfectsVolume = 90;
    public static int soundVolume = 50;

    //debug related
    public static boolean showHitBox = false;
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    public PlayScreen(MainClass game) {

        // Inicialização dos elementos principais
        gameCameraManager = new CameraManager(GAME_WIDTH * scale, GAME_HEIGHT * scale);
        this.batch = game.batch;

        // Configuração da câmera e do batch para o menu
        uiCameraManager = new CameraManager(Gdx.graphics.getWidth() / scale, Gdx.graphics.getHeight() / scale);
        this.uiBatch = new SpriteBatch();

        configState = new Configuration(this, gameCameraManager, uiCameraManager);
        playingState = new Playing(this, gameCameraManager, uiCameraManager);
        pausedState = new Paused(this, gameCameraManager, uiCameraManager);
        menuState = new Menu(this, gameCameraManager, uiCameraManager);

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
        frameTimer += delta;


        // Atualiza a lógica do jogo em passos fixos usando um segundo loop
        while (accumulator >= FIXED_TIMESTEP) {
            updateGameStates(); // Use FIXED_TIMESTEP para manter a lógica consistente
            accumulator -= FIXED_TIMESTEP;
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

        frames++;
        frameTimer -= MAX_FPS; // Reduz o tempo acumulado do FPS


    }
    private void renderDebuggVariables() {

        if (showHitBox) {
            debugRenderer.render(
                playingState.getWorld(),
                gameCameraManager.getCamera().combined.scl(PPM)
            );
        }

    }

    private void updateGameStates() {
        switch (GameState.state) {
            case PLAYING -> {

                playingState.update(FIXED_TIMESTEP);

            }
            case MENU -> {

                menuState.update(FIXED_TIMESTEP);

            }
            case CONFIGURATION -> {

                configState.updateUi(FIXED_TIMESTEP);

            }
            case PAUSED -> {

                pausedState.update(FIXED_TIMESTEP);

            }
            case QUIT -> {

                this.quitGame();

            }
            default -> {
            }
        }

    }

    private void drawGameStates(SpriteBatch batch, SpriteBatch uiBatch) {

        switch (GameState.state) {
            case PLAYING -> {

                playingState.draw(batch, uiBatch);
                renderDebuggVariables();

            }
            case MENU -> {

                menuState.draw(batch, uiBatch);

            }
            case CONFIGURATION -> {

                configState.draw(batch, uiBatch);

            }
            case PAUSED -> {

                pausedState.draw(batch, uiBatch);

            }
            case QUIT -> {


            }
            default -> {
            }
        }

    }

    private void calculateFpsUps() {
        long now = System.nanoTime();
        if (now - lastTime >= 1_000_000_000) { // Se 1 segundo passou
            fps = frames;
            ups = updates;
            frames = 0;
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

    //TODO: definir um dispose válido para todas essas classes
    @Override
    public void dispose() {
        playingState.dispose();
        pausedState.dispose();
        configState.dispose();
        menuState.dispose();

    }

    private void quitGame() {
        System.out.println("Encerrando o jogo..."); // Mensagem informativa (opcional)
        // Finalize o aplicativo sem mensagens de erro
        Gdx.app.exit();
    }

    public Paused getPausedState() {
        return pausedState;
    }

    public Playing getPlayingState() {
        return playingState;
    }

    public Configuration getConfigState() {
        return configState;
    }

    public Menu getMenuState() {
        return menuState;
    }
}
