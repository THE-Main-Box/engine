package official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related;

public class RawDamageData {
    /// Quantidade de dano a ser aplicado
    private final double amount;
    /// Modificador de dano
    private final float amountMod;
    /// Tempo de invencibilidade para o temporizador do receptor
    private final float invincibilityTime;
    /// Velocidade com a qual o objeto será jogado ao haver knockBack
    private final float knockBack;
    /// Se devemos aplicar o knockBack
    private final boolean applyKnockBack;

    public RawDamageData(double amount, float amountMod, float invincibilityTime, float knockBack, boolean applyKnockBack) {
        this.amount = amount;
        this.amountMod = amountMod;
        this.invincibilityTime = invincibilityTime;
        this.knockBack = knockBack;
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
}
