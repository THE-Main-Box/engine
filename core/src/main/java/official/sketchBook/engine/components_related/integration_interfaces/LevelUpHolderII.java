package official.sketchBook.engine.components_related.integration_interfaces;

import official.sketchBook.engine.components_related.toUse_component.object.LevelingComponent;

public interface LevelUpHolderII {

    void onLevelUp(LevelingComponent lvlC);

    void onLevelDown(LevelingComponent lvlC);

    LevelingComponent getLvlC();

}
