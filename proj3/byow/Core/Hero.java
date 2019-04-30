package byow.Core;

import byow.TileEngine.TETile;

import java.util.Random;

public class Hero extends Player {
    int numKeys;
    int health;

    public Hero(Engine e, TETile tile, Coordinate c, Random r, int numKeys) {
        super(e, tile, c, r);
        this.numKeys = numKeys;
        health = 4;
    }

    public void takeKey() {
        numKeys--;
    }

    public boolean hasAllKeys() {
        return numKeys == 0;
    }

    public void play(char c) {
        keyboardInput(c);
    }

    public void keyboardInput(char input) {
        switch (input) {
            case 'W':
            case 'w':
                move(Engine.Direction.NORTH);
                break;
            case 'A':
            case 'a':
                move(Engine.Direction.WEST);
                break;
            case 'S':
            case 's':
                move(Engine.Direction.SOUTH);
                break;
            case 'D':
            case 'd':
                move(Engine.Direction.EAST);
                break;
            case 'I':
            case 'i':
                engine.shoot(this, Engine.Direction.NORTH, rnd);
                break;
            case 'K':
            case 'k':
                engine.shoot(this, Engine.Direction.SOUTH, rnd);
                break;
            case 'J':
            case 'j':
                engine.shoot(this, Engine.Direction.WEST, rnd);
                break;
            case 'L':
            case 'l':
                engine.shoot(this, Engine.Direction.EAST, rnd);
                break;
            default:
                break;
        }
    }
}
