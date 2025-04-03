package official.sketchBook.gameObject_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDatahandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.PhysicsComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    //dimensions and position related
    protected float x, y;
    protected float width, height;

    //physics related
    protected Body body;
    protected World world;

    //rendering and animation related
    protected List<SpriteSheetDatahandler> spriteSheetDatahandlerList;
    protected List<ObjectAnimationPlayer> objectAnimationPlayerList;

    protected boolean facingForward;

    //components related
    protected List<Component> components;

    protected PhysicsComponent physicsC;

    public GameObject(float x, float y, float width, float height, boolean facingForward, World world) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.facingForward = facingForward;

        objectAnimationPlayerList = new ArrayList<>();
        spriteSheetDatahandlerList = new ArrayList<>();
        components = new ArrayList<>();

        this.world = world;

        createBody();

        physicsC = new PhysicsComponent(this, this.body);
        addComponent(physicsC);

    }

    // Métodos para inicializar a física e a renderização (implementados nas subclasses)
    protected abstract void createBody();

    public abstract void update(float deltaTime);

    public abstract void render(SpriteBatch batch);

    protected void updateComponents(float delta) {
        for (Component component : components) {
            component.update(delta);
        }
    }

    public void dispose() {
        if (!spriteSheetDatahandlerList.isEmpty()) {
            for (SpriteSheetDatahandler spriteSheetDatahandler : spriteSheetDatahandlerList) {
                spriteSheetDatahandler.dispose();
            }
        }
    }

    public List<Component> getComponents() {
        return components;
    }

    public void addComponent(Component comp) {
        components.add(comp);
    }

    public <T> T getComponent(Class<T> componentClass) {
        for (Component comp : components) {
            if (componentClass.isInstance(comp)) {
                return componentClass.cast(comp);
            }
        }
        return null;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
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


    public List<SpriteSheetDatahandler> getSpriteSheetDatahandlerList() {
        return spriteSheetDatahandlerList;
    }

    public List<ObjectAnimationPlayer> getObjectAnimationPlayerList() {
        return objectAnimationPlayerList;
    }

    public PhysicsComponent getPhysicsC() {
        return physicsC;
    }
}
