package byow.Core;

import byow.TileEngine.TETile;

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
        double nextMove = rnd.nextDouble();
        if (nextMove < 0.25) { // move left
            move(Engine.Direction.WEST);
        } else if (nextMove < 0.5) {
            move(Engine.Direction.EAST); // move right
        } else if (nextMove < 0.75) {
            move(Engine.Direction.NORTH); // move up
        } else {
            move(Engine.Direction.SOUTH); // move down
        }
    }
}
