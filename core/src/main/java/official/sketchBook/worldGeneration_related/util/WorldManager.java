package official.sketchBook.worldGeneration_related.util;


import official.sketchBook.util_related.helpers.IO.RoomBlueprintIO;
import official.sketchBook.util_related.helpers.IO.WorldLayoutBlueprintIO;
import official.sketchBook.util_related.info.paths.LayoutAssetPath;
import official.sketchBook.worldGeneration_related.generation.WorldLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    private final WorldLayoutBlueprintIO layoutIO = new WorldLayoutBlueprintIO();
    private final RoomBlueprintIO roomIO = new RoomBlueprintIO();
    private final WorldLayoutConverter converter = new WorldLayoutConverter(roomIO);
    private final String WORLD_FOLDER = LayoutAssetPath.worldLayout_path;

    public List<String> listWorlds() {
        File folder = new File(WORLD_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        List<String> worldNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                worldNames.add(file.getName().replace(".json", ""));
            }
        }
        return worldNames;
    }

    public WorldLayout loadWorld(String name) {
        var blueprint = layoutIO.load(name);
        return converter.convert(blueprint);
    }

    public void saveWorld(String name, WorldLayout layout) {
        var blueprint = converter.toBlueprint(layout);
        layoutIO.save(name, blueprint);
    }

    public void deleteWorld(String name) {
        File file = new File(WORLD_FOLDER + name + ".json");
        if (file.exists()) file.delete();
    }
}

