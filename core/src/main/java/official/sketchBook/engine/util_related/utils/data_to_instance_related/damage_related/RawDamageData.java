package official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related;

public class RawDamageData {
    /// Quantidade de dano a ser aplicado
    private double amount;
    /// Modificador de dano
    private float amountMod;
    /// Tempo de invencibilidade para o temporizador do receptor
    private float invincibilityTime;
    /// Velocidade com a qual o objeto será jogado ao haver knockBack
    private float knockBack;
    /// Multiplicador de knockBack
    private float knockBackMulti;
    /// Se devemos aplicar o knockBack
    private boolean applyKnockBack;

    public RawDamageData(
        double amount,
        float amountMod,
        float invincibilityTime,
        float knockBack,
        float knockBackMulti,
        boolean applyKnockBack
    ) {
        this.amount = amount;
        this.amountMod = amountMod;
        this.invincibilityTime = invincibilityTime;
        this.knockBack = knockBack;
        this.applyKnockBack = applyKnockBack;
        this.knockBackMulti = knockBackMulti;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAmountMod(float amountMod) {
        this.amountMod = amountMod;
    }

    public void setInvincibilityTime(float invincibilityTime) {
        this.invincibilityTime = invincibilityTime;
    }

    public void setKnockBack(float knockBack) {
        this.knockBack = knockBack;
    }

    public void setApplyKnockBack(boolean applyKnockBack) {
        this.applyKnockBack = applyKnockBack;
    }

    public double getAmount() {
        return amount;
    }

    public float getAmountMod() {
        return amountMod;
    }

    public float getInvincibilityTime() {
        return invincibilityTime;
    }

    public float getKnockBack() {
        return knockBack;
    }

    public boolean isApplyKnockBack() {
        return applyKnockBack;
    }

    public float getKnockBackMulti() {
        return knockBackMulti;
    }

    public void setKnockBackMulti(float knockBackMulti) {
        this.knockBackMulti = knockBackMulti;
    }
}
