package official.sketchBook.ui_related;

import official.sketchBook.animation_related.Sprite;

public class On_OffButton extends Button{

    protected boolean turnedOn;

    public On_OffButton(
        float xPos,
        float yPos,
        int buttonCanvasWidth,
        int buttonCanvasHeight,
        boolean turnedOn,
        float multiplier,
        Sprite buttonSprite,
        String type
    ) {
        super(xPos, yPos, buttonCanvasWidth, buttonCanvasHeight, multiplier, buttonSprite, type);
        this.turnedOn = turnedOn;
    }

    @Override
    public void update() {
        buttonCurrentSprite.setIndexX(0);

        if (mouseOver) {
            buttonCurrentSprite.setIndexX(1);
        }

        if (mousePressed) {
            buttonCurrentSprite.setIndexX(2);
        }

        if(!turnedOn){
            buttonCurrentSprite.setIndexY(1);
        }else{
            buttonCurrentSprite.setIndexY(0);
        }

    }

    @Override
    public void mousePressedEvent() {
        toggleMute();
    }

    private void toggleMute(){
        turnedOn = !turnedOn;
    }

    public boolean isTurnedOn() {
        return turnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        this.turnedOn = turnedOn;
    }
}
