package byow.Core;

import byow.TileEngine.TETile;

import java.util.Random;

public abstract class Player extends Element {
    int health = 2;
    Engine engine;
    Random rnd;

    public Player(Engine e, TETile tile, Coordinate c, Random r) {
        super(tile, c);
        engine = e;
        rnd = r;
    }

    abstract public void play(char c);

    public void takeHit() {
        health--;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int manhattanDistance(Player p1, Player p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    public boolean move(Engine.Direction dir) {
        switch (dir) {
            case NORTH:
                return engine.move(0, 1, this);
            case SOUTH:
                return engine.move(0, -1, this);
            case EAST:
                return engine.move(1, 0, this);
            case WEST:
                return engine.move(-1, 0, this);
            default:
                return false;
        }
    }
}
