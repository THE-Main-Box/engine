package official.sketchBook.engine.room_related.worldGeneration_related.blueprint;

/**
 * Classe record para facilitar a criação de um layout de mundo
 *
 * @param x         coordena x
 * @param y         coordenada y
 * @param blueprint valor para criação de sala
 */
public record LayoutParam(int x, int y, RoomBlueprint blueprint) {
}
