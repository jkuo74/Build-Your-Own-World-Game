package byow.Core;

import java.util.Random;

public abstract class Player {
    int x;
    int y;
    int health = 2;
    Engine engine;
    Random rnd;

    public Player(Engine e, int xPos, int yPos, Random r) {
        engine = e;
        x = xPos;
        y = yPos;
        rnd = r;
    }

    abstract public void play(char c);

    public void takeHit() {
        health --;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int manhattanDistance(Player p1, Player p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
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
