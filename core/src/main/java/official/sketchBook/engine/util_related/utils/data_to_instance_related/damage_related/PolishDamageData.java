package official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related;

import official.sketchBook.engine.components_related.integration_interfaces.DamageDealerII;

public class PolishDamageData {
    /// Direção do dano recebido no eixo X
    public int dmgDirX;

    /// Direção do dano recebido no eixo Y
    public int dmgDirY;
    /// Dados brutos a respeito do dano recebido
    public final RawDamageData damageData = new RawDamageData();

    public DamageDealerII dealer;

}
