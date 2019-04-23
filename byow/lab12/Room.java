package byow.lab12;

import java.util.HashSet;

public class Room {
    private final int maxHallways = 8;
    int x;
    int y;
    int width;
    int height;
    long id;
    int hallways;
    HashSet<Long> roomsConnected;

    public Room(int x, int y, int w, int h, long id) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.id = id;
        roomsConnected = new HashSet<>();
    }
    public long getId() {
        return id;
    }
    public int getNumHallways() {
        return hallways;
    }
    public boolean connectToRoom(Long roomId) {
        if (roomsConnected.size() < maxHallways) {
            roomsConnected.add(roomId);
            hallways += 1;
            return true;
        }
        return false;
    }
}