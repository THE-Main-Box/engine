package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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

    /// Usa matemática para estipular onde que o projétil estava quando colidiu
    public static Vector2 estimateProjectileContactPointWithRayCast(
        Projectile projectile,
        float fixedTimeStep,
        RayCastHelper rayCastHelper
    ) {
        if (projectile == null) return null;

        Body body = projectile.getBody();
        if (body == null || rayCastHelper == null) return null;

        Vector2 currentPos = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();

        // Evita cálculo se quase parado
        if (velocity.isZero(0.0001f)) return new Vector2(currentPos);

        float gravityScale = body.getGravityScale();
        Vector2 gravity = body.getWorld().getGravity();

        // Calcular deslocamento com MRUV: s = v*t + 1/2 * a * t^2
        float halfStepSquared = 0.5f * fixedTimeStep * fixedTimeStep;

        // Reuso vetor para evitar gc
        Vector2 displacement = new Vector2(velocity).scl(fixedTimeStep);
        displacement.add(new Vector2(gravity).scl(gravityScale * halfStepSquared));

        Vector2 previousPos = new Vector2(currentPos).sub(displacement);

        // Calcular direção do movimento, fallback para velocity se direção for zero
        Vector2 rawDir = new Vector2(currentPos).sub(previousPos);
        Vector2 direction;
        if (rawDir.len2() > 1e-6f) {
            direction = rawDir.nor();
        } else {
            direction = new Vector2(velocity).nor();
            if (direction.isZero()) direction.set(1, 0); // fallback padrão
        }

        float radiusMeters = projectile.getRadius() / PPM;

        Vector2 rayStart = new Vector2(previousPos).sub(direction.scl(radiusMeters));
        Vector2 rayEnd = new Vector2(currentPos).add(new Vector2(direction).scl(radiusMeters));

        final Vector2 impactPoint = new Vector2();
        final boolean[] hit = {false};

        rayCastHelper.castRay(rayStart, rayEnd, rayData -> {
            if (rayData.fixture() != null && rayData.fixture().getBody().isActive()) {
                impactPoint.set(rayData.point());
                hit[0] = true;
            }
        });

        return hit[0] ? impactPoint : new Vector2(currentPos);
    }

    /// Atualiza os filtros de mascara e categoria de colisão logo após a criação da body
    public static void updateCollisionFilters(Body body, short categoryBits, short maskBits) {
        for (Fixture f : body.getFixtureList()) {
            Filter filter = f.getFilterData();
            filter.categoryBits = categoryBits;
            filter.maskBits     = maskBits;
            f.setFilterData(filter);
        }
    }

    /// Obtém a tag presente em uma body dentro de uma fixture
    public static GameObjectTag getTag(Fixture fixture) {
        if (fixture == null || !(fixture.getBody().getUserData() instanceof GameObjectTag tag)) return null;
        return tag;
    }
}
