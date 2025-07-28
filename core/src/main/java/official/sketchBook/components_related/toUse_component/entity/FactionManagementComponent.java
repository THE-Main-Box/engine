package official.sketchBook.components_related.toUse_component.entity;

import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.util_related.enumerators.types.FactionTypes;

public class FactionManagementComponent implements Component {

    private final FactionTypes faction;

    public FactionManagementComponent(FactionTypes faction) {
        this.faction = faction;

    }

    @Override
    public void update(float delta) {

    }


    public FactionTypes getFaction() {
        return faction;
    }
}
