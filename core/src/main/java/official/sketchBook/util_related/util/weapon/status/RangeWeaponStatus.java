package official.sketchBook.util_related.util.weapon.status;

public class RangeWeaponStatus {
    public int ammo;
    public int maxAmmo;
    public float fireCoolDown;

    public float fireCoolDownMultiplier;
    public float rechargeSpeedMultiplier;

    public RangeWeaponStatus(
        int maxAmmo,
        float fireCoolDown,
        float rechargeSpeedMultiplier,
        float fireCoolDownMultiplier
    ) {
        this.ammo = maxAmmo;
        this.maxAmmo = maxAmmo;
        this.fireCoolDown = fireCoolDown;

        this.rechargeSpeedMultiplier = rechargeSpeedMultiplier;
        this.fireCoolDownMultiplier = fireCoolDownMultiplier;
    }



    public void refillOnLimit(){
        ammo = maxAmmo;
    }

    public void resetAmmo(){
        ammo = 0;
    }
}
