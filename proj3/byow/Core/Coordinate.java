package byow.Core;

public class Coordinate {
    private int X;
    private int Y;

    public Coordinate(int x, int y) {
        X = x;
        Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }
    public Coordinate copy() {
        return new Coordinate(X, Y);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Coordinate)) {
            return false;
        }

        Coordinate c = (Coordinate) o;
        return X == c.getX()
                && Y == c.getY();
    }
}
