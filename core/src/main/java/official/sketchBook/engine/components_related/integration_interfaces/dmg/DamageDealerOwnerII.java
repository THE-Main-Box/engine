package official.sketchBook.engine.components_related.integration_interfaces.dmg;

import official.sketchBook.engine.components_related.integration_interfaces.physic.RayCasterII;

/**
 * Interface que referencia aquele que irá receber o crédito pelo dano
 * Estende RayCasterII, pois cada dono precisa ser capaz de lidar com rayCasts
 * */
public interface DamageDealerOwnerII extends RayCasterII {

}
