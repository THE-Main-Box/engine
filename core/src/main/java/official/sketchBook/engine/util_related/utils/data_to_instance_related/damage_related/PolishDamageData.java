package official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related;

import official.sketchBook.engine.components_related.integration_interfaces.dmg.DamageDealerII;
import official.sketchBook.engine.custom_utils_related.CustomPool;
import official.sketchBook.engine.util_related.pools.PolishDamageDataPool;

public class PolishDamageData implements CustomPool.Poolable {
    /// Direção do dano recebido no eixo X
    private int dmgDirX;

    /// Direção do dano recebido no eixo Y
    private int dmgDirY;
    /// Dados brutos a respeito do dano recebido
    private RawDamageData damageData;
    /// Referência ao causador do dano
    private DamageDealerII dealer;

    /// Determina se está pronto para ser limpo ou resetado
    private boolean isReset;

    private final PolishDamageDataPool ownerPool;

    public PolishDamageData(PolishDamageDataPool ownerPool) {
        this.ownerPool = ownerPool;
    }

    public void init(int dmgDirX, int dmgDirY, RawDamageData damageData, DamageDealerII dealer) {
        this.dmgDirX = dmgDirX;
        this.dmgDirY = dmgDirY;
        this.damageData = damageData;
        this.dealer = dealer;

        this.isReset = false;
    }

    public void endUse(){
        ownerPool.free(this);
    }

    @Override
    public void reset() {
        if(isReset) return;

        this.isReset = true;
    }

    @Override
    public void destroy() {
        if(!isReset) return;
        this.damageData = null;
        this.dealer = null;
    }

    @Override
    public boolean isReset() {
        return isReset;
    }

    public int getDmgDirX() {
        return dmgDirX;
    }

    public int getDmgDirY() {
        return dmgDirY;
    }

    public RawDamageData getDamageData() {
        return damageData;
    }

    public DamageDealerII getDealer() {
        return dealer;
    }
}
