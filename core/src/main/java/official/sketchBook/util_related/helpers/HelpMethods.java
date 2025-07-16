package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.camera_related.CameraManager;
import official.sketchBook.gameState_related.states.Playing;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.values.GameObjectTag;

import static official.sketchBook.util_related.helpers.DirectionHelper.getDirection;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public class HelpMethods {

    public static TextureRegion obtainCurrentSpriteImage(Sprite sprite, int textureWidth, int textureHeight, Texture spriteSheet, boolean flipX, boolean flipY) {

        TextureRegion region = new TextureRegion(
            spriteSheet,
            sprite.getIndexX() * textureWidth,
            sprite.getIndexY() * textureHeight,
            textureWidth,
            textureHeight
        );

        region.flip(flipX,flipY);  // Inverte horizontalmente


        return region;
    }

    public static float scale(float value, float factor, boolean shouldMultiply) {
        return shouldMultiply ? value * factor : value / factor;
    }

    public static void updateCameraMovementParams(CameraManager cameraManager, float width, float height) {
        OrthographicCamera cam = cameraManager.getCamera();
        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cameraManager.maxLvlOffSetX = Math.max(0, Math.round(width - effectiveViewportWidth));
        cameraManager.maxLvlOffSetY = Math.max(0, Math.round(height - effectiveViewportHeight));
    }

    //valida se existe o listener antes de adicionar ou remover
    public static synchronized void handleContactListener(boolean remove, String listenerKey, ContactListener listener) {
        if (!remove) {

            if (Playing.multiContactListener.existListener(listenerKey))
                Playing.multiContactListener.removeListener(listenerKey);

            Playing.multiContactListener.addListener(listenerKey, listener);
        } else {
            if (Playing.multiContactListener.existListener(listenerKey))
                Playing.multiContactListener.removeListener(listenerKey);
        }
    }

    /// Obtemos a direção da colisão limitado em quatro pontos
    public static Direction getCollisionDirection(Projectile projectile, Contact contact) {
        //Pegamos a quantidade de pontos de contato a serem considerados
        final Vector2[] points = contact.getWorldManifold().getPoints();
        if (points == null || points.length == 0) return Direction.STILL;

        return getDirection(
            ((projectile.getX() + projectile.getRadius()) / PPM),
            ((projectile.getY() + projectile.getRadius()) / PPM),
            points[0].x,
            points[0].y,
            (projectile.getRadius() * 0.1f)
        );
    }

    public static GameObjectTag getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof GameObjectTag tag)) return null;
        return tag;
    }
}
