package official.sketchBook.weapon_related.util.weapon.status;

public class RangeWeaponStatus {
    public int ammo;
    public int maxAmmo;
    public float fireCoolDown;

    public float fireCoolDownSpeedMultiplier;
    public float rechargeSpeedMultiplier;

    public RangeWeaponStatus(
        int maxAmmo,
        float fireCoolDown,
        float rechargeSpeedMultiplier,
        float fireCoolDownSpeedMultiplier
    ) {
        this.ammo = maxAmmo;
        this.maxAmmo = maxAmmo;
        this.fireCoolDown = fireCoolDown;

        this.rechargeSpeedMultiplier = rechargeSpeedMultiplier;
        this.fireCoolDownSpeedMultiplier = fireCoolDownSpeedMultiplier;
    }



    public void refillOnLimit(){
        ammo = maxAmmo;
    }

    public void resetAmmo(){
        ammo = 0;
    }
}
