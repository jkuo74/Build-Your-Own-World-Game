package byow.Core;

import java.util.Random;

public class Hero extends Player {
    boolean hasKey;
    int health;

    public Hero(Engine e, int xPos, int yPos, Random r) {
        super(e, xPos,yPos, r);
        hasKey = false;
        health = 4;
    }

    public void takeKey() {
        hasKey = true;
    }

    public boolean hasKey() {
        return hasKey;
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
