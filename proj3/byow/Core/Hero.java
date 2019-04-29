package byow.Core;

public class Hero extends Player {
    boolean hasKey;
    int health;

    public Hero(Engine e, int xPos, int yPos) {
        super(e, xPos,yPos);
        hasKey = false;
        health = 2;
    }

    public void takeKey() {
        hasKey = true;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void play(char c) {
        keyboardInput(c, this);
    }
}
