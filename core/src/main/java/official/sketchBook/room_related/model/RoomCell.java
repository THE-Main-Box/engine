package official.sketchBook.room_related.model;

import official.sketchBook.room_related.worldGeneration_related.connection.RoomNode;

public class RoomCell {
    private int x,y;
    private Room room;
    private RoomNode node;

    public RoomCell(int x, int y) {
        this.x = x;
        this.y = y;
        this.room = null;
        this.node = null;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RoomNode getNode() {
        return node;
    }

    public void setNode(RoomNode node) {
        this.node = node;
    }
}
