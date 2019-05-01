package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.Random;

public class Warrior extends Player {
    int health;
    Hero hero;

    public Warrior(Engine e, TETile tile, Coordinate c, Random r, Hero h) {
        super(e, tile, c, r);
        health = 2;
        this.hero = h;
    }

    public void play(char c) {
        double nextMove = rnd.nextDouble();
        if (manhattanDistance(hero, this) < 14) {
            if (nextMove > 0.3) {
                // astar to player and move accordingly
            }
        } else {
            if (nextMove > 0.85) {
                return; // don't move
            }
            moveRandomly();
        }
    }

    private void moveRandomly() {
        ArrayList<Engine.Direction> directions = new ArrayList<>();
        directions.add(Engine.Direction.NORTH);
        directions.add(Engine.Direction.SOUTH);
        directions.add(Engine.Direction.EAST);
        directions.add(Engine.Direction.WEST);
        moveValid(directions);
    }

    private void moveValid(ArrayList<Engine.Direction> directions) {
        int nextMove = rnd.nextInt(directions.size());
        if (!move((directions.get(nextMove)))) {
            directions.remove(nextMove);
            moveValid(directions);
        }
    }
}
