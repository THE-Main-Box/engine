package official.sketchBook.gameState_related.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.ui_related.Button;

import java.util.List;

public abstract class State {
    protected PlayScreen game;

    protected CameraManager gameCameraManager;
    protected CameraManager uiCameraManager;

    // Vetor temporário para conversões

    protected Vector3 tempVector;
    protected Texture backGroundImage;
    protected int menuX, menuY, menuWidth, menuHeight;
    protected int canvasWidth, canvasHeight, spriteQuantityW, spriteQuantityH;
    protected ObjectAnimationPlayer aniPlayer;

    protected float mult;

    public State(PlayScreen game, CameraManager gameCameraManager, CameraManager uiCameraManager) {
        this.game = game;
        this.gameCameraManager = gameCameraManager;
        this.uiCameraManager = uiCameraManager;

        // Inicializa o vetor temporário
        this.tempVector = new Vector3();

    }

    protected void initBackGround(String texturePath, int spriteQW, int spriteQH, int y) {
        backGroundImage = new Texture(texturePath);

        // Quantidade de sprites na largura e altura do sprite sheet
        spriteQuantityW = spriteQW;
        spriteQuantityH = spriteQH;

        updateMenuSize();

        // Centraliza o menu na tela
        menuX = PlayScreen.GAME_WIDTH / 2 - menuWidth / 2; // Centralizado horizontalmente
        menuY = (int) (y * mult); // Mantém 1/3 da altura da tela, como no original

        initIdleBackGroundAnimation();
    }

    protected void initIdleBackGroundAnimation() {
        aniPlayer = new ObjectAnimationPlayer();
        aniPlayer.addAnimation("idle", List.of(new Sprite(0, 0)));
        aniPlayer.setAnimation("idle");
    }

    public void updateMenuSize() {
        // Dimensões individuais de cada sprite no canvas
        canvasWidth = backGroundImage.getWidth() / spriteQuantityW;
        canvasHeight = backGroundImage.getHeight() / spriteQuantityH;

        // Calcula o percentual que o canvas ocupa em relação à tela
        float canvasWidthPercentage = (float) canvasWidth / PlayScreen.GAME_WIDTH;
        float canvasHeightPercentage = (float) canvasHeight / PlayScreen.GAME_HEIGHT;

        // Ajusta as dimensões do menu com base no percentual calculado e no multiplicador
        menuWidth = (int) (PlayScreen.GAME_WIDTH * canvasWidthPercentage * mult);
        menuHeight = (int) (PlayScreen.GAME_HEIGHT * canvasHeightPercentage * mult);

    }

    public void draw(SpriteBatch batch, SpriteBatch uiBatch) {
        if (batch != null) {
            gameCameraManager.update();
            batch.setProjectionMatrix(gameCameraManager.getCamera().combined);

            // Inicia a renderização
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

    public boolean isMouseInsideButton(int mouseX, int mouseY, Button button) {
        // Converte as coordenadas da tela para o sistema de coordenadas da câmera/mundo
        uiCameraManager.getViewport().unproject(tempVector.set(mouseX, mouseY, 0));

        // Verifica se o mouse está dentro dos limites do botão
        return tempVector.x >= button.getxPos() &&
            tempVector.x <= button.getxPos() + button.getWidth() &&
            tempVector.y >= button.getyPos() &&
            tempVector.y <= button.getyPos() + button.getHeight();
    }

    public void dispose() {
        if (backGroundImage != null) {
            backGroundImage.dispose();
        }
    }

    protected abstract void render(SpriteBatch batch);

    protected abstract void renderUi(SpriteBatch uiBatch);

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
