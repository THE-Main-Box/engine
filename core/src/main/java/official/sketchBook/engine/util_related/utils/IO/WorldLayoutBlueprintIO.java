package official.sketchBook.engine.util_related.utils.IO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import official.sketchBook.game.util_related.info.paths.LayoutAssetPath;
import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.WorldLayoutBlueprint;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WorldLayoutBlueprintIO {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save(String name, WorldLayoutBlueprint blueprint) {
        try (FileWriter writer = new FileWriter(LayoutAssetPath.worldLayout_path + name + ".json")) {
            gson.toJson(blueprint, writer);
        } catch (IOException e) {
            e.printStackTrace(); // pode melhorar com log ou exceptions específicas
        }
    }

    public WorldLayoutBlueprint load(String name) {
        try (FileReader reader = new FileReader(LayoutAssetPath.worldLayout_path + name + ".json")) {
            return gson.fromJson(reader, WorldLayoutBlueprint.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
