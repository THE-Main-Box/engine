package official.sketchBook.util_related.util.weapon.status;

public class RangeWeaponStatus {
    public int ammo;
    public int maxAmmo;
    public float fireRate;
    public float rechargeSpeedMultiplier;

    public RangeWeaponStatus(
        int maxAmmo,
        float fireRate,
        float rechargeSpeedMultiplier
    ) {
        this.ammo = maxAmmo;
        this.maxAmmo = maxAmmo;
        this.fireRate = fireRate;
        this.rechargeSpeedMultiplier = rechargeSpeedMultiplier;
    }



    public void refillOnLimit(){
        ammo = maxAmmo;
    }

    public void resetAmmo(){
        ammo = 0;
    }
}
