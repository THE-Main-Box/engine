package official.sketchBook.engine.util_related.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.game.gameState_related.models.Playing;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;

import static official.sketchBook.engine.util_related.utils.DirectionUtils.getDirection;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;

public class CollisionUtils {
    // Vetores pré-alocados
    private static final Vector2 tmpDisplacement = new Vector2();
    private static final Vector2 tmpVel = new Vector2();
    private static final Vector2 tmpGrav = new Vector2();
    private static final Vector2 tmpPrevPos = new Vector2();
    private static final Vector2 tmpDir = new Vector2();
    private static final Vector2 tmpPos = new Vector2();
    private static final Vector2 tmpRayStart = new Vector2();
    private static final Vector2 tmpRayEnd = new Vector2();
    private static final Vector2 tmpRayCoefficient = new Vector2();
    private static final Vector2 tmpImpact = new Vector2();

    public static Vector2 estimateProjectileContactPointWithRayCast(
        Projectile projectile,
        float fixedTimeStep,
        RayCastUtils rayCastUtils
    ) {
        if (projectile == null) return null;

        Body body = projectile.getBody();
        if (body == null || rayCastUtils == null) return null;

        tmpVel.set(body.getLinearVelocity());
        tmpPos.set(body.getPosition());
        tmpGrav.set(body.getWorld().getGravity());

        if (tmpVel.isZero(0.0001f)) return tmpPos;

        float halfStepSquared = 0.5f * fixedTimeStep * fixedTimeStep * body.getGravityScale();

        tmpDisplacement.set(tmpVel).scl(fixedTimeStep);
        tmpDisplacement.add(tmpGrav.x * halfStepSquared, tmpGrav.y * halfStepSquared);

        tmpPrevPos.set(tmpPos).sub(tmpDisplacement);

        tmpDir.set(tmpPos).sub(tmpPrevPos);
        if (tmpDir.len2() < 1e-6f) {
            if (tmpVel.len2() > 0) tmpDir.set(tmpVel);
            else tmpDir.set(1f, 0f);
        }
        tmpDir.nor();

        float radiusMeters = projectile.getRadius() / PPM;
        tmpRayCoefficient.set(tmpDir).scl(radiusMeters);

        tmpRayStart.set(tmpPrevPos).sub(tmpRayCoefficient);
        tmpRayEnd.set(tmpPos).add(tmpRayCoefficient);

        final boolean[] hit = {false};

        rayCastUtils.castRay(tmpRayStart, tmpRayEnd, rayData -> {
            if (rayData.fixture() != null && rayData.fixture().getBody().isActive()) {
                tmpImpact.set(rayData.point());
                hit[0] = true;
            }
        });

        return hit[0] ? tmpImpact : tmpPos;
    }



    /// Atualiza os filtros de mascara e categoria de colisão logo após a criação da body
    public static void updateCollisionFilters(Body body, short categoryBits, short maskBits) {
        for (Fixture f : body.getFixtureList()) {
            Filter filter = f.getFilterData();
            filter.categoryBits = categoryBits;
            filter.maskBits = maskBits;
            f.setFilterData(filter);
        }
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

}
