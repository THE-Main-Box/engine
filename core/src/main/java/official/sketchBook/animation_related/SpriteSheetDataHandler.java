package official.sketchBook.animation_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.util_related.helpers.HelpMethods;

public class SpriteSheetDataHandler {

    // Posição da imagem
    private float x, y;

    // Offset de renderização da imagem
    private float drawOffSetX, drawOffSetY;

    // Dimensões do canvas
    private int canvasHeight, canvasWidth;

    // Dimensões de renderização
    private float renderWidth, renderHeight;

    // Quantidade de sprites
    private int spriteQuantityX, spriteQuantityY;

    // Escala da imagem
    private float scaleX = 1f, scaleY = 1f;

    // Define se a escala deve multiplicar (true) ou dividir (false)
    private boolean scaleMultiplyMode = true;

    // Rotação da imagem
    private float rotation = 0f;

    // Origem da rotação
    private float originX = 0f, originY = 0f;

    // Se estamos virados para frente
    private boolean facingFoward;

    // Se estamos virados de ponta cabeça
    private boolean upsideDown;

    // Sprite sheet
    private Texture spriteSheet;

    public SpriteSheetDataHandler(
        float x,
        float y,
        float drawOffSetX,
        float drawOffSetY,
        int spriteQuantityX,
        int spriteQuantityY,
        boolean facingFoward,
        boolean upsideDown,
        Texture spriteSheet
    ) {
        this.x = x;
        this.y = y;
        this.drawOffSetX = drawOffSetX;
        this.drawOffSetY = drawOffSetY;

        this.spriteQuantityX = spriteQuantityX;
        this.spriteQuantityY = spriteQuantityY;
        this.facingFoward = facingFoward;
        this.upsideDown = upsideDown;

        this.spriteSheet = spriteSheet;
        this.canvasWidth = spriteSheet.getWidth() / spriteQuantityX;
        this.canvasHeight = spriteSheet.getHeight() / spriteQuantityY;

        updateRenderDimensions();
        setRotationOrigin(renderWidth / 2, renderHeight / 2);
    }

    private void updateRenderDimensions() {
        if (scaleMultiplyMode) {
            renderWidth = canvasWidth * scaleX;
            renderHeight = canvasHeight * scaleY;
        } else {
            renderWidth = canvasWidth / scaleX;
            renderHeight = canvasHeight / scaleY;
        }
    }

    public void updatePosition(float x, float y) {
        float offsetX = scaleMultiplyMode ? drawOffSetX * scaleX : drawOffSetX / scaleX;
        float offsetY = scaleMultiplyMode ? drawOffSetY * scaleY : drawOffSetY / scaleY;

        this.x = x - offsetX;
        this.y = y - offsetY;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        updateRenderDimensions();
    }

    public void setScaleModeMultiply(boolean multiply) {
        this.scaleMultiplyMode = multiply;
        updateRenderDimensions();
    }

    public void setRotationOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void renderSprite(SpriteBatch batch, Sprite currentSprite) {
        batch.draw(
            HelpMethods.obtainCurrentSpriteImage(
                currentSprite,
                canvasWidth,
                canvasHeight,
                spriteSheet,
                !facingFoward,
                upsideDown
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

    public void setDrawOffSetX(float drawOffSetX) {
        this.drawOffSetX = drawOffSetX;
    }

    public void setDrawOffSetY(float drawOffSetY) {
        this.drawOffSetY = drawOffSetY;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setFacingFoward(boolean facingFoward) {
        this.facingFoward = facingFoward;
    }

    public void setUpsideDown(boolean upsideDown) {
        this.upsideDown = upsideDown;
    }

    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
