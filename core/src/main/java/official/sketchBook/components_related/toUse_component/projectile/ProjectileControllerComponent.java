package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.helpers.DirectionHelper;

import static official.sketchBook.util_related.helpers.DirectionHelper.getDirection;

public class ProjectileControllerComponent implements Component {

    /// Projétil a quem pertence esse controlador
    private final Projectile projectile;

    /// Temporizador do tempo de vida do projétil
    private final TimerComponent activeTimeLimit;

    /// Controle de travamento por colisão
    private boolean stickOnCollision = false;
    private boolean stickToWall = false;
    private boolean stickToGround = false;
    private boolean stickToCeiling = false;

    /// Propriedades físicas
    private boolean affectedByGravity = false;
    private boolean canRotate = false;
    private boolean lockX = false;
    private boolean lockY = false;

    /// Coeficiente de restituição
    private float bounceX = 0f;
    private float bounceY = 0f;

    /// Direção da última colisão com uma entidade
    public Direction entityCollisionDirection;
    /// Direção da última colisão com o ambiente
    public Direction enviromentCollisionDirection;
    /// Direção da última colisão com outro projétil
    public Direction projectileCollisionDirection;

    public ProjectileControllerComponent(Projectile projectile) {
        this.projectile = projectile;
        this.activeTimeLimit = new TimerComponent();
    }

    @Override
    public void update(float delta) {
        if (!projectile.isActive()) return;

        updateLifeTime(delta);
        updateAxisMovementByLockState();

        resetCollisionDirections();
    }

    /// Atualiza o estado de ativo com base no tempo ativo e incrementa o tempo ativo
    private void updateLifeTime(float delta) {
        if (!activeTimeLimit.isRunning()) {
            activeTimeLimit.reset();
            activeTimeLimit.start();
        }

        if (activeTimeLimit.isFinished()) {
            activeTimeLimit.stop();
            activeTimeLimit.reset();
            projectile.release(); // mata o projétil
        }

        activeTimeLimit.update(delta);

    }

    private boolean lastLockX = false;
    private boolean lastLockY = false;

    /// Aplica travamentos de movimento com base nos eixos
    private void updateAxisMovementByLockState() {
        // Checa se há mudança de estado
        if (lockX != lastLockX || lockY != lastLockY) {
            lastLockX = lockX;
            lastLockY = lockY;

            Vector2 cachedVelocity = projectile.getBody().getLinearVelocity();

            projectile.getBody().setLinearVelocity(
                lockX ? 0f : cachedVelocity.x,
                lockY ? 0f : cachedVelocity.y
            );
        }

        // Sempre aplica (confia que a setter interna já tem controle)
        projectile.getPhysicsComponent().setAffectedByGravity(affectedByGravity && !lockY);
    }

    /// Reinicializa projétil para reuso
    public void reset() {
        if (projectile.isReset()) return;

        resetCollisionDirections();
        unlockAllAxes();

        activeTimeLimit.stop();
        activeTimeLimit.reset();

        projectile.getPhysicsComponent().resetMovement();
        projectile.getBody().setTransform(projectile.getX(), projectile.getY(), projectile.getRotation());
    }

    protected void resetCollisionDirections(){
        projectileCollisionDirection =
            enviromentCollisionDirection =
                entityCollisionDirection = Direction.STILL;
    }

    // ----- COLISÕES COM O AMBIENTE -----

    public void onHitEnvironment(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        updateAxisStatesByCollision(entityCollisionDirection);
        bounceFromEnvironment(entityCollisionDirection);

        projectile.onEnvironmentCollision(contact, target);
    }

    public void onLeaveEnvironment(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        updateAxisStatesByCollision(enviromentCollisionDirection);
        projectile.onEnvironmentEndCollision(contact, target);
    }

    // ----- COLISÕES COM ENTIDADES -----

    public void onHitEntity(Entity entity, Contact contact) {
        if (!projectile.isActive()) return;

        bounceFromEntity(entityCollisionDirection, entity.getBody().getLinearVelocity());
        projectile.onEntityCollision(contact, entity);
    }

    public void onLeaveEntity(Entity entity, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onEntityEndCollision(contact, entity);
    }

    // ----- COLISÕES COM OUTROS PROJÉTEIS -----

    public void onHitProjectile(Projectile other, Contact contact) {
        if (!projectile.isActive()) return;

        bounceFromProjectile(getCollisionDirection(contact), other.getBody().getLinearVelocity());
        projectile.onProjectileCollision(contact, other);

    }

    public void onLeaveProjectile(Projectile other, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onProjectileEndCollision(contact, other);
    }

    // ----- BOUNCE -----

    public void bounceFromEnvironment(Direction direction) {
        applyBounceStatic(direction, bounceX, bounceY);
    }

    public void bounceFromEntity(Direction direction, Vector2 entityVelocity) {
        applyBounceDynamic(direction, entityVelocity, bounceX, bounceY);
    }

    public void bounceFromProjectile(Direction direction, Vector2 otherVelocity) {
        applyBounceDynamic(direction, otherVelocity, bounceX, bounceY);
    }

    // 🔁 SUGESTÃO: mover para um utilitário físico (e.g., BounceHandler)
    private void applyBounceStatic(Direction direction, float bx, float by) {
        Vector2 v = projectile.getBody().getLinearVelocity().cpy();

        if ((direction.isLeft() || direction.isRight()) && !lockX && bx != 0f) {
            v.x = -v.x * bx;
        }
        if ((direction.isUp() || direction.isDown()) && !lockY && by != 0f) {
            v.y = -v.y * by;
        }

        projectile.getBody().setLinearVelocity(v);
    }

    // 🔁 SUGESTÃO: mover para um utilitário físico (e.g., BounceHandler)
    private void applyBounceDynamic(Direction direction, Vector2 otherVel, float bx, float by) {
        Vector2 selfVel = projectile.getBody().getLinearVelocity();
        Vector2 rel = selfVel.cpy().sub(otherVel);

        if ((direction.isLeft() || direction.isRight()) && !lockX && bx > 0f) {
            rel.x = -rel.x * bx;
        }
        if ((direction.isUp() || direction.isDown()) && !lockY && by > 0f) {
            rel.y = -rel.y * by;
        }

        projectile.getBody().setLinearVelocity(rel.add(otherVel));
    }

    /// Obtemos a direção da colisão
    public Direction getCollisionDirection(Contact contact) {
        //Pegamos a quantidade de pontos de contato a serem considerados
        final Vector2[] points = contact.getWorldManifold().getPoints();
        if (points == null || points.length == 0) return Direction.STILL;

        return getDirection(projectile.getBody().getPosition(), points[0], projectile.getRadius() * 0.1f);
    }


    /// Atualiza os eixos que devem ser travados com base na direção de colisão
    private void updateAxisStatesByCollision(Direction direction) {
        if (stickOnCollision) {
            lockAllAxes();
            return;
        }

        lockX = stickToWall && (direction == Direction.LEFT || direction == Direction.RIGHT);
        lockY = (stickToGround && direction == Direction.DOWN) || (stickToCeiling && direction == Direction.UP);
    }

    // ----- LOCK CONTROLS -----

    public void lockAllAxes() {
        this.lockX = true;
        this.lockY = true;
    }

    public void unlockAllAxes() {
        this.lockX = false;
        this.lockY = false;
    }

    private void setLockState(boolean state) {
        if (state) lockAllAxes();
        else unlockAllAxes();
    }

    // ----- DISPARO -----

    public void launch(Vector2 displacement, float timeSeconds) {
        projectile.setActive(true);
        projectile.getOwnerPool().addToActive(projectile);

        projectile.getPhysicsComponent().getBody().setTransform(projectile.getX(), projectile.getY(), projectile.getRotation());
        projectile.getPhysicsComponent().getBody().setLinearVelocity(0, 0);

        projectile.getPhysicsComponent().applyTimedTrajectory(displacement, timeSeconds);
    }

    // ----- GETTERS/SETTERS -----

    public Projectile getProjectile() {
        return projectile;
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    public void setTimeAliveLimit(float seconds) {
        this.activeTimeLimit.setTargetTime(seconds);
    }

    public void setAffectedByGravity(boolean affected) {
        this.affectedByGravity = affected;
        this.projectile.getPhysicsComponent().setAffectedByGravity(affected);
    }

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
        this.projectile.getBody().setFixedRotation(!canRotate);
    }

    public void setStickOnCollision(boolean b) {
        this.stickOnCollision = b;
    }

    public void setStickToWall(boolean b) {
        this.stickToWall = b;
    }

    public void setStickToGround(boolean b) {
        this.stickToGround = b;
    }

    public void setStickToCeiling(boolean b) {
        this.stickToCeiling = b;
    }

    public void setBounceX(float x) {
        this.bounceX = x;
    }

    public void setBounceY(float y) {
        this.bounceY = y;
    }

}
