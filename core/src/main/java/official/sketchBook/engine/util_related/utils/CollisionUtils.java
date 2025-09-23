package official.sketchBook.engine.util_related.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.game.gameState_related.models.Playing;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;

import static official.sketchBook.engine.util_related.utils.DirectionUtils.getDirection;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;

public class CollisionUtils {

    private static final Vector2 tmpPos = new Vector2();

    public static Vector2 estimateProjectileContactPoint(Projectile projectile) {
        if (projectile == null) return null;

        Body body = projectile.getBody();
        if (body == null) return tmpPos.set(0f, 0f);

        // Mantém o comportamento original: retorna a posição do corpo
        return tmpPos.set(body.getPosition());
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
