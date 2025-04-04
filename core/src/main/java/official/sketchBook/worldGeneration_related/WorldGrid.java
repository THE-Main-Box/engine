package official.sketchBook.worldGeneration_related;

import official.sketchBook.worldGeneration_related.connection.RoomNode;
import official.sketchBook.worldGeneration_related.model.Room;
import official.sketchBook.worldGeneration_related.model.RoomCell;

public class WorldGrid {

    private int width, height;
    private RoomCell[][] grid;

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

    public Room getRoom(int x, int y) {
        if (!isInBounds(x, y)) return null;
        return grid[y][x].getRoom();
    }

    public RoomNode getNode(int x, int y) {
        if (!isInBounds(x, y)) return null;
        return grid[y][x].getNode();
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

    // Acesso à célula
    public RoomCell getCell(int x, int y) {
        if (isInBounds(x, y)) {
            return grid[y][x];
        }
        return null;
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
}
