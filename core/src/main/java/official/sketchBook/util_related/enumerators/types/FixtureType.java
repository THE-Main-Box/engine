package official.sketchBook.util_related.enumerators.types;

public class FixtureType {

    public enum Type {
        PROJECTILE,
        ENTITY,
        ENVIRONMENT,
        // etc...
    }

    public final Type type;
    public final Object owner; // referência à entidade/projétil/etc

    public FixtureType(Type type, Object owner) {
        this.type = type;
        this.owner = owner;
    }
}
