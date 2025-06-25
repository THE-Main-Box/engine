package official.sketchBook.util_related.info.values;

import official.sketchBook.util_related.enumerators.types.FixtType;

public class FixtureType {

    public final FixtType type;
    public final Object owner; // referência à entidade/projétil/etc

    public FixtureType(FixtType type, Object owner) {
        this.type = type;
        this.owner = owner;
    }
}
