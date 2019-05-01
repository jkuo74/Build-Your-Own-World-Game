package byow.Core;

import byow.TileEngine.TETile;

public class Element {
    private Coordinate coord;
    TETile id;

    public Element(TETile tile, Coordinate c) {
        coord = c;
    }

    public int getX() {
        return coord.getX();
    }

    public int getY() {
        return coord.getY();
    }

    public Coordinate getC() {
        return coord;
    }

    public void setCoords(int x, int y) {
        coord.setX(x);
        coord.setY(y);
    }
}
