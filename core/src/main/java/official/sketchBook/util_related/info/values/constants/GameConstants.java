package official.sketchBook.util_related.info.values.constants;

public class GameConstants {

    //TODO: implementar sistema de carregamento de configurações dinâmicas por json nos métodos setter dos valores

    /// Valores de controle de renderização e física
    public static class Physics {
        /// Iterações de velocidade para o box2d
        public static int VELOCITY_ITERATIONS = 8;
        /// Iterações de posição para o box2d
        public static int POSITION_ITERATIONS = 3;
        /// Taxa de fps
        public static int FPS_TARGET;
        /// Taxa de ups
        public static int UPS_TARGET;
        /// Constante de deltaTime
        public static float FIXED_TIMESTAMP;

        /// Constante de Pixels Per Meter
        public final static float PPM = 100;

        static{
            FPS_TARGET = 60;
            UPS_TARGET = 60;
            FIXED_TIMESTAMP = 1f / UPS_TARGET;
        }
    }

    /// Constantes de valores para visualização de elementos na tela
    public static class Screen{
        /// Número de tiles na largura que devem ser renderizados
        public static final int TILES_IN_WIDTH = 39;
        /// Número de tiles na altura que devem ser renderizados
        public static final int TILES_IN_HEIGHT = 21;
        /// Dimensão padrão das tiles
        public static final int TILES_DEFAULT_SIZE = 16;
        /// Escala de renderização de coisas na tela
        public static float scale;
        /// Zoom levando em conta a escala
        public static float zoom;

        static {
            updateScale();
            updateZoom();
        }

        ///Como o zoom é o único valor que será alterado de fato, junto da escala, podemos fazer isso daqui
        public static void updateZoom(){
            zoom = 0.7f / scale;
        }

        public static void updateScale(){
            scale = 2f;

            updateZoom();//Atualiza o zoom logo após atualizar a escala
        }

    }

    /// Classe para armazenar os estados de depuração
    public static class Debug {
        /// Permite a chamada do renderizador de debbug do bx2d a mostrar as hitboxes
        public static boolean showHitBox = true;
        /// Permite que o meu sistema próprio de gestão de rayCasts renderize os raios lançados de cada entidade
        public static boolean showRayCast = true;
        /// Permite que a ui mostre os números de todos os projéteis ativos e inativos
        public static boolean showProjectilesActive = true;
        /// Permite que a ui mostre a quantidade de pools de projéteis ativas no momento
        public static boolean showActiveProjectilePools = true;
    }

    /// Valores de comportamento de som
    public static class Sound{
        public static boolean soundEffectsMute = false;
        public static boolean soundMute = true;
        public static int soundEffectsVolume = 50;
        public static int soundVolume = 50;
    }

}
