package official.sketchBook.engine.data_management_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.gameObject_related.base_model.PhysicalGameObject;
import official.sketchBook.engine.room_related.model.PlayableRoom;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.world_gen_blueprint.LayoutParam;
import official.sketchBook.engine.room_related.worldGeneration_related.generation.WorldGenerator;
import official.sketchBook.engine.room_related.worldGeneration_related.generation.WorldLayout;
import official.sketchBook.game.screen_related.PlayScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class GameObjectManagerBase {

    protected final List<LayoutParam> tmpLayoutList = new ArrayList<>();

    /// Gerenciador de salas
    protected PlayableRoomManager manager;
    /// Sala atual
    protected PlayableRoom currentRoom;
    /// Gerador de mundo
    protected WorldGenerator generator;
    /// Mundo físico
    protected World world;

    /// Dimensões de altura e largura para facilitar interpretação
    private int width, height;

    public GameObjectManagerBase(World world) {
        this.world = world;
        this.generator = new WorldGenerator();
        this.manager = new PlayableRoomManager(world, generator.getGrid());
    }

    protected void initRoom(List<LayoutParam> params) {
        WorldLayout layout = new WorldLayout(getWidth(), getHeight());

        layout.setBlueprintsByList(params);

        generator.applyLayoutToGrid(layout);

    }

    public void setCurrentRoom(int x, int y) {
        //Caso não exista sala ainda, criamos a nossa própria
        currentRoom = manager.initRoom(
            Objects.requireNonNullElseGet(currentRoom, () -> new PlayableRoom(world)),
            x,
            y
        );

        PlayScreen.worldWidth = currentRoom.getRoomData().getWidthInP();
        PlayScreen.worldHeight = currentRoom.getRoomData().getHeightInP();
    }

    /**
     * Atualiza o gerador de mundo e a grade existente (não pode existir nenhuma sala jogável sem iniciar primeiro)
     *
     * @param width Quantidade de salas na horizontal que podem existir
     * @param height Quantidade de salas na vertical que podem existir
     */
    protected void initWorldGrid(int width, int height) {
        this.generator.setDimensions(width, height);

        this.width = width;
        this.height = height;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void updateObjectsAfterStep() {
        currentRoom.updateObjectsAfterStep();
    }

    public void updateObjects(float delta) {
        currentRoom.updateObjects(delta);
    }

    public void renderObjects(SpriteBatch batch) {
        currentRoom.render(batch);
    }

    public void dispose() {
        currentRoom.dispose();
    }

    public void removeGameObject(PlayableRoom room, PhysicalGameObject object) {
        room.removeObject(object);
    }

    public void addGameObject(PlayableRoom room, PhysicalGameObject object) {
        room.addObject(object);
    }

    public PlayableRoom getCurrentRoom() {
        return currentRoom;
    }
}
