package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.components_related.collisionBehaviorComponents.IEnterCollisionBehavior;
import official.sketchBook.components_related.collisionBehaviorComponents.IExitCollisionBehavior;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.util.collision.CollisionDataBuffer;

import static official.sketchBook.util_related.helpers.DirectionHelper.getDirection;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;


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
    private boolean continuousCollisionDetection = false;
    private boolean manageExitCollision = false;
    private boolean sensorProjectile = false;

    private boolean applyLockLogicToEntities = false;

    /// Propriedades físicas
    private boolean affectedByGravity = false;
    private boolean canRotate = false;

    public CollisionDataBuffer lastContactBeginData;
    public CollisionDataBuffer lastContactEndData;

    /// Estamos a acertar alguma coisa
    public boolean colliding;

    /// Armazenamento de métodos de entrada de colisão
    private final Array<IEnterCollisionBehavior> enterCollisionEnvBehaviors = new Array<>(false, 2);
    /// Armazenamento de métodos de saída de colisão
    private final Array<IExitCollisionBehavior> exitCollisionEnvBehaviors = new Array<>(false, 2);

    /// Armazenamento de métodos de entrada de colisão
    private final Array<IEnterCollisionBehavior> enterCollisionEnttBehaviors = new Array<>(false, 2);
    /// Armazenamento de métodos de saída de colisão
    private final Array<IExitCollisionBehavior> exitCollisionEnttBehaviors = new Array<>(false, 2);

    /// Armazenamento de métodos de entrada de colisão
    private final Array<IEnterCollisionBehavior> enterCollisionProjBehaviors = new Array<>(false, 2);
    /// Armazenamento de métodos de saída de colisão
    private final Array<IExitCollisionBehavior> exitCollisionProjBehaviors = new Array<>(false, 2);

    public ProjectileControllerComponent(Projectile projectile) {
        this.projectile = projectile;
        this.activeTimeLimit = new TimerComponent();

        lastContactEndData = new CollisionDataBuffer();
        lastContactBeginData = new CollisionDataBuffer();

    }

    public void init() {

    }

    @Override
    public void update(float delta) {
        if (!projectile.isActive()) return;

        updateLifeTime(delta);

    }

    public void handleBufferedEndCollision() {
        if (lastContactEndData.isReset()) return;

        switch (lastContactEndData.getLastCollisionWith().type()) {
            case PROJECTILE -> onLeaveProjectile(
                (Projectile) lastContactEndData.getLastCollisionWith().owner(),
                lastContactEndData.getLastContact()
            );
            case ENTITY -> onLeaveEntity(
                (Entity) lastContactEndData.getLastCollisionWith().owner(),
                lastContactEndData.getLastContact()
            );
            case ENVIRONMENT -> onLeaveEnvironment(
                lastContactEndData.getLastCollisionWith().owner(),
                lastContactEndData.getLastContact()
            );
        }
    }

    public void handleBufferedCollision() {
        if (lastContactBeginData.isReset()) return;

        switch (lastContactBeginData.getLastCollisionWith().type()) {
            case PROJECTILE -> onHitProjectile(
                (Projectile) lastContactBeginData.getLastCollisionWith().owner(),
                lastContactBeginData.getLastContact()
            );
            case ENTITY -> onHitEntity(
                (Entity) lastContactBeginData.getLastCollisionWith().owner(),
                lastContactBeginData.getLastContact()
            );
            case ENVIRONMENT -> onHitEnvironment(
                lastContactBeginData.getLastCollisionWith().owner(),
                lastContactBeginData.getLastContact()
            );
        }
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

    /// Reinicializa projétil para reuso
    public void reset() {
        if (projectile.isReset()) return;

        resetCollisionDirections();

        activeTimeLimit.stop();
        activeTimeLimit.reset();

        projectile.getPhysicsComponent().resetMovement();
        projectile.getBody().setTransform(projectile.getX(), projectile.getY(), projectile.getRotation());
    }

    protected void resetCollisionDirections() {
        lastContactEndData.reset();
        lastContactBeginData.reset();
        colliding = false;
    }

    // ----- COLISÕES COM O AMBIENTE -----

    public void onHitEnvironment(Object target, Contact contact) {
        for (int i = 0; i < enterCollisionEnvBehaviors.size; i++) {
            enterCollisionEnvBehaviors.get(i).onCollisionEnter(this, contact, target);
        }
    }

    public void onLeaveEnvironment(Object target, Contact contact) {
        for (int i = 0; i < exitCollisionEnvBehaviors.size; i++) {
            exitCollisionEnvBehaviors.get(i).onCollisionExit(this, contact, target);
        }
    }

    // ----- COLISÕES COM ENTIDADES -----

    public void onHitEntity(Entity entity, Contact contact) {
        for (int i = 0; i < enterCollisionEnttBehaviors.size; i++) {
            enterCollisionEnttBehaviors.get(i).onCollisionEnter(this, contact, entity);
        }
    }

    public void onLeaveEntity(Entity entity, Contact contact) {
        for (int i = 0; i < exitCollisionEnttBehaviors.size; i++) {
            exitCollisionEnttBehaviors.get(i).onCollisionExit(this, contact, entity);
        }
    }

    // ----- COLISÕES COM OUTROS PROJÉTEIS -----

    public void onHitProjectile(Projectile other, Contact contact) {
        for (int i = 0; i < enterCollisionProjBehaviors.size; i++) {
            enterCollisionProjBehaviors.get(i).onCollisionEnter(this, contact, other);
        }
    }

    public void onLeaveProjectile(Projectile other, Contact contact) {
        for (int i = 0; i < exitCollisionProjBehaviors.size; i++) {
            exitCollisionProjBehaviors.get(i).onCollisionExit(this, contact, other);
        }
    }

    /// Obtemos a direção da colisão
    public Direction getCollisionDirection(Contact contact) {
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

    // ----- DISPARO -----

    public void launch(Vector2 displacement, float timeSeconds) {
        projectile.setActive(true);
        projectile.getOwnerPool().addToActive(projectile);

        projectile.getPhysicsComponent().getBody().setTransform(projectile.getX(), projectile.getY(), projectile.getRotation());
        projectile.getPhysicsComponent().getBody().setLinearVelocity(0, 0);

        projectile.getPhysicsComponent().applyTimedTrajectory(displacement, timeSeconds);
    }

    // ----- GETTERS/SETTERS -----

    public void addBeginCollisionBehavior(IEnterCollisionBehavior behavior, Array<IEnterCollisionBehavior> behaviorArray) {
        behaviorArray.add(behavior);
    }

    public void addEndCollisionBehavior(IExitCollisionBehavior behavior, Array<IExitCollisionBehavior> behaviorArray) {
        behaviorArray.add(behavior);
    }

    public Array<IEnterCollisionBehavior> getEnterCollisionEnvBehaviors() {
        return enterCollisionEnvBehaviors;
    }

    public Array<IExitCollisionBehavior> getExitCollisionEnvBehaviors() {
        return exitCollisionEnvBehaviors;
    }

    public Array<IEnterCollisionBehavior> getEnterCollisionEnttBehaviors() {
        return enterCollisionEnttBehaviors;
    }

    public Array<IExitCollisionBehavior> getExitCollisionEnttBehaviors() {
        return exitCollisionEnttBehaviors;
    }

    public Array<IEnterCollisionBehavior> getEnterCollisionProjBehaviors() {
        return enterCollisionProjBehaviors;
    }

    public Array<IExitCollisionBehavior> getExitCollisionProjBehaviors() {
        return exitCollisionProjBehaviors;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    public boolean isStickOnCollision() {
        return stickOnCollision;
    }

    public boolean isStickToWall() {
        return stickToWall;
    }

    public boolean isStickToGround() {
        return stickToGround;
    }

    public boolean isStickToCeiling() {
        return stickToCeiling;
    }

    public void setTimeAliveLimit(float seconds) {
        this.activeTimeLimit.setTargetTime(seconds);
    }

    public boolean isApplyLockLogicToEntities() {
        return applyLockLogicToEntities;
    }

    public void setApplyLockLogicToEntities(boolean applyLockLogicToEntities) {
        this.applyLockLogicToEntities = applyLockLogicToEntities;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
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

    public boolean isContinuousCollisionDetection() {
        return continuousCollisionDetection;
    }

    public void setContinuousCollisionDetection(boolean continuousCollisionDetection) {
        this.continuousCollisionDetection = continuousCollisionDetection;
    }

    public boolean isManageExitCollision() {
        return manageExitCollision;
    }

    public boolean isSensorProjectile() {
        return sensorProjectile;
    }

    public void setSensorProjectile(boolean sensorProjectile) {
        this.sensorProjectile = sensorProjectile;
        for (Fixture fix : projectile.getBody().getFixtureList()) {
            fix.setSensor(sensorProjectile);
        }
    }

    public void setManageExitCollision(boolean manageExitCollision) {
        this.manageExitCollision = manageExitCollision;
    }
}
