package official.sketchBook.util_related.info.values.constants;

public class RangeWeaponsStatusConstants {

    public static class Shotgun {
        public static int maxAmmo;
        public static int ammoCost;
        public static float fireCooldown;
        public static float fireRecoilSpeed;
        public static float rechargeSpeedMulti;
        public static float fireCooldownSpeedMulti;
        public static float fireRecoilForceMulti;

        static {
            maxAmmo = 2;
            ammoCost = 1;
            fireCooldown = 0.1f;
            rechargeSpeedMulti = 1f;
            fireCooldownSpeedMulti = 1f;
            fireRecoilForceMulti = 1f;
            fireRecoilSpeed = 2f;
        }
    }

}
