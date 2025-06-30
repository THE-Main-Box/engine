package official.sketchBook.util_related.info.values;

public class RangeWeaponBaseStatus {

    public static class Shotgun {
        public static int maxAmmo;
        public static float fireCooldown;
        public static float rechargeSpeedMulti;
        public static float fireCooldownSpeedMulti;

        static {
            maxAmmo = 2;
            fireCooldown = 0.1f;
            rechargeSpeedMulti = 1f;
            fireCooldownSpeedMulti = 1f;
        }
    }

}
