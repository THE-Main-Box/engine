package official.sketchBook.engine.util_related.utils.registers;

import com.badlogic.gdx.utils.Array;
import official.sketchBook.engine.components_related.toUse_component.projectile.ProjectileControllerComponent;

/// Registro de colisões com projéteis
public class ProjectileCollisionRegister {
    /**
     * Armazenamos o controlador de cada projétil, pois será o mesmo para cada tipo de projétil existente
     * além de servir como acesso direto a um projétil,
     * sem precisar de um cast explicito para o tipo na hora do acesso
     */
    private static final Array<ProjectileControllerComponent> collisionsToProcess;
    private static final Array<ProjectileControllerComponent> exitToMonitor;

    static {
        collisionsToProcess = new Array<>();
        exitToMonitor = new Array<>();
    }

    /// Registra um projétil que acabou de colidir
    public static void registerCollision(ProjectileControllerComponent controller) {
        if (!collisionsToProcess.contains(controller, true)) {
            collisionsToProcess.add(controller);
        }
    }

    /// Registra projéteis que devem ter a saída de colisão monitorada
    public static void registerCollisionExitCheck(ProjectileControllerComponent controller) {
        // Remove da lista de entrada se ainda estiver lá (evita dupla iteração)
        collisionsToProcess.removeValue(controller, true);

        if (!exitToMonitor.contains(controller, true)) {
            exitToMonitor.add(controller);
        }
    }

    /// Processa as colisões detectadas
    public static void processCollisions() {

        //Percorremos a lista de cima pra baixo para evitar uma realocação geral
        for (int i = collisionsToProcess.size - 1; i >= 0; i--) {
            //Pegamos o projétil que queremos
            ProjectileControllerComponent c = collisionsToProcess.get(i);

            //Lidamos com a colisão
            c.handleBufferedCollision();

            //Se tiver marcado para lidar com a colisão de forma constante,
            // mantemos dentro da lista para ser iterado futuramente
            if (!c.isContinuousCollisionDetection()) {
                collisionsToProcess.removeIndex(i);
            }
        }


    }

    /// Processa a saída de colisões
    private static void processCollisionExits() {
        for (int i = exitToMonitor.size - 1; i >= 0; i--) {
            ProjectileControllerComponent c = exitToMonitor.get(i);
            c.handleBufferedEndCollision();
            exitToMonitor.removeIndex(i);
        }

    }


    /// Chama os métodos para atualizar as colisões
    public static void update() {
        processCollisions();
        processCollisionExits();
    }

}
