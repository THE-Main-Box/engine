package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.room_related.model.PlayableRoom;

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
    protected List<SpriteSheetDataHandler> spriteSheetDatahandlerList;
    protected List<ObjectAnimationPlayer> objectAnimationPlayerList;

    protected boolean facingForward;

    //components related
    protected List<Component> components;

    protected float defFric, defRest, defDens;

    protected PlayableRoom ownerRoom;

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

        setBodyDefValues();
        createBody();

    }

    protected abstract void setBodyDefValues();

    // Métodos para inicializar a física e a renderização (implementados nas subclasses)
    protected abstract void createBody();

    public abstract void update(float deltaTime);

    protected void updateAnimationPlayer(float delta) {
        for (ObjectAnimationPlayer animationPlayer : objectAnimationPlayerList) {
            animationPlayer.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        if (!spriteSheetDatahandlerList.isEmpty() && !objectAnimationPlayerList.isEmpty()) {
            //renderizamos primeiro tudo o que tivermos para renderizar do objeto do jogador
            for (int i = 0; i < spriteSheetDatahandlerList.size(); i++) {
                spriteSheetDatahandlerList.get(i).setFacingForward(facingForward);
                spriteSheetDatahandlerList.get(i).renderSprite(
                    batch,
                    objectAnimationPlayerList.get(i).getCurrentSprite()
                );
            }
        }
    }

    protected void updateComponents(float delta) {
        for (Component component : components) {
            component.update(delta);
        }
    }

    public void dispose() {
        if (!spriteSheetDatahandlerList.isEmpty()) {
            for (SpriteSheetDataHandler spriteSheetDatahandler : spriteSheetDatahandlerList) {
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

    public boolean hasComponent(Class<? extends Component> type) {
        return components.stream().anyMatch(type::isInstance);
    }

    public PlayableRoom getOwnerRoom() {
        return ownerRoom;
    }

    public void setOwnerRoom(PlayableRoom ownerRoom) {
        this.ownerRoom = ownerRoom;
    }

    public float getDefFric() {
        return defFric;
    }

    public float getDefRest() {
        return defRest;
    }

    public float getDefDens() {
        return defDens;
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

    public void addSpriteSheetDataHandler(SpriteSheetDataHandler spriteHandler) {
        if (spriteHandler != null) {
            spriteSheetDatahandlerList.add(spriteHandler);
        }
    }

    // Métodos para acessar os principais elementos
    public SpriteSheetDataHandler getPrimarySpriteHandler() {
        return spriteSheetDatahandlerList.isEmpty() ? null : spriteSheetDatahandlerList.get(0);
    }

    public ObjectAnimationPlayer getPrimaryAnimationPlayer() {
        return objectAnimationPlayerList.isEmpty() ? null : objectAnimationPlayerList.get(0);
    }


    public List<SpriteSheetDataHandler> getSpriteSheetDatahandlerList() {
        return spriteSheetDatahandlerList;
    }

    public List<ObjectAnimationPlayer> getObjectAnimationPlayerList() {
        return objectAnimationPlayerList;
    }
}
