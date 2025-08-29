package official.sketchBook.engine.util_related.utils.general;

import official.sketchBook.engine.util_related.enumerators.type.ObjectType;

/**
 * @param owner referência à entidade/projétil/etc
 */
public record GameObjectTag(ObjectType type, Object owner) {

}
