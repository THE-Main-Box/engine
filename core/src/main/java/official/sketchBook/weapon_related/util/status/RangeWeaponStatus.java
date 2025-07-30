package official.sketchBook.weapon_related.util.status;

public class RangeWeaponStatus {
    public int ammo;
    public int ammoCost;
    public int maxAmmo;
    public float fireCoolDown;
    public float recoilForce;

    public float fireCoolDownSpeedMultiplier;
    public float rechargeSpeedMultiplier;
    public float recoilForceMultiplier;

    public RangeWeaponStatus(
        int maxAmmo,
        int ammoCost,
        float fireCoolDown,
        float recoilForce,
        float rechargeSpeedMultiplier,
        float fireCoolDownSpeedMultiplier,
        float recoilForceMultiplier
    ) {
        this.ammo = maxAmmo;
        this.ammoCost = ammoCost;
        this.maxAmmo = maxAmmo;
        this.fireCoolDown = fireCoolDown;
        this.recoilForce = recoilForce;

        this.rechargeSpeedMultiplier = rechargeSpeedMultiplier;
        this.fireCoolDownSpeedMultiplier = fireCoolDownSpeedMultiplier;
        this.recoilForceMultiplier = recoilForceMultiplier;
    }



    public void refillOnLimit(){
        ammo = maxAmmo;
    }

    public void resetAmmo(){
        ammo = 0;
    }
}
