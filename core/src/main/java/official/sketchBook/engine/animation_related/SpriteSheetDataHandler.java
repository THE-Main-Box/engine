package official.sketchBook.engine.animation_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static official.sketchBook.engine.util_related.utils.TextureUtils.obtainCurrentSpriteImage;

/**
 * Gerencia dados e operações relacionados a uma sprite sheet,
 * incluindo posicionamento, rotação, escala e renderização de sprites.
 */
public class SpriteSheetDataHandler {

    /// Posição de renderização da imagem na tela
    private float x, y;


    /// Offset de ajuste para o ponto de renderização
    private float drawOffSetX, drawOffSetY;

    /// Origem da rotação (ponto em torno do qual o sprite será rotacionado)
    private float originX = 0f, originY = 0f;

    /// Rotação atual do sprite
    private float rotation = 0f;

    /// Dimensões de cada quadro da sprite sheet
    private final int canvasHeight, canvasWidth;

    /// Dimensões reais de renderização após aplicação da escala
    private float renderWidth, renderHeight;

    /// Escala da imagem
    private float scaleX = 1f, scaleY = 1f;

    /// Define se a escala é feita por multiplicação (true) ou divisão (false)
    private boolean scaleMultiplyMode = true;

    /// Define se o sprite está virado para frente
    private boolean xAxisInvert;

    /// Define se o sprite está de ponta cabeça
    private boolean yAxisInvert;

    /// Textura contendo a sprite sheet
    private final Texture spriteSheet;

    /**
     * Construtor da classe responsável por inicializar os dados da sprite sheet.
     *
     * @param x                 Posição X inicial da imagem.
     * @param y                 Posição Y inicial da imagem.
     * @param drawOffSetX       Offset de renderização no eixo X.
     * @param drawOffSetY       Offset de renderização no eixo Y.
     * @param spriteQuantityX   Quantidade de sprites na horizontal.
     * @param spriteQuantityY   Quantidade de sprites na vertical.
     * @param xAxisInvert      Define se o sprite começa virado para frente.
     * @param yAxisInvert        Define se o sprite começa de cabeça para baixo.
     * @param spriteSheet       Textura contendo a sprite sheet.
     */
    public SpriteSheetDataHandler(
        float x,
        float y,
        float drawOffSetX,
        float drawOffSetY,
        int spriteQuantityX,
        int spriteQuantityY,
        boolean xAxisInvert,
        boolean yAxisInvert,
        Texture spriteSheet
    ) {
        this.x = x;
        this.y = y;
        this.drawOffSetX = drawOffSetX;
        this.drawOffSetY = drawOffSetY;

        this.xAxisInvert = xAxisInvert;
        this.yAxisInvert = yAxisInvert;

        this.spriteSheet = spriteSheet;
        this.canvasWidth = spriteSheet.getWidth() / spriteQuantityX;
        this.canvasHeight = spriteSheet.getHeight() / spriteQuantityY;

        updateRenderDimensions();
        setRotationOrigin(renderWidth / 2, renderHeight / 2);
    }

    /**
     * Atualiza as dimensões reais de renderização com base na escala.
     */
    private void updateRenderDimensions() {
        if (scaleMultiplyMode) {
            renderWidth = canvasWidth * scaleX;
            renderHeight = canvasHeight * scaleY;
        } else {
            renderWidth = canvasWidth / scaleX;
            renderHeight = canvasHeight / scaleY;
        }
    }

    /**
     * Atualiza a posição da imagem, considerando os offsets de escala.
     *
     * @param x Nova posição X.
     * @param y Nova posição Y.
     */
    public void updatePosition(float x, float y) {
        float offsetX = scaleMultiplyMode ? drawOffSetX * scaleX : drawOffSetX / scaleX;
        float offsetY = scaleMultiplyMode ? drawOffSetY * scaleY : drawOffSetY / scaleY;

        this.x = x - offsetX;
        this.y = y - offsetY;
    }

    /**
     * Define a escala de renderização do sprite.
     *
     * @param scaleX Escala no eixo X.
     * @param scaleY Escala no eixo Y.
     */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        updateRenderDimensions();
    }

    /**
     * Define o modo de escala da imagem.
     *
     * @param multiply True para multiplicar a escala, false para dividir.
     */
    public void setScaleModeMultiply(boolean multiply) {
        this.scaleMultiplyMode = multiply;
        updateRenderDimensions();
    }

    /**
     * Define o ponto de origem para rotação do sprite.
     *
     * @param originX Coordenada X do ponto de rotação.
     * @param originY Coordenada Y do ponto de rotação.
     */
    public void setRotationOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
    }

    /**
     * Define a rotação atual da imagem.
     *
     * @param rotation Ângulo de rotação em graus.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Renderiza o sprite atual com base no estado interno da classe.
     *
     * @param batch         SpriteBatch usado para desenhar o sprite.
     * @param currentSprite Instância de Sprite contendo as informações do frame atual.
     */
    public void renderSprite(SpriteBatch batch, Sprite currentSprite) {
        batch.draw(
            obtainCurrentSpriteImage(
                currentSprite,
                canvasWidth,
                canvasHeight,
                spriteSheet,
                xAxisInvert,
                yAxisInvert
            ),
            x,
            y,
            originX,
            originY,
            renderWidth,
            renderHeight,
            1f,
            1f,
            rotation
        );
    }

    /** Define o offset X da renderização. */
    public void setDrawOffSetX(float drawOffSetX) {
        this.drawOffSetX = drawOffSetX;
    }

    /** Define o offset Y da renderização. */
    public void setDrawOffSetY(float drawOffSetY) {
        this.drawOffSetY = drawOffSetY;
    }

    /** @return Origem da rotação no eixo X. */
    public float getOriginX() {
        return originX;
    }

    /** @return Origem da rotação no eixo Y. */
    public float getOriginY() {
        return originY;
    }

    /** Define se o sprite está virado para frente. */
    public void setxAxisInvert(boolean xAxisInvert) {
        this.xAxisInvert = xAxisInvert;
    }

    /** Define se o sprite está de ponta cabeça. */
    public void setyAxisInvert(boolean yAxisInvert) {
        this.yAxisInvert = yAxisInvert;
    }

    /** @return Textura da sprite sheet. */
    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    /** @return Altura de cada quadro da sprite sheet. */
    public int getCanvasHeight() {
        return canvasHeight;
    }

    /** @return Largura de cada quadro da sprite sheet. */
    public int getCanvasWidth() {
        return canvasWidth;
    }

    /** Libera os recursos associados à textura. */
    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
