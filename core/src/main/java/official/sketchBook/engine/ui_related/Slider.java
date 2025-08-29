package official.sketchBook.engine.ui_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import official.sketchBook.engine.animation_related.Sprite;
import official.sketchBook.engine.camera_related.CameraManager;
import official.sketchBook.game.util_related.info.paths.UISpritePaths;

import static official.sketchBook.engine.util_related.utils.TextureUtils.obtainCurrentSpriteImage;

public class Slider extends On_OffButton {

    private Texture tickSpriteSheet;
    private int tickCanvasWidth, tickCanvasHeight;
    private int tickWidth, tickHeight;

    private int tickXPos, minX, maxX;

    protected Vector3 tempVector;

    private int value;
    private CameraManager uiCameraManager;
    public Slider(
        float xPos,
        float yPos,
        int buttonCanvasWidth,
        int buttonCanvasHeight,
        float multiplier,
        Sprite buttonSprite,
        String type,
        CameraManager uiCameraManager
    ) {
        super(xPos, yPos, buttonCanvasWidth, buttonCanvasHeight,true, multiplier, buttonSprite, type);

        // Inicializa o vetor temporário
        this.tempVector = new Vector3();
        this.uiCameraManager = uiCameraManager;

        //está organizado desse jeito devido a código nátivo
        this.tickSpriteSheet = new Texture(UISpritePaths.slider_BTNs);//botão do slider
        this.buttonSpriteSheet = new Texture(UISpritePaths.slider_BG);//background do slider

        int spriteCountw = 1, spriteCounth = 2;

        tickCanvasWidth = tickSpriteSheet.getWidth() / spriteCountw;
        tickCanvasHeight = tickSpriteSheet.getHeight() / spriteCounth;

        tickWidth = (int) (tickCanvasWidth * multiplier);
        tickHeight = (int) (tickCanvasHeight * multiplier);

        tickXPos = (int) (this.xPos + this.width / 2);

        minX = (int) this.xPos;
        maxX = (int) (this.xPos + this.width);
    }

    @Override
    public void update() {
        if (turnedOn) {
            buttonCurrentSprite.setIndexY(0);
        } else {
            buttonCurrentSprite.setIndexY(1);
        }

        if (!mousePressed)
            updateSliderValue();

    }

    public void changeXTickPos(int screenX) {
        if (!turnedOn) return;

        // Converte as coordenadas de tela para coordenadas do mundo
        uiCameraManager.getViewport().unproject(tempVector.set(screenX, 0, 0));
        int worldX = (int) tempVector.x;

        // Ajusta a posição do tick no intervalo permitido
        if (worldX < minX) {
            tickXPos = minX;
        } else if (worldX > maxX) {
            tickXPos = maxX;
        } else {
            tickXPos = worldX;
        }
    }

    public void updateXTickPos(int x) {
        if (x < minX) {
            tickXPos = minX;
        } else if (x > maxX) {
            tickXPos = maxX;
        } else {
            tickXPos = x;
        }


    }

    public void setTickPositionFromValue(int value) {
        // Certifique-se de que o valor está dentro do intervalo esperado
        int maxVolume = 100;
        if (value < 0) value = 0;
        if (value > maxVolume) value = maxVolume;

        // Normaliza o valor no intervalo da barra (0 a 50) para o intervalo da posição (minX a maxX)
        float normalizedPosition = value / (float) maxVolume;

        // Calcula a nova posição X do *tick* baseado no valor
        tickXPos = (int) (minX + normalizedPosition * (maxX - minX));

        updateXTickPos(tickXPos);

    }

    public void updateSliderValue() {
        // Normaliza a posição do tick ao intervalo original
        float normalizedPosition = (tickXPos - minX) / (float) (maxX - minX);

        // Calcula o valor correspondente ao slider (0 a 50, onde cada pixel equivale a 20%)
        int maxVolume = 100; // Valor máximo do slider

        value = Math.round(normalizedPosition * maxVolume);
    }

    @Override
    public void render(SpriteBatch batch) {

        //renderiza o slider
        batch.draw(
            buttonSpriteSheet,
            xPos,
            yPos,
            width,
            height
        );

        //renderiza o tick
        batch.draw(
            obtainCurrentSpriteImage(
                buttonCurrentSprite,
                tickCanvasWidth,
                tickCanvasHeight,
                tickSpriteSheet,
                false,
                false
            ),
             shouldScaleByMultiplying()? tickXPos - 3 * multiplier : tickXPos - 3 / multiplier,
            shouldScaleByMultiplying()? yPos - 3 * multiplier : yPos - 3 / multiplier,
            tickWidth,
            tickHeight
        );
    }

    public Texture getTickSpriteSheet() {
        return tickSpriteSheet;
    }

    public void setTickSpriteSheet(Texture tickSpriteSheet) {
        this.tickSpriteSheet = tickSpriteSheet;
    }

    public int getTickCanvasWidth() {
        return tickCanvasWidth;
    }

    public void setTickCanvasWidth(int tickCanvasWidth) {
        this.tickCanvasWidth = tickCanvasWidth;
    }

    public int getTickCanvasHeight() {
        return tickCanvasHeight;
    }

    public void setTickCanvasHeight(int tickCanvasHeight) {
        this.tickCanvasHeight = tickCanvasHeight;
    }

    public int getTickWidth() {
        return tickWidth;
    }

    public void setTickWidth(int tickWidth) {
        this.tickWidth = tickWidth;
    }

    public int getTickHeight() {
        return tickHeight;
    }

    public void setTickHeight(int tickHeight) {
        this.tickHeight = tickHeight;
    }

    public int getTickXPos() {
        return tickXPos;
    }

    public void setTickXPos(int tickXPos) {
        this.tickXPos = tickXPos;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
