package official.sketchBook.util_related.info.values.constants;

public class SpeedRelatedConstants {

    /**
     * DECELERATION - a desaceleração é em "%", então não precisa ser muito alto que não tem sentido
     * <p>
     * MAX_SPEED - a velocidade máxima utiliza um padrão de "PPM", ou seja são unidades por centenas,
     * deixar como sendo maior de "100" é uma abordagem interessante para objetos que podem se mover rápidamente
     * <p>
     * ACCELERATION - a aceleração também usa um padrão de "PPM", unidades por centenas,
     * mas é interessante ter menos de uma centena para ser interessante ou seja menos de "1"
     */

    public static class Player {
        //Velocidade máxima calculada por centena por ponto inteiro
        public static final float HORIZONTAL_WALK_MAX = 110f;
        public static final float HORIZONTAL_AIR_MAX = 140f;

        //Valores de desaceleração por segundo por multiplicação
        public static final float HORIZONTAL_WALK_DEC = 10f;
        public static final float HORIZONTAL_AIR_DEC = 5f;

        //Valor de aceleração de 70 centímetros por segundo
        public static final float GROUND_ACCEL = 0.5f;
        public static final float AIR_ACCEL = 0.1f;
    }

    public static class Mobs {
        public static final float VERTICAL_MAX = 1_000f;
        public static final float HORIZONTAL_MAX = 1_000f;
    }

}
