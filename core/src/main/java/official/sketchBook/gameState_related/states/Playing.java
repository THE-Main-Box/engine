package official.sketchBook.gameState_related.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.gameDataManagement_related.GameObjectManager;
import official.sketchBook.gameState_related.model.State;
import official.sketchBook.gameState_related.model.StateMethods;
import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.contactListeners.ProjectileContactListener;
import official.sketchBook.util_related.enumerators.states.GameState;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.helpers.MultiContactListener;
import official.sketchBook.util_related.registers.ProjectileCollisionRegister;

import static official.sketchBook.screen_related.PlayScreen.*;
import static official.sketchBook.util_related.helpers.HelpMethods.handleContactListener;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Debug.*;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Screen.*;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.*;

public class Playing extends State implements StateMethods {

    private final BitmapFont font = new BitmapFont();

    public static MultiContactListener multiContactListener = new MultiContactListener();

    public World world;
    public GameObjectManager objectManager;


    public Playing(PlayScreen game, CameraManager gameCameraManager, CameraManager uiCameraManager) {
        super(game, gameCameraManager, uiCameraManager);
        // Cria o world com a gravidade desejada (por exemplo, 8.2 m/s² para baixo)
        world = new World(new Vector2(0, -8.2f), true);

        //zoom padrão 0.7f
        gameCameraManager.setZoom(zoom);

        objectManager = new GameObjectManager(world);

        world.setContactListener(multiContactListener);
        this.setContactListeners();
    }

    private void setContactListeners() {
        handleContactListener(false, "projectile_listener", new ProjectileContactListener());
    }

    @Override
    public void update(float delta) {

        objectManager.updateObjects(delta);

        if (world != null) {
            world.step(
                delta,
                VELOCITY_ITERATIONS,
                POSITION_ITERATIONS
            );
        }

        objectManager.syncObjectsBodies();
        objectManager.updateEntityRayCast();

        ProjectileCollisionRegister.update();

        HelpMethods.updateCameraMovementParams(gameCameraManager, worldWidth, worldHeight);
        gameCameraManager.setEase(0.1f, 0.5f, 1f);
        gameCameraManager.trackObjectByOffset(objectManager.player.getX(), objectManager.player.getY());
    }

    @Override
    public void updateUi(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {

        objectManager.renderObjects(batch);

    }

    @Override
    public void draw(SpriteBatch batch, SpriteBatch uiBatch) {
        if (batch != null) {
            gameCameraManager.update();
            batch.setProjectionMatrix(gameCameraManager.getCamera().combined);

            // Inicia a renderização
            batch.begin();

            this.render(batch);

            batch.end();
        }

        if (uiBatch != null) {
            uiCameraManager.update();
            uiBatch.setProjectionMatrix(uiCameraManager.getCamera().combined);
            uiBatch.begin();

            renderUi(uiBatch);

            uiBatch.end();

        }


    }

    public void dispose() {
        super.dispose();

        font.dispose();

        objectManager.dispose();

        if (world != null) {
            world.dispose();
            world = null;
        }

    }

    @Override
    public void renderUi(SpriteBatch uiBatch) {

        // Exibe FPS e UPS na tela
        font.draw(uiBatch, "FPS: " + game.fps, 10, GAME_HEIGHT - 10);
        font.draw(uiBatch, "UPS: " + game.ups, 10, GAME_HEIGHT - 30);

        if (showProjectilesActive) {
            font.draw(
                uiBatch,
                "projectiles: "
                    + objectManager.getCurrentRoom().getProjectilePool().getTotalActiveProjectiles()
                    + " / "
                    + objectManager.getCurrentRoom().getProjectilePool().getTotalWaitingProjectiles(),
                10,
                GAME_HEIGHT - 50
            );

            if (showActiveProjectilePools) {
                font.draw(
                    uiBatch,
                    "pools: "
                        + objectManager.getCurrentRoom().getProjectilePool().getTotalPools(),
                    10,
                    GAME_HEIGHT - 70
                );
            }
        }
    }

    @Override
    public boolean handleMouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean handleTouchDown(int screenX, int screenY, int button) {
        return false;
    }

    @Override
    public boolean handleTouchUp(int screenX, int screenY, int button) {
        return false;
    }

    @Override
    public boolean handleTouchDragged(int screenX, int screenY, int button) {
        return false;
    }

    @Override
    public boolean handleKeyDown(int keycode) {

        if (keycode == Input.Keys.ESCAPE) {
            GameState.state = GameState.PAUSED;
        }

        if (keycode == Input.Keys.F1) {
            showHitBox = !showHitBox;
        }
        if (keycode == Input.Keys.F2) {
            showRayCast = !showRayCast;
        }
        if (keycode == Input.Keys.F3) {
            showProjectilesActive = !showProjectilesActive;
        }
        if (keycode == Input.Keys.F4) {
            showActiveProjectilePools = !showActiveProjectilePools;
        }

        return true;
    }

    @Override
    public boolean handleKeyUp(int keycode) {
        return false;
    }

    public World getWorld() {
        return world;
    }
}
