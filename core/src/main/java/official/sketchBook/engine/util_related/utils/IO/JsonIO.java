package official.sketchBook.engine.util_related.utils.IO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonIO {

    private final Gson gson;

    public JsonIO() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public <T> T load(String path, Class<T> type, boolean local) {
        String json = readString(path, local);
        return gson.fromJson(json, type);
    }

    public void save(String path, Object object, boolean local) {
        String json = gson.toJson(object);
        writeString(path, json, local);
    }

    private String readString(String path, boolean local) {
        if (Gdx.files != null) {
            FileHandle file = local ? Gdx.files.local(path) : Gdx.files.internal(path);
            return file.readString();
        } else {
            try {
                return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao ler arquivo (modo Java): " + path, e);
            }
        }
    }

    private void writeString(String path, String data, boolean local) {
        if (Gdx.files != null) {
            FileHandle file = local ? Gdx.files.local(path) : Gdx.files.internal(path);
            file.writeString(data, false);
        } else {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get(path), data.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Erro ao escrever arquivo (modo Java): " + path, e);
            }
        }
    }

    public Gson getGson() {
        return gson;
    }
}

