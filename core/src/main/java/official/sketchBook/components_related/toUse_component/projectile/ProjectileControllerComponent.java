package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.projectiles_related.Projectile;

public class ProjectileControllerComponent extends Component {
    /// Projétil a quem pertence esse controlador
    private Projectile projectile;
    /// Tempo que o projétil permaneceu ativo
    private float timeAlive;
    /// Tempo que o projétil deverá permanecer vivo
    private float timeAliveLimit;

    /// Tempo que o projétil está inativo
    private float inactiveTime;
    /// Tempo que um projétil pode ficar inativo
    private float inactiveTimeLimit;

    /// Deve travar todos os eixos quando houver uma colisão
    private boolean stickOnCollision = false;
    /// Deve travar o eixo X ao colidir com uma parede
    private boolean stickToWall = false;
    /// Deve travar o eixo Y ao colidir com o chão
    private boolean stickToGround = false;
    /// Deve travar o eixo Y ao colidir com o teto
    private boolean stickToCeiling = false;
    /// Deve ser afetado pela gravidade
    private boolean affectedByGravity = false;

    private boolean lockX = false;
    private boolean lockY = false;

    private int groundContacts = 0;
    private int wallContacts = 0;
    private int ceilingContacts = 0;

    public ProjectileControllerComponent(Projectile projectile) {
        this.projectile = projectile;
        this.timeAliveLimit = 0f;
        this.timeAlive = 0f;
        this.inactiveTimeLimit = 3f;
        this.inactiveTime = 0f;
    }

    @Override
    public void update(float delta) {
        updateInactiveTime(delta);

        if (!projectile.isActive()) return;

        updateLifeTime(delta);
        updateAxisSpeedByLockState();

    }

    private void updateInactiveTime(float delta) {
        if (projectile.isActive()) {
            inactiveTime = 0;
            return;
        }

        if (inactiveTime < inactiveTimeLimit) {
            inactiveTime += delta;
            if (inactiveTime > inactiveTimeLimit) {
                inactiveTime = inactiveTimeLimit; // opcional, por segurança
            }
        }
    }

    public void reset() {
        this.groundContacts = 0;
        this.ceilingContacts = 0;
        this.wallContacts = 0;

        this.lockX = false;
        this.lockY = false;

        this.timeAlive = 0f;

        projectile.getPhysicsComponent().resetMovement();

        projectile.getBody().setTransform(
            projectile.getX(),
            projectile.getY(),
            0
        );

    }

    public void onHitEnvironment(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        updateAxisStatesByCollision();

        projectile.onEnvironmentCollision(contact, target);
    }

    public void onLeaveEnvironment(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        updateAxisStatesByCollision();

        projectile.onEnvironmentEndCollision(contact, target);
    }

    public void onHitEntity(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onEntityCollision(contact, target);
    }

    public void onLeaveEntity(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onEntityEndCollision(contact, target);
    }


    public void onHitProjectile(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onProjectileCollision(contact, target);

    }

    public void onLeaveProjectile(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onProjectileEndCollision(contact, target);
    }

    private void updateAxisStatesByCollision() {
        if (stickOnCollision) {
            setLockState(collidingAny());
        } else {
            lockX = stickToWall && wallContacts > 0;
            lockY = (stickToGround && groundContacts > 0) || (stickToCeiling && ceilingContacts > 0);
        }
    }

    private boolean collidingAny() {
        return groundContacts > 0 || wallContacts > 0 || ceilingContacts > 0;
    }

    private void setLockState(boolean state) {
        lockX = state;
        lockY = state;
    }

    /// Atualiza o estado de ativo com base no tempo ativo e incrementa o tempo ativo
    private void updateLifeTime(float delta) {
        timeAlive += delta;
        if (timeAlive >= timeAliveLimit) {
            projectile.die();
        }
    }

    /// Valida se algum dos eixos estão travados,
    /// se houver verificamos qual deles está e assim atualizamos o valor deles corretamente
    private void updateAxisSpeedByLockState() {
        // Travas de eixo
        if (lockX || lockY) {
            Vector2 velocity = projectile.getPhysicsComponent().getBody().getLinearVelocity();
            float x = lockX ? 0f : velocity.x;
            float y = lockY ? 0f : velocity.y;
            projectile.getPhysicsComponent().getBody().setLinearVelocity(x, y);
        }
    }

    /// Atualiza a posição do projétil e o lança para atingir um deslocamento no tempo desejado
    public void launch(Vector2 displacement, float timeSeconds) {
        // Posiciona o projétil corretamente
        projectile.getPhysicsComponent().getBody().setTransform(projectile.getX(), projectile.getY(), 0f);
        projectile.getPhysicsComponent().getBody().setLinearVelocity(0, 0); // reseta a velocidade

        // Aplica o impulso calculado para atingir o deslocamento no tempo desejado
        projectile.getPhysicsComponent().applyTimedTrajectory(displacement, timeSeconds);
    }

    public float getInactiveTime() {
        return inactiveTime;
    }

    public float getInactiveTimeLimit() {
        return inactiveTimeLimit;
    }

    public void setTimeAliveLimit(float timeAliveLimit) {
        this.timeAliveLimit = timeAliveLimit;
    }

    public void addGroundContact() {
        groundContacts++;
    }

    public void removeGroundContact() {
        groundContacts = Math.max(0, groundContacts - 1);
    }

    public void addWallContact() {
        wallContacts++;
    }

    public void removeWallContact() {
        wallContacts = Math.max(0, wallContacts - 1);
    }

    public void addCeilingContact() {
        ceilingContacts++;
    }

    public void removeCeilingContact() {
        ceilingContacts = Math.max(0, ceilingContacts - 1);
    }

    public boolean isCollidingGround() {
        return groundContacts > 0;
    }

    public boolean isCollidingWall() {
        return wallContacts > 0;
    }

    public boolean isCollidingCeiling() {
        return ceilingContacts > 0;
    }

    public void lockXAxis() {
        this.lockX = true;
    }

    public void unlockXAxis() {
        this.lockX = false;
    }

    public void lockYAxis() {
        this.lockY = true;
    }

    public void unlockYAxis() {
        this.lockY = false;
    }

    public void lockAllAxes() {
        this.lockX = true;
        this.lockY = true;
    }

    public void unlockAllAxes() {
        this.lockX = false;
        this.lockY = false;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
        this.projectile.getPhysicsComponent().setAffectedByGravity(affectedByGravity);
    }

    public boolean isXAxisLocked() {
        return lockX;
    }

    public boolean isYAxisLocked() {
        return lockY;
    }

    public void setStickToWall(boolean stickToWall) {
        this.stickToWall = stickToWall;
    }

    public void setStickOnCollision(boolean stickOnCollision) {
        this.stickOnCollision = stickOnCollision;
    }

    public void setStickToGround(boolean stickToGround) {
        this.stickToGround = stickToGround;
    }

    public void setStickToCeiling(boolean stickToCeiling) {
        this.stickToCeiling = stickToCeiling;
    }

    public float getTimeAliveLimit() {
        return timeAliveLimit;
    }

    public boolean isStickToWall() {
        return stickToWall;
    }

    public boolean isStickOnCollision() {
        return stickOnCollision;
    }

    public boolean isStickToGround() {
        return stickToGround;
    }

    public boolean isStickToCeiling() {
        return stickToCeiling;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public float getTimeAlive() {
        return timeAlive;
    }
}
