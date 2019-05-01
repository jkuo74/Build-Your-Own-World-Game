package byow.Core;

import byow.TileEngine.TETile;

import java.util.Random;

public abstract class Player extends Element{
    int health;
    Engine engine;
    Random rnd;

    public Player(Engine e, TETile tile, Coordinate c, Random r, int h) {
        super(tile, c);
        engine = e;
        rnd = r;
        health = h;
    }

    abstract public void play(char c);

    public void takeHit() {
        health -= 1;
        System.out.println("Health: " + health);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int manhattanDistance(Player p1, Player p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    public void move(Engine.Direction dir) {
        switch(dir) {
            case NORTH:
                engine.move(0, 1, this);
                break;
            case SOUTH:
                engine.move(0, -1, this);
                break;
            case EAST:
                engine.move(1, 0, this);
                break;
            case WEST:
                engine.move(-1, 0, this);
                break;
        }
    }
}
