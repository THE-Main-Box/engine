package official.sketchBook.components_related.toUse_component.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.util_related.enumerators.directions.Direction;

public class ProjectileControllerComponent implements Component {
    /// Projétil a quem pertence esse controlador
    private Projectile projectile;

    /// Temporizador do tempo de vida do projétil
    private TimerComponent activeTimeLimit;

    private boolean colideWithSameTypeProjectiles = false;
    private boolean colideWithOtherProjectiles = false;

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

    private int downContact = 0;
    private int leftContact = 0;
    private int rightContact = 0;
    private int upContact = 0;

    private float bounceX = 0f;
    private float bounceY = 0f;

    public ProjectileControllerComponent(Projectile projectile) {
        this.projectile = projectile;
        this.activeTimeLimit = new TimerComponent();
    }

    @Override
    public void update(float delta) {
        if (!projectile.isActive()) return;

        updateLifeTime(delta);
        updateAxisMovementByLockState();

    }

    /// Atualiza o estado de ativo com base no tempo ativo e incrementa o tempo ativo
    private void updateLifeTime(float delta) {

        if (!activeTimeLimit.isRunning()) {
            activeTimeLimit.reset();
            activeTimeLimit.start();
        }

        //Caso tenha acabado
        if (activeTimeLimit.isFinished()) {
            //paramos e resetamos o componente de tempo de vida
            activeTimeLimit.stop();
            activeTimeLimit.reset();

            projectile.die();//matamos o projétil
        }

        //Atualizamos o temporizador
        activeTimeLimit.update(delta);
    }

    /// Valida se algum dos eixos estão travados,
    /// se houver verificamos qual deles está e assim atualizamos o valor deles corretamente
    private void updateAxisMovementByLockState() {

        // verificamos há alguma alteração no lockX e lockY
        if (lockX || lockY) {
            projectile.getBody().setLinearVelocity(
                lockX ? 0f : projectile.getBody().getLinearVelocity().x,
                lockY ? 0f : projectile.getBody().getLinearVelocity().y
            );

            //determinamos se podemos permitir o efeito de gravidade caso sejamos afetados
            // e não estejamos a travar o eixo vertical
            projectile.getPhysicsComponent().setAffectedByGravity(affectedByGravity && !lockY);
        }
    }


    public void reset() {
        resetCollisionCounters();

        this.lockX = false;
        this.lockY = false;

        //paramos e resetamos o tempo de vida caso estejamos desativando o projétil
        this.activeTimeLimit.stop();
        this.activeTimeLimit.reset();

        projectile.getPhysicsComponent().resetMovement();

        projectile.getBody().setTransform(
            projectile.getX(),
            projectile.getY(),
            0
        );

    }

    /// Zeramos o contador de colisões, tornando assim o projétil limpo de colisões
    public void resetCollisionCounters() {
        this.downContact = 0;
        this.upContact = 0;
        this.leftContact = 0;
        this.rightContact = 0;
    }

    public void onHitEnvironment(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        Direction direction = resolveCollisionDirection();
        updateAxisStatesByCollision();

        bounceFromEnvironment(direction);
        projectile.onEnvironmentCollision(contact, target);
    }

    public void onLeaveEnvironment(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        updateAxisStatesByCollision();

        projectile.onEnvironmentEndCollision(contact, target);
    }


    public void onHitEntity(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        Direction direction = resolveCollisionDirection();

        if (target instanceof Entity entity) {
            bounceFromEntity(direction, entity.getBody().getLinearVelocity());
            projectile.onEntityCollision(contact, entity);
        }

    }

    public void onLeaveEntity(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onEntityEndCollision(contact, target);
    }


    public void onHitProjectile(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        Direction direction = resolveCollisionDirection();

        if (target instanceof Projectile targetProjectile) {
            Vector2 targetVelocity = targetProjectile.getBody().getLinearVelocity();

            bounceFromProjectile(direction, targetVelocity);
            projectile.onProjectileCollision(contact, target);
        }
    }

    public void onLeaveProjectile(Object target, Contact contact) {
        if (!projectile.isActive()) return;

        projectile.onProjectileEndCollision(contact, target);
    }

    public void bounceFromEnvironment(Direction direction) {
        applyBounceStatic(direction, bounceX, bounceY);
    }

    public void bounceFromEntity(Direction direction, Vector2 entityVelocity) {
        applyBounceDynamic(direction, entityVelocity, bounceX, bounceY);
    }

    public void bounceFromProjectile(Direction direction, Vector2 otherProjectileVelocity) {
        applyBounceDynamic(direction, otherProjectileVelocity, bounceX, bounceY);
    }

    //TODO:impedir o efeito de colisão caso não possamos nos mover no eixo a ser afetado

    private void applyBounceStatic(Direction direction, float bounceX, float bounceY) {
        Vector2 v = projectile.getBody().getLinearVelocity().cpy();

        if ((direction.isLeft() || direction.isRight()) && !lockX && bounceX != 0f) {
            v.x = -v.x * bounceX;
        }
        if ((direction.isUp() || direction.isDown()) && !lockY && bounceY != 0f) {
            v.y = -v.y * bounceY;
        }


        projectile.getBody().setLinearVelocity(v);
    }

    private void applyBounceDynamic(Direction direction, Vector2 targetVelocity, float bounceX, float bounceY) {
        Vector2 selfVel = projectile.getBody().getLinearVelocity();
        Vector2 rel = selfVel.cpy().sub(targetVelocity);

        // idem: só inverte e multiplica se coeficiente não for zero
        if ((direction.isLeft() || direction.isRight()) && !lockX && bounceX > 0f) {
            rel.x = -rel.x * bounceX;
        }
        if ((direction.isUp() || direction.isDown()) && !lockY && bounceY > 0f) {
            rel.y = -rel.y * bounceY;
        }

        // volta para o referencial do alvo
        Vector2 finalVel = rel.add(targetVelocity);
        projectile.getBody().setLinearVelocity(finalVel);
    }

    private Direction resolveCollisionDirection() {
        if (downContact > 0) return Direction.DOWN;
        if (upContact > 0) return Direction.UP;
        if (leftContact > 0) return Direction.LEFT;
        if (rightContact > 0) return Direction.RIGHT;
        return Direction.STILL;
    }

    private void updateAxisStatesByCollision() {
        if (stickOnCollision) {
            setLockState(collidingAny());
        } else {
            lockX = stickToWall && (leftContact > 0 || rightContact > 0);
            lockY = (stickToGround && downContact > 0) || (stickToCeiling && upContact > 0);
        }
    }

    public void lockAllAxes() {
        this.lockX = true;
        this.lockY = true;
    }

    public void unlockAllAxes() {
        this.lockX = false;
        this.lockY = false;
    }

    /// Atualiza a posição do projétil e o lança para atingir um deslocamento no tempo desejado
    public void launch(Vector2 displacement, float timeSeconds) {
        projectile.setActive(true);

        // Posiciona o projétil corretamente
        projectile.getPhysicsComponent().getBody().setTransform(projectile.getX(), projectile.getY(), 0f);
        projectile.getPhysicsComponent().getBody().setLinearVelocity(0, 0); // reseta a velocidade

        // Aplica o impulso calculado para atingir o deslocamento no tempo desejado
        projectile.getPhysicsComponent().applyTimedTrajectory(displacement, timeSeconds);
    }

    public boolean isColideWithOtherProjectiles() {
        return colideWithOtherProjectiles;
    }

    public void setColideWithOtherProjectiles(boolean colideWithOtherProjectiles) {
        this.colideWithOtherProjectiles = colideWithOtherProjectiles;
    }

    public boolean isColideWithSameTypeProjectiles() {
        return colideWithSameTypeProjectiles;
    }

    public void setColideWithSameTypeProjectiles(boolean colideWithSameTypeProjectiles) {
        this.colideWithSameTypeProjectiles = colideWithSameTypeProjectiles;
    }

    public void setTimeAliveLimit(float targetTime) {
        this.activeTimeLimit.setTargetTime(targetTime);
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
        this.projectile.getPhysicsComponent().setAffectedByGravity(affectedByGravity);
    }

    public void setStickOnCollision(boolean stickOnCollision) {
        this.stickOnCollision = stickOnCollision;
    }

    public void setStickToWall(boolean stickToWall) {
        this.stickToWall = stickToWall;
    }

    public void setStickToGround(boolean stickToGround) {
        this.stickToGround = stickToGround;
    }

    public void setStickToCeiling(boolean stickToCeiling) {
        this.stickToCeiling = stickToCeiling;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public void setBounceX(float bounceX) {
        this.bounceX = bounceX;
    }

    public void setBounceY(float bounceY) {
        this.bounceY = bounceY;
    }

    public void addDownContact() {
        downContact++;
    }

    public void addUpContact() {
        upContact++;
    }

    public void addLeftContact() {
        leftContact++;
    }

    public void addRightContact() {
        rightContact++;
    }


    public void removeDownContact() {
        downContact = Math.max(0, downContact - 1);
    }

    public void removeUpContact() {
        upContact = Math.max(0, upContact - 1);
    }

    public void removeLeftContact() {
        leftContact = Math.max(0, leftContact - 1);
    }

    public void removeRightContact() {
        rightContact = Math.max(0, rightContact - 1);
    }


    private boolean collidingAny() {
        return downContact > 0 || upContact > 0 || leftContact > 0 || rightContact > 0;
    }

    private void setLockState(boolean state) {
        if (state) {
            lockAllAxes();
        } else {
            unlockAllAxes();
        }
    }
}
