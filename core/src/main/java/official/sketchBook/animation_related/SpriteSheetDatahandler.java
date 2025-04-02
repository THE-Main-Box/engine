package official.sketchBook.animation_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.util_related.helpers.HelpMethods;

public class SpriteSheetDatahandler {
    private float x, y;
    private float drawOffSetX, drawOffSetY;
    private int canvasHeight, canvasWidth;
    private float renderHeight, renderWidth;
    private int spriteQuantityX, spriteQuantityY;
    private Texture spriteSheet;

    private boolean facingFoward;

    public SpriteSheetDatahandler(
        float x,
        float y,
        float drawOffSetX,
        float drawOffSetY,
        int spriteQuantityX,
        int spriteQuantityY,
        boolean facingFoward,
        Texture spriteSheet
    ) {

        this.drawOffSetX = drawOffSetX;
        this.drawOffSetY = drawOffSetY;

        this.x = x;
        this.y = y;

        this.spriteQuantityY = spriteQuantityY;
        this.spriteQuantityX = spriteQuantityX;

        this.spriteSheet = spriteSheet;

        this.facingFoward = facingFoward;

        this.canvasWidth = spriteSheet.getWidth() / spriteQuantityX;
        this.canvasHeight = spriteSheet.getHeight() / spriteQuantityY;

    }


    //pegamos as dimensões e renderizamos usando a escala, passando ela para renderizar os objetos de forma correta
    public void update(float x, float y, float scale, boolean multiply) {
        // Escala o offset para manter o posicionamento correto
        float scaledOffSetX = multiply ? drawOffSetX * scale : drawOffSetX / scale;
        float scaledOffSetY = multiply ? drawOffSetY * scale : drawOffSetY / scale;

        // Ajusta a posição baseada no offset escalado
        this.x = x - scaledOffSetX;
        this.y = y - scaledOffSetY;

        // Escala a largura e altura corretamente
        if (multiply) {
            renderWidth = canvasWidth * scale;
            renderHeight = canvasHeight * scale;
        } else {
            renderWidth = canvasWidth / scale;
            renderHeight = canvasHeight / scale;
        }

    }

    public void renderSprite(SpriteBatch batch, Sprite currentSprite) {
        batch.draw(
            HelpMethods.obtainCurrentSpriteImage(
                currentSprite,
                canvasWidth,
                canvasHeight,
                spriteSheet,
                !facingFoward
            ),
            x,
            y,
            renderWidth,
            renderHeight
        );
    }

    public void setFacingFoward(boolean facingFoward) {
        this.facingFoward = facingFoward;
    }

    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    public void dispose(){
        this.spriteSheet.dispose();
    }

}
