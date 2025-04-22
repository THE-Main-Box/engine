package official.sketchBook.room_related.worldGeneration_related.generation;

import official.sketchBook.room_related.worldGeneration_related.connection.RoomNode;
import official.sketchBook.room_related.model.Room;
import official.sketchBook.room_related.model.RoomCell;
import official.sketchBook.room_related.worldGeneration_related.blueprint.WorldLayoutBlueprint;

public class WorldGrid {

    private int width, height;
    private RoomCell[][] grid;
    private WorldLayoutBlueprint sourceBlueprint;


    public WorldGrid(int width, int height) {
        this.width = width;
        this.height = height;

        this.grid = new RoomCell[height][width];

        for (int y = 0; y < height; y++) { // Inicia as células da grid
            for (int x = 0; x < width; x++) {
                grid[y][x] = new RoomCell(x, y);
            }
        }
    }

    public void setRoom(int x, int y, Room room) {
        if (!isInBounds(x, y)) return;

        RoomCell cell = grid[y][x];
        Room existing = cell.getRoom();

        if (existing == null || existing.canBeOverwritten()) {
            // Se já tinha uma sala, limpa as conexões antes de sobrescrever
            if (cell.getNode() != null) {
                cell.getNode().clearConnections();
            }
            cell.setRoom(room);
            cell.setNode(new RoomNode(room));
        }
    }

    public void connectAdjacentRooms() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RoomNode node = getNode(x, y);
                if (node == null) continue;

                // Vizinhos imediatos (N, S, L, O)
                connectNodes(x, y, x + 1, y); // direita
                connectNodes(x, y, x - 1, y); // esquerda
                connectNodes(x, y, x, y + 1); // baixo
                connectNodes(x, y, x, y - 1); // cima
            }
        }
    }

    // Limpa toda a grid (inclusive as conexões)
    public void emptyGridRooms() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RoomCell cell = grid[y][x];
                if (cell.getNode() != null) {
                    cell.getNode().clearConnections(); // Desconecta antes de apagar
                }
                cell.setRoom(null);
                cell.setNode(null);
            }
        }

        RoomNode.resetIdCounter();
    }

    // Limpa apenas as salas que podem ser sobrescritas
    public void clearGridRooms() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RoomCell cell = grid[y][x];
                if (cell.getRoom() != null && cell.getRoom().canBeOverwritten()) {
                    if (cell.getNode() != null) {
                        cell.getNode().clearConnections(); // Desconecta antes de apagar
                    }
                    cell.setRoom(null);
                    cell.setNode(null);
                }
            }
        }
    }

    // serve para conectar dois nodes
    public void connectNodes(int x1, int y1, int x2, int y2) {
        if (!isInBounds(x1, y1) || !isInBounds(x2, y2)) return;

        RoomCell cellA = getCell(x1, y1);
        RoomCell cellB = getCell(x2, y2);

        if (cellA.getNode() == null || cellB.getNode() == null) return;

        RoomNode nodeA = cellA.getNode();
        RoomNode nodeB = cellB.getNode();

        nodeA.connect(nodeB); // Respeita conexão existente automaticamente
    }

    public Room getRoom(int x, int y) {
        if (!isInBounds(x, y)) return null;
        return grid[y][x].getRoom();
    }

    public RoomNode getNode(int x, int y) {
        if (!isInBounds(x, y)) return null;
        return grid[y][x].getNode();
    }

    // Acesso à célula
    public RoomCell getCell(int x, int y) {
        if (isInBounds(x, y)) {
            return grid[y][x];
        }
        return null;
    }

    public void setSourceBlueprint(WorldLayoutBlueprint blueprint) {
        this.sourceBlueprint = blueprint;
    }

    public WorldLayoutBlueprint getSourceBlueprint() {
        return sourceBlueprint;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public RoomCell[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasSourceBlueprint() {
        return sourceBlueprint != null;
    }
}
