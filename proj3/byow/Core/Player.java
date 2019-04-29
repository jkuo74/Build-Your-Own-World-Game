package byow.Core;

import byow.TileEngine.TETile;

public abstract class Player {
    int x;
    int y;
    int health = 2;
    Engine engine;
    abstract public void play(char c);

    public Player(Engine e, int xPos, int yPos) {
        engine = e;
        x = xPos;
        y = yPos;
    }

    public void keyboardInput(char input, Player p) {
        switch (input) {
            case 'W':
            case 'w':
                engine.move(engine.gameGrid, 0, 1, p);
                break;
            case 'A':
            case 'a':
                engine.move(engine.gameGrid, -1, 0, p);
                break;
            case 'S':
            case 's':
                engine.move(engine.gameGrid, 0, -1, p);
                break;
            case 'D':
            case 'd':
                engine.move(engine.gameGrid, 1, 0, p);
                break;
            default:
                break;
        }
    }

}
