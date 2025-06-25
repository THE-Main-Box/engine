package official.sketchBook.gameState_related.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.ui_related.Button;
import official.sketchBook.util_related.helpers.HelpMethods;

import java.util.List;

public abstract class State implements StateMethods {
    protected PlayScreen game;

    protected CameraManager gameCameraManager;
    protected CameraManager uiCameraManager;

    /// Vetor do mouse normalizado (coordenadas corrigidas, sem o eixo Y invertido do LibGDX)
    protected Vector3 normalizedMouseVector;
    /// Imagem de fundo do menu
    protected Texture backGroundImage;
    /// Coordenadas de posicionamento do menu
    protected int menuX, menuY;
    /// Dimensões do menu
    protected int menuWidth, menuHeight;
    /// Dimensões do canvas, usadas para calcular os recortes da imagem
    protected int canvasWidth, canvasHeight;
    /// Quantidade de sprites na vertical e horizontal no spritesheet
    protected int spriteQuantityV, spriteQuantityH;
    /// Controlador de animações para o background
    protected ObjectAnimationPlayer aniPlayer;

    /// Multiplicador geral para escalar elementos internos independentemente da escala global
    protected float multi;
    protected boolean shouldMultiply = true;

    /**
     * Construtor da classe base State.
     *
     * @param game Tela principal que chamará esse estado
     * @param gameCameraManager Gerenciador da câmera do jogo
     * @param uiCameraManager Gerenciador da câmera da interface (UI)
     */
    public State(PlayScreen game, CameraManager gameCameraManager, CameraManager uiCameraManager) {
        this.game = game;
        this.gameCameraManager = gameCameraManager;
        this.uiCameraManager = uiCameraManager;

        // Inicializa o vetor temporário
        this.normalizedMouseVector = new Vector3();
    }

    /**
     * Inicializa a imagem de fundo e define o posicionamento do menu
     *
     * @param texturePath Caminho da textura de fundo
     * @param spriteQV Quantidade de sprites verticais no spritesheet
     * @param spriteQH Quantidade de sprites horizontais no spritesheet
     * @param x Posição X centralizada do menu
     * @param y Posição Y centralizada do menu
     */
    protected void initBackGround(String texturePath, int spriteQV, int spriteQH, int x, int y) {
        backGroundImage = new Texture(texturePath);

        spriteQuantityV = spriteQV;
        spriteQuantityH = spriteQH;

        updateMenuSize();

        // Centraliza o menu no ponto (x, y) dado
        menuX = x - menuWidth / 2; // Centralizado horizontalmente

        menuY = y - menuHeight / 2; // Centralizado verticalmente

        initIdleBackGroundAnimation();
    }

    /**
     * Inicializa uma animação padrão de fundo (idle)
     */
    protected void initIdleBackGroundAnimation() {
        aniPlayer = new ObjectAnimationPlayer();
        aniPlayer.addAnimation("idle", List.of(new Sprite(0, 0)));
        aniPlayer.playAnimation("idle");
    }

    /**
     * Atualiza o tamanho do menu baseado nas dimensões da imagem e na multiplicação de escala
     */
    public void updateMenuSize() {
        // Dimensões de um quadro individual do spritesheet
        canvasWidth = backGroundImage.getWidth() / spriteQuantityV;
        canvasHeight = backGroundImage.getHeight() / spriteQuantityH;

        // Percentuais da largura e altura relativas à tela
        float canvasWidthPercentage = (float) canvasWidth / PlayScreen.GAME_WIDTH;
        float canvasHeightPercentage = (float) canvasHeight / PlayScreen.GAME_HEIGHT;

        // Ajusta o tamanho do menu considerando o percentual e o multiplicador
        menuWidth = (int) HelpMethods.scale(PlayScreen.GAME_WIDTH * canvasWidthPercentage, multi, shouldMultiply);
        menuHeight = (int) HelpMethods.scale(PlayScreen.GAME_HEIGHT * canvasHeightPercentage, multi, shouldMultiply);

    }

    /**
     * Renderiza o jogo e a UI, usando diferentes SpriteBatches
     */
    public void draw(SpriteBatch batch, SpriteBatch uiBatch) {
        if (batch != null) {
            gameCameraManager.update();
            batch.setProjectionMatrix(gameCameraManager.getCamera().combined);

            batch.begin();
            this.render(batch);
            batch.end();
        }

        if (uiBatch != null) {
            uiCameraManager.update();
            uiBatch.setProjectionMatrix(uiCameraManager.getCamera().combined);

            uiBatch.begin();
            renderUi(uiBatch);
            uiBatch.end();
        }
    }

    /**
     * Verifica se o mouse está dentro dos limites de um botão
     *
     * @param mouseX Posição X do mouse (em pixels de tela)
     * @param mouseY Posição Y do mouse (em pixels de tela)
     * @param button Instância do botão a ser checado
     * @return true se o mouse estiver sobre o botão
     */
    public boolean isMouseInsideButton(int mouseX, int mouseY, Button button) {
        // Corrige as coordenadas do mouse para o sistema de mundo
        uiCameraManager.getViewport().unproject(normalizedMouseVector.set(mouseX, mouseY, 0));

        // Verifica colisão simples ponto-retângulo
        return normalizedMouseVector.x >= button.getxPos() &&
            normalizedMouseVector.x <= button.getxPos() + button.getWidth() &&
            normalizedMouseVector.y >= button.getyPos() &&
            normalizedMouseVector.y <= button.getyPos() + button.getHeight();
    }

    /**
     * Libera os recursos utilizados pelo estado
     */
    public void dispose() {
        if (backGroundImage != null) {
            backGroundImage.dispose();
        }
    }

    public abstract void render(SpriteBatch batch);

    public abstract void renderUi(SpriteBatch uiBatch);

    public PlayScreen getGame() {
        return game;
    }

    public CameraManager getGameCameraManager() {
        return gameCameraManager;
    }

    public CameraManager getUiCameraManager() {
        return uiCameraManager;
    }
}
