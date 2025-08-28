package official.sketchBook.gameObject_related.base_model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.room_related.model.PlayableRoom;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {
    /// Sala física em que estamos instanciados
    protected PlayableRoom ownerRoom;

    /// Posição no mundo em pixels
    protected float x, y;
    /// Dimensões do corpo em pixels
    protected float width, height;

    /// Inversão de percepção do objeto em relação ao eixo
    protected boolean xAxisInverted, yAxisInverted;

    /// Lista de gerenciadores de dado de sprites
    /// <p>(a ordem é muito importante, deve ser a mesma da dos tocadores de animação)
    protected List<SpriteSheetDataHandler> spriteSheetDatahandlerList = new ArrayList<>();

    /// Lista de gerenciadores de animações
    /// <p> (a ordem é muito importante, deve ser a mesma da dos gerenciadores de spritesheet)
    protected List<ObjectAnimationPlayer> objectAnimationPlayerList = new ArrayList<>();

    /// Lista de componentes existentes
    protected List<Component> components = new ArrayList<>();


    public GameObject(float x, float y, float width, float height, boolean xAxisInverted, boolean yAxisInverted) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xAxisInverted = xAxisInverted;
        this.yAxisInverted = yAxisInverted;
    }

    /// Atualizamos o objeto em si
    public abstract void update(float deltaTime);

    /// Atualizamos os componentes salvos
    protected final void updateComponents(float delta) {
        for (Component component : components) {
            component.update(delta);
        }
    }

    /// Renderizamos todas as sprite sheets a partir da lista dos gerenciadores dos dados das sprites
    public void render(SpriteBatch batch) {
        if (!spriteSheetDatahandlerList.isEmpty() && !objectAnimationPlayerList.isEmpty()) {
            //renderizamos primeiro tudo o que tivermos para renderizar do objeto do jogador
            for (int i = 0; i < spriteSheetDatahandlerList.size(); i++) {
                spriteSheetDatahandlerList.get(i).setFacingForward(xAxisInverted);
                spriteSheetDatahandlerList.get(i).renderSprite(batch,
                    objectAnimationPlayerList.get(i).getCurrentSprite()
                );
            }
        }
    }

    /// Atualizamos os gerenciadores de animação dentro das listas
    protected final void updateAnimationPlayer(float delta) {
        for (ObjectAnimationPlayer animationPlayer : objectAnimationPlayerList) {
            animationPlayer.update(delta);
        }
    }

    public void dispose() {
        // validamos a lista de sprite sheets e permitimos assim que ela seja limpa corretamente
        if (!spriteSheetDatahandlerList.isEmpty()) {
            for (SpriteSheetDataHandler spriteSheetDatahandler : spriteSheetDatahandlerList) {
                spriteSheetDatahandler.dispose();
            }
        }
    }

    /// Adicionamos mais um componente à lista
    public void addComponent(Component comp) {
        components.add(comp);
    }

    public abstract void onEnterNewRoom();

    /// Adicionamos um tocador de animações
    public void addAnimationPlayer(ObjectAnimationPlayer aniPlayer) {
        if (aniPlayer != null) {
            objectAnimationPlayerList.add(aniPlayer);
        }
    }

    /// Adicionamos um gerenciador de sprites
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

    public boolean isxAxisInverted() {
        return xAxisInverted;
    }

    public void setxAxisInverted(boolean xAxisInverted) {
        this.xAxisInverted = xAxisInverted;
    }

    public boolean isyAxisInverted() {
        return yAxisInverted;
    }

    public void setyAxisInverted(boolean yAxisInverted) {
        this.yAxisInverted = yAxisInverted;
    }

    public PlayableRoom getOwnerRoom() {
        return ownerRoom;
    }

    public void setOwnerRoom(PlayableRoom ownerRoom) {
        this.ownerRoom = ownerRoom;
        this.onEnterNewRoom();
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
}
