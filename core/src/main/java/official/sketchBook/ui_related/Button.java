package official.sketchBook.ui_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.util_related.helpers.HelpMethods;

public abstract class Button {

    protected String type;

    protected int xOffSetCenter;

    protected boolean mouseOver = false;
    protected boolean mousePressed = false;

    protected float xPos;
    protected float yPos;
    protected float width;
    protected float height;


    protected Sprite buttonCurrentSprite;

    public Texture buttonSpriteSheet;

    public int buttonCanvasWidth;
    public int buttonCanvasHeight;

    protected float multiplier;

    public Button(float xPos, float yPos, int buttonCanvasWidth, int buttonCanvasHeight, float multiplier, Sprite buttonSprite, String type) {
        this.buttonCanvasHeight = buttonCanvasHeight;
        this.buttonCanvasWidth = buttonCanvasWidth;

        // Tamanho do botão é baseado no canvas e no multiplicador
        this.width = HelpMethods.scale(buttonCanvasWidth, multiplier, shouldScaleByMultiplying()); // ou false, dependendo do seu uso
        this.height = HelpMethods.scale(buttonCanvasHeight, multiplier, shouldScaleByMultiplying());


        this.xOffSetCenter = (int) (width / 2);

        this.xPos = xPos - xOffSetCenter;
        this.yPos = yPos;


        this.buttonCurrentSprite = buttonSprite;

        this.type = type;

        this.multiplier = multiplier;

    }

    /// Retorna true para multiplicar e false para dividir
    protected boolean shouldScaleByMultiplying(){
        return true;
    }

    public void update() {
        buttonCurrentSprite.setIndexX(0);

        if (mouseOver) {
            buttonCurrentSprite.setIndexX(1);
        } else if (mousePressed) {
            buttonCurrentSprite.setIndexX(2);
        }
    }

    public void render(SpriteBatch batch) {
        //caso tenhamos um tocador de animações usamos ele para obter a sprite que deve ser renderizado atualmente
        batch.draw(
            HelpMethods.obtainCurrentSpriteImage(
                buttonCurrentSprite,
                buttonCanvasWidth,
                buttonCanvasHeight,
                buttonSpriteSheet,
                false,
                false
            ),
            xPos,
            yPos,
            width,
            height
        );
    }

    public void mouseOverEvent() {
    }

    public void mousePressedEvent() {
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getyPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public float getxPos() {
        return xPos;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public int getxOffSetCenter() {
        return xOffSetCenter;
    }

    public void setxOffSetCenter(int xOffSetCenter) {
        this.xOffSetCenter = xOffSetCenter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void resetBools() {
        this.mouseOver = false;
        this.mousePressed = false;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public Sprite getButtonCurrentSprite() {
        return buttonCurrentSprite;
    }

    public void setButtonCurrentSprite(Sprite buttonCurrentSprite) {
        this.buttonCurrentSprite = buttonCurrentSprite;
    }

    public Texture getButtonSpriteSheet() {
        return buttonSpriteSheet;
    }

    public void setButtonSpriteSheet(Texture buttonSpriteSheet) {
        this.buttonSpriteSheet = buttonSpriteSheet;
    }

    public int getButtonCanvasWidth() {
        return buttonCanvasWidth;
    }

    public void setButtonCanvasWidth(int buttonCanvasWidth) {
        this.buttonCanvasWidth = buttonCanvasWidth;
    }

    public int getButtonCanvasHeight() {
        return buttonCanvasHeight;
    }

    public void setButtonCanvasHeight(int buttonCanvasHeight) {
        this.buttonCanvasHeight = buttonCanvasHeight;
    }
}
