package official.sketchBook.util_related.info.values;

public class RangeWeaponBaseStatus {

    public static class Shotgun {
        public static int maxAmmo;
        public static int ammoCost;
        public static float fireCooldown;
        public static float rechargeSpeedMulti;
        public static float fireCooldownSpeedMulti;

        static {
            maxAmmo = 2;
            ammoCost = -1;
            fireCooldown = 0f;
            rechargeSpeedMulti = 1f;
            fireCooldownSpeedMulti = 1f;
        }
    }

}
