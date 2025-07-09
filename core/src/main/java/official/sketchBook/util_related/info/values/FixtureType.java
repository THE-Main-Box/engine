package official.sketchBook.util_related.info.values;

import official.sketchBook.util_related.enumerators.types.ObjectType;

public class FixtureType {

    public final ObjectType type;
    public final Object owner; // referência à entidade/projétil/etc

    public FixtureType(ObjectType type, Object owner) {
        this.type = type;
        this.owner = owner;
    }
}
