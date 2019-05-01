package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.Random;


public class Warrior extends Player {
    Hero hero;

    public Warrior(Engine e, TETile tile, Coordinate c, Random r, Hero h) {
        super(e, tile, c, r, 2);
        this.hero = h;
    }

    @Override
    public void play(char c) {
        double nextMove = rnd.nextDouble();
        if (manhattanDistance(hero, this) < 14) {
            if (nextMove > 0.3) {
                ArrayList<Coordinate> path = engine.performSearch(getCoord(), hero.getCoord());
                if (path.isEmpty()) {
                    System.out.println("Path for finding hero returned empty");
                    return;
                }
                Coordinate nextCoor = path.get(0);
                Engine.Direction nextDir;
                if ((nextCoor.getX() != getX() && nextCoor.getY() != getY())) {
                    System.out.println("Next coordinate invalid after search");
                    return;
                }
                if (nextCoor.getX() > getX()) {
                    nextDir = Engine.Direction.EAST;
                } else if (nextCoor.getX() < getX()) {
                    nextDir = Engine.Direction.WEST;
                } else if (nextCoor.getY() > getY()) {
                    nextDir = Engine.Direction.NORTH;
                } else {
                    nextDir = Engine.Direction.SOUTH;
                }
                System.out.println("YES THIS WORKED");
                move(nextDir);
                //if (!move(nextDir)) {
                //    System.out.println("couldn't move towards player");
                //}
            }
        } else {
            if (nextMove > 0.9) {
                return; // don't move
            }
            moveRandomly();
            // If hero is exactly around warrior, hit hero
            if (manhattanDistance(this, hero) <= 1) {
                hero.takeHit();
            }
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
