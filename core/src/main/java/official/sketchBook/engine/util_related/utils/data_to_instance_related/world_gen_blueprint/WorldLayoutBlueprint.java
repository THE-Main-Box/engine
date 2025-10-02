package official.sketchBook.engine.util_related.utils.data_to_instance_related.world_gen_blueprint;

public class WorldLayoutBlueprint {
    private String[][] tags;
    private int width;
    private int height;

    private String tag;

    public WorldLayoutBlueprint() {
    }

    public WorldLayoutBlueprint(int width, int height) {
        this.width = width;
        this.height = height;
        this.tags = new String[height][width];
    }

    public void setRoomName(int x, int y, String name) {
        if (isInBounds(x, y)) {
            tags[y][x] = name;
        }
    }

    public String getRoomName(int x, int y) {
        return isInBounds(x, y) ? tags[y][x] : null;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String[][] getTags() {
        return tags;
    }

    public void setTags(String[][] tags) {
        this.tags = tags;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
