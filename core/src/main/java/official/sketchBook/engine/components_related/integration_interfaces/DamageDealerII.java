package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.RawDamageData;

public interface DamageDealerII {

    DamageDealerOwnerII getOwner();

    Body getBody();

    RawDamageData getDamageData();
}
