package official.sketchBook.worldGeneration_related.connection;

import official.sketchBook.worldGeneration_related.model.Room;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoomNode {

    //index da posição da array bidimensional dentro da sala
    private Room room;
    private final List<RoomConnection> connections;

    public RoomNode(Room room) {
        this.room = room;
        this.connections = new ArrayList<>();
    }

    //remove a instância da conexão no RoomNode
    public void disconnect(RoomNode other) {
        removeConnectionTo(other);
        other.removeConnectionTo(this); // remove do outro também
    }

    //itera sobre as conexões e as remove de forma segura e simples
    private void removeConnectionTo(RoomNode target) {
        Iterator<RoomConnection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            RoomConnection conn = iterator.next();
            if (conn.getTarget() == target) {
                iterator.remove();
                break;
            }
        }
    }

    //limpa todas as conexões
    public void clearConnections() {
        for (RoomConnection conn : new ArrayList<>(connections)) {
            RoomNode target = conn.getTarget();
            target.removeConnectionTo(this);
        }
        connections.clear();
    }

    //pega o nó da outra sala e adiciona ela na nossa lista e adicionamos essa lista na outra
    public void connect(RoomNode other) {
        if (isAlreadyConnected(other)) return;

        this.connections.add(new RoomConnection(other));
        other.connections.add(new RoomConnection(this));

    }

    //valida se já existe uma conexão com o outro nó
    private boolean isAlreadyConnected(RoomNode other) {
        return connections.stream()
            .anyMatch(conn -> conn.getTarget() == other);
    }

    public List<RoomConnection> getConnections() {
        return connections;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
