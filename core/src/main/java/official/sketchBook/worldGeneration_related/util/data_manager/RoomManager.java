package official.sketchBook.worldGeneration_related.util.data_manager;

import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;
import official.sketchBook.util_related.helpers.IO.RoomBlueprintIO;
import official.sketchBook.util_related.info.paths.LayoutAssetPath;
import official.sketchBook.worldGeneration_related.model.Room;
import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;
import official.sketchBook.worldGeneration_related.util.converter.RoomConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private final RoomBlueprintIO blueprintIO;
    private final RoomConverter converter;
    private final String ROOM_FOLDER = LayoutAssetPath.roomBlueprint_path;

    public RoomManager() {
        this.blueprintIO = new RoomBlueprintIO();
        this.converter = new RoomConverter();
    }

    public List<String> listRooms(){
        File folder = new File(ROOM_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        List<String> roomNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                roomNames.add(file.getName().replace(".json", ""));
            }
        }
        return roomNames;
    }

    public Room loadRoom(String name) {
        RoomBlueprint blueprint = loadBlueprint(name);
        return converter.toObject(blueprint);
    }

    public void deleteRoom(String name) {
        File file = new File(ROOM_FOLDER + name + ".json");
        if (file.exists()) file.delete();
    }

    public void saveRoom(Room room) {
        RoomBlueprint blueprint = converter.toBlueprint(room);
        saveBlueprint(blueprint);
    }

    public void saveBlueprint(RoomBlueprint blueprint){
        blueprintIO.save(blueprint);
    }

    public RoomBlueprint loadBlueprint(String name){
        return blueprintIO.load(name);
    }

    //cria a blueprint a partir de uma conversão de enum para int[][]
    public RoomBlueprint newBlueprint(TileType[][] tileTypes, RoomType roomType, String tag){
        return new RoomBlueprint(
            converter.convertToIntegerTileMap(tileTypes),
            roomType,
            tag
        );
    }

    //carrega como deveria ser carregado
    public RoomBlueprint newBlueprint(int[][] tileTypes, RoomType roomType, String tag){
        return new RoomBlueprint(
            tileTypes,
            roomType,
            tag
        );
    }

}
