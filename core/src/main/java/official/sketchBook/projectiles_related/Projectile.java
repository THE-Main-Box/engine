package official.sketchBook.projectiles_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.collisionBehaviorComponents.StickToSurfaceBehavior;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.components_related.toUse_component.projectile.ProjectilePhysicsComponent;
import official.sketchBook.customComponents_related.CustomPool;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.gameObject_related.entities.Player;
import official.sketchBook.projectiles_related.util.ProjectilePool;
import official.sketchBook.util_related.enumerators.types.FactionTypes;
import official.sketchBook.util_related.enumerators.types.ObjectType;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.values.GameObjectTag;

import static official.sketchBook.util_related.enumerators.layers.CollisionLayers.*;
import static official.sketchBook.util_related.helpers.HelpMethods.updateCollisionFilters;

public abstract class Projectile implements CustomPool.Poolable {

    /// Raio do projétil
    protected float radius;

    /// Posição do projétil em pixels
    protected float x, y;

    /// Rotação do projétil
    protected float r;

    /// Componente de controle, tem um em cada projétil
    protected ProjectileControllerComponent controllerComponent;

    /// Componente de física próprio do projétil
    private final ProjectilePhysicsComponent physicsComponent;

    /// Gerenciador de sprites
    protected SpriteSheetDataHandler spriteSheetDatahandler;

    /// Gerenciador de animações
    protected ObjectAnimationPlayer animationPlayer;

    /// Dono do projétil, apenas uma entidade pode usar um projétil
    protected Entity owner;

    /// Corpo do projétil
    protected Body body;

    /// Mundo físico externo onde o projétil está presente
    protected World world;

    /// Valores padrão para a criação da body
    protected float defFric, defRest, defDens;

    /// Pool dona do projétil
    protected ProjectilePool<?> ownerPool;

    /// Lista de componentes
    protected Array<Component> components;

    /// Flag de estado de vida
    private boolean reset, active;

    /// O que o projétil é
    protected short category;

    /// Com quem pode interagir
    protected short mask;

    /// Flag que diz se o projétil está virado para a direita ou não
    protected boolean facingForward;

    protected boolean enemyProjectile;

    public Projectile(World world) {
        this.world = world;
        this.active = false;
        this.components = new Array<>();

        this.setBodyDefValues();
        this.createBody();

        this.controllerComponent = new ProjectileControllerComponent(this);
        this.addComponent(controllerComponent);

        this.physicsComponent = new ProjectilePhysicsComponent(this);
        this.addComponent(physicsComponent);

        initContactBehaviorComponents();

    }

    /// Inicia e aloca os objetos de classe de comportamento de colisão
    private void initContactBehaviorComponents() {

        StickToSurfaceBehavior stickBC = new StickToSurfaceBehavior();
        controllerComponent.getEnterCollisionEnvBehaviors().add(stickBC);
        controllerComponent.getEnterCollisionEnttBehaviors().add(stickBC);

    }

    /**
     * Inicia as informações dinâmicas do projétil
     * é preciso passar o tempo de vida e outros valores que precisam ser iniciados dinamicamente
     */
    public void init(Entity owner) {
        this.owner = owner;
        this.reset = false;

        this.controllerComponent.init();

        updateAllyEnemyMark(owner.getFaction());
        this.setMask((short) (SENSOR.bit() | ENVIRONMENT.bit()));

        updateCollisionFilters(body, category, mask);
    }

    /**
     * Inicia o comportamento padrão do projétil(chamado no construtor)
     *
     * @param stickOnCollision         trava todos os eixos quando detectamos uma colisão
     * @param stickOnLeftWall          trava o eixo X quando detectamos uma colisão na horizontal
     * @param stickOnCeiling           trava o eixo Y quando detectamos uma colisão vinda da parte de cima do projétil
     * @param stickOnGround            trava o eixo Y quando detectamos uma colisão vinda da parte de baixo do projétil
     * @param affectedByGravity        se o projétil é afetado ou não pela constante da gravidade
     * @param continuousCollisionCheck se devemos continuar a lidar com as colisões sem parar enquanto houver
     * @param manageExit               se temos métodos para serem chamados ao sair de uma colisão
     * @param canRotate                se o projétil pode rotacionar por conta própria
     */
    protected void initBodyBehavior(
        boolean stickOnCollision,
        boolean stickOnLeftWall,
        boolean stickOnRightWall,
        boolean stickOnCeiling,
        boolean stickOnGround,
        boolean affectedByGravity,
        boolean canRotate,
        boolean manageExit,
        boolean continuousCollisionCheck,
        boolean applyToEntities
    ) {
        //Validar colisão de forma contínua
        this.controllerComponent.setContinuousCollisionDetection(continuousCollisionCheck);
        //Aplicar lógica de colisão a entidades
        this.controllerComponent.setApplyLockLogicToEntities(applyToEntities);
        //é afetado por gravidade
        this.controllerComponent.setAffectedByGravity(affectedByGravity);
        //Deve parar de se mover ao colidir
        this.controllerComponent.setStickOnCollision(stickOnCollision);
        //É sensor por padrão
        this.controllerComponent.setSensorProjectile(true);
        //Deve lidar com a saída da colisão
        this.controllerComponent.setManageExitCollision(manageExit);
        //deve parar de mover ao acertar o teto
        this.controllerComponent.setStickToCeiling(stickOnCeiling);
        //Deve parar de se mover ao acertar o chão
        this.controllerComponent.setStickToGround(stickOnGround);
        //Deve parar de se mover ao acertar a parede da esquerda
        this.controllerComponent.setStickToLeftWall(stickOnLeftWall);
        //Deve parar de se mover ao acertar a parede da direita
        this.controllerComponent.setStickToRightWall(stickOnRightWall);
        //Se pode girar em volta do próprio eixo
        this.controllerComponent.setCanRotate(canRotate);

    }

    /// Iniciamos os valores padrão da body, mas cada projétil implementa por conta própria
    protected abstract void setBodyDefValues();

    /// Cria uma body padrão para todos os projéteis
    protected void createBody() {
        body = BodyCreatorHelper.createCircle(
            world,
            new Vector2(x, y),
            radius,
            BodyDef.BodyType.DynamicBody,
            defDens, // density
            defFric,   // friction
            defRest, // restitution
            category,
            mask
        );
        body.setBullet(true); // Importante para colisões de alta velocidade
        body.setSleepingAllowed(true);
        body.setUserData(new GameObjectTag(ObjectType.PROJECTILE, this));
    }

    public void update(float deltaTime) {
        updateComponents(deltaTime);
    }


    /// Atualiza todos os componentes existentes dentro do objeto
    private void updateComponents(float delta) {
        if (!active) return;        // Nenhum componente precisa rodar se está inativo
        for (Component c : components) {
            c.update(delta);
        }
    }

    private void updateAllyEnemyMark(FactionTypes faction) {
        if (faction == null) {
            throw new NullPointerException(
                "Valor de facção da entidade [" +
                    owner.getClass().getSimpleName() +
                    "] não pode ser nulo"
            );
        }

        if (faction.equals(FactionTypes.ENEMY)) {
            this.setCategory(ENEMY_PROJECTILE.bit());
        } else {
            this.setCategory(ALLY_PROJECTILE.bit());
        }
    }

    /// Reset e desativação do projétil junto da liberação da memória
    @Override
    public void reset() {
        if (reset) return;

        this.setActive(false);
        this.owner = null;
        controllerComponent.reset();

        this.dispose();

        reset = true;
    }

    public void render(SpriteBatch batch) {

    }

    /// Destrói a body e a física do projétil permanentemente dentro do mundo e a limpa da memória
    private void destroyPhysics() {
        if (body != null && world != null && !world.isLocked()) {
            world.destroyBody(body);
            this.body = null;
            this.world = null;
        }
    }

    /// Destrói todos os componentes antes de limpar a lista
    private void clearComponents() {
        components.clear();
    }

    /// Limpa todas as informações do projétil após ser resetado, para ser destruído
    public void destroy() {
        if (!reset) return;
        destroyPhysics();
        clearComponents();
    }

    /// Limpa os dados mais pesados que precisam ser reciclados períodicamente
    public void dispose() {
        if (spriteSheetDatahandler != null) {
            spriteSheetDatahandler.dispose();
        }

        if (controllerComponent != null && controllerComponent.getRayCastHelper() != null) {
            controllerComponent.getRayCastHelper().dispose();
        }
    }

    public void setOwnerPool(ProjectilePool<?> ownerPool) {
        this.ownerPool = ownerPool;
    }

    /// Libera de dentro da pool
    public void release() {
        if (ownerPool != null) {
            ownerPool.free(this);
        }
    }

    public ProjectilePool<?> getOwnerPool() {
        return ownerPool;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public void setLifeTime(float lifeTime) {
        this.controllerComponent.setTimeAliveLimit(lifeTime);
    }

    public ProjectilePhysicsComponent getPhysicsComponent() {
        return physicsComponent;
    }

    public ProjectileControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public float getRadius() {
        return radius;
    }

    public void setActive(boolean value) {
        this.active = value;
        if (body != null) { // se houver uma instancia válida de body permitimos sua alteração
            body.setActive(value);
        }
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFacingForward() {
        return facingForward;
    }

    public void setFacingForward(boolean facingForward) {
        this.facingForward = facingForward;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return (float) Math.toRadians(r);
    }

    public void setAndUpdateRotation(float r) {
        setR(r);
        updateBodyRotation();
    }

    public void updateBodyRotation() {
        this.body.setTransform(this.body.getPosition(), getRotation());
    }

    public void setR(float r) {
        this.r = r;
    }

    public Body getBody() {
        return body;
    }

    public World getWorld() {
        return world;
    }

    public void addComponent(Component comp) {
        components.add(comp);
    }

    public short getCategory() {
        return category;
    }

    public void setCategory(short category) {
        this.category = category;
    }

    public short getMask() {
        return mask;
    }

    public void setMask(short mask) {
        this.mask = mask;
    }
}
