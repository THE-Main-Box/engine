package official.sketchBook.util_related.info.values;

import official.sketchBook.util_related.enumerators.types.ObjectType;

/**
 * @param owner referência à entidade/projétil/etc
 */
public record FixtureType(ObjectType type, Object owner) {

}
