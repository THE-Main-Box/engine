package official.sketchBook.gameObject_related;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDatahandler;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    //dimensions and position related
    protected Vector2 position;
    protected float width, height;

    //body related
    protected Body body;

    //rendering and animation related
    protected List<SpriteSheetDatahandler> spriteSheetDatahandlerList;
    protected List<ObjectAnimationPlayer> objectAnimationPlayerList;

    protected boolean facingForward;

    public GameObject(Vector2 position, float width, float height, boolean facingForward) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.facingForward = facingForward;

        objectAnimationPlayerList = new ArrayList<>();
        spriteSheetDatahandlerList = new ArrayList<>();
    }

    // Métodos para inicializar a física e a renderização (implementados nas subclasses)
    protected abstract void createBody();
    public abstract void update(float deltaTime);
    public abstract void render();

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public boolean isFacingForward() {
        return facingForward;
    }

    public void setFacingForward(boolean facingForward) {
        this.facingForward = facingForward;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
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

    // Métodos para adicionar handlers de sprite e animação
    public void addAnimationPlayer(ObjectAnimationPlayer aniPlayer) {
        if (aniPlayer != null) {
            objectAnimationPlayerList.add(aniPlayer);
        }
    }

    public void addSpriteSheetDataHandler(SpriteSheetDatahandler spriteHandler) {
        if (spriteHandler != null) {
            spriteSheetDatahandlerList.add(spriteHandler);
        }
    }

    // Métodos para acessar os principais elementos
    public SpriteSheetDatahandler getPrimarySpriteHandler() {
        return spriteSheetDatahandlerList.isEmpty() ? null : spriteSheetDatahandlerList.get(0);
    }

    public ObjectAnimationPlayer getPrimaryAnimationPlayer() {
        return objectAnimationPlayerList.isEmpty() ? null : objectAnimationPlayerList.get(0);
    }

    public void addAniPlayer(ObjectAnimationPlayer aniPlayer){
        objectAnimationPlayerList.add(aniPlayer);
    }


    public List<SpriteSheetDatahandler> getSpriteSheetDatahandlerList() {
        return spriteSheetDatahandlerList;
    }

    public List<ObjectAnimationPlayer> getObjectAnimationPlayerList() {
        return objectAnimationPlayerList;
    }

}
