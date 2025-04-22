package official.sketchBook.room_related.worldGeneration_related.connection;

public class RoomConnection {
    private final RoomNode target;

    public RoomConnection(RoomNode target) {
        this.target = target;
    }

    public RoomNode getTarget() {
        return target;
    }
}
