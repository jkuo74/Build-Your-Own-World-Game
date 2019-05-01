package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    TETile[][] gameGrid;

    enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    private static final int WIDTH = 70;
    private static final int HEIGHT = 40;
    Player[] players;
    Element Door;
    boolean endGame = false;
    Random rng;
    Portal[] portals;
    HashMap<Integer, Coordinate> floorMap = new HashMap<>();
    HashMap<Integer, Coordinate> wallMap = new HashMap<>();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {

        //TODO MAIN MENU FOR SEED GENERATION...
        /**Random # of adversaries in rooms to walk completely/stochastically at random EACH TURN
         * Add life to hero
         * add a key that opens a door
         * add Heads Up Display **/

        Menu menu = new Menu(this, WIDTH, HEIGHT);
        menu.initialize();
        menu.run();

    }

    public TETile[][] runGame(String input, boolean isKeyboard, boolean isLoad) {
        ter.initialize(WIDTH, HEIGHT);
        gameGrid = new TETile[WIDTH][HEIGHT];


        Object[] commands = generateSeed(input);
        // If seed could not be generated, return
        if (commands[0] == null || commands[1] == null) {
            return gameGrid;
        }

        rng = (Random) commands[0];
        int maxGenFactor = (WIDTH + HEIGHT) / 2;
        int minGenFactor = (WIDTH + HEIGHT) / 10;
        int numRooms = (Math.abs(rng.nextInt()) % (maxGenFactor - minGenFactor)) + minGenFactor;
        Room[] rooms = new Room[numRooms];
        createWorld(rooms);
        generateMaps();
        int numKeys = Math.max(4, rng.nextInt(floorMap.size() / 50));
        Hero hero = placeHero(numKeys);
        Door = placeElement(wallMap, Tileset.LOCKED_DOOR);
        for (int n = 0; n < numKeys; n++) {
            placeElement(floorMap, Tileset.TREE);
        }
        portals = new Portal[(WIDTH + HEIGHT) / Math.min(WIDTH, HEIGHT)];
        for (int n = 0; n < portals.length; n++) {
            portals[n] = placePortal(Tileset.WATER);
        }
        // TODO save and load other players.
        if (isLoad) {
            String rest = (String) commands[1];
            for (int j = 0; j < rest.length(); j++) {
                hero.play(rest.charAt(j));
            }
        } else {
            // TODO If didn't load players, Initialize players and random locations
            // TODO num of players needs to be a soft function of number of floor tiles
            int numOfPlayers = 5;
            players = new Player[numOfPlayers];
            for (int i = 0; i < numOfPlayers; i++) {
                players[i] = placeWarrior(hero);
            }
        }

        ter.renderFrame(gameGrid);

        // Only if keyboard is allowed
        if (isKeyboard) {

            String userInput = "";
            while (!endGame) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = StdDraw.nextKeyTyped();
                    if (quitSequence(c, userInput)) {
                        userInput += c;
                        quitAndSave(input + userInput);
                    }
                    userInput += c;
                    hero.play(c);
                    for (Player w : players) {
                        w.play(c);
                    }
                    ter.renderFrame(gameGrid);
                }

            }
        }
        return gameGrid;
    }

    /**
     * Given all previous inputs and the latest key inserted, check
     * if quit sequence was pressed
     *
     * @param c: very last char inputted by user
     * @param s: sequence of previous inputs by user.
     * @return
     */

    private boolean quitSequence(char c, String s) {
        if (s.length() > 0 && s.charAt(s.length() - 1) == ':'
                && (c == 'q' || c == 'Q')) {
            return true;
        }
        return false;
    }

    /**
     * Function returns an array with Random object and "rest" string.
     * Used to initialize a word with an appropriate seed.
     *
     * @param input
     * @return
     */
    public Object[] generateSeed(String input) {
        Object[] result = new Object[2];
        char prefix = input.charAt(0);
        String seedString = "";
        int i = 1;
        while (Character.isDigit(input.charAt(i))) {
            seedString += input.charAt(i);
            i += 1;
        }
        char endSeedChar = input.charAt(i);
        result[1] = input.substring(i + 1); // "rest string - commands to execute

        // Check prefix, suffix, and that input contains only digits.
        if ((prefix != 'N' && prefix != 'n') || (endSeedChar != 'S'
                && endSeedChar != 's') || !seedString.matches("\\d+")) {
            return result;
        }
        long seed = Long.parseLong(seedString);
        System.out.println("Seed is: " + seed);
        result[0] = new Random(seed);

        return result;
    }

    /**
     * Load saved game by loading the string of inputs previously
     * entered by user and creating a new game with these inputs.
     */
    // @Source: Editor class
    public void loadSavedGame() {
        String savedInput = "";
        File f = new File("./save.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                savedInput = (String) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
            // run startNewGame running the "rest" commands
            runGame(savedInput, true, true);
        }
    }

    /**
     * Save file by writing the string of input sequence to a file.
     *
     * @param s: string of all user input including seed
     */
    // @Source: Editor class
    private void quitAndSave(String s) {
        File f = new File("./save.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(s);
            System.out.println("file saved successfully, thanks for playing!");
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
        System.exit(0);
    }

    /**
     * Find a valid coordinate to place the hero, initialize at this coordinate
     * and return
     *
     * @return a new Hero
     */
    private Hero placeHero(int numKeys) {
        int place = rng.nextInt(floorMap.size());
        Coordinate coord = floorMap.get(place);
        gameGrid[coord.getX()][coord.getY()] = Tileset.INDIANA;
        return new Hero(this, Tileset.INDIANA, coord.copy(), rng, numKeys);
    }

    /**
     * Finds a valid coordinate to place element on given set of tiles
     *
     * @param tiles Valid tiles to initialize elements on
     * @param tile
     * @return
     */
    private Element placeElement(HashMap<Integer, Coordinate> tiles, TETile tile) {
        int place = rng.nextInt(tiles.size());
        Coordinate coord = tiles.get(place);
        gameGrid[coord.getX()][coord.getY()] = tile;
        return new Element(tile, coord.copy());
    }

    /**
     * Find two valid walls to place portals
     *
     * @param tile
     * @return
     */
    private Portal placePortal(TETile tile) {
        int place1 = rng.nextInt(wallMap.size());
        int place2 = rng.nextInt(wallMap.size());

        Coordinate coord1 = wallMap.get(place1);
        Coordinate coord2 = wallMap.get(place2);
        gameGrid[coord1.getX()][coord1.getY()] = tile;
        gameGrid[coord2.getX()][coord2.getY()] = tile;
        return new Portal(tile, coord1.copy(), coord2.copy());
    }

    /**
     * Find a valid coordinate to place a warrior, initialize at this coordinate
     * and return
     *
     * @return a new Hero
     */
    private Warrior placeWarrior(Hero h) {
        int place = rng.nextInt(floorMap.size());
        Coordinate coord = floorMap.get(place);
        gameGrid[coord.getX()][coord.getY()] = Tileset.WARRIOR;

        return new Warrior(this, Tileset.WARRIOR, coord.copy(), rng, h);
    }

    /**
     * Hero can shoot enemies in a range of 3 steps.  Hits with .66 chance.
     * Effectively, function will look for a range of 3 tiles in the
     * direction specified, and if a warrior is spotted, it will hit them.
     *
     * @param p - hero
     * @return If some creature was shot or not
     */
    // TODO: Remove print statements.
    public boolean shoot(Player p, Direction dir, Random rng) {
        int range = 3;
        double probability = .67;
        // shot misses with .33 probability
        if (rng.nextDouble() > probability) {
            System.out.println("Shooting falied stochastically");
            return false;
        }

        boolean hit = false;
        switch (dir) {
            case NORTH:
                for (int i = 1; i < range + 1; i++) {
                    if (p.getY() + i > HEIGHT - 1) {
                        System.out.println("Shooting exceeded limits upwards");
                        break;
                    }
                    if (gameGrid[p.getX()][p.getY() + i] == Tileset.WARRIOR) {
                        hitWarrior(p.getX(), p.getY() + i);
                        System.out.println("Warrior hit up!");
                        hit = true;
                    }
                }
                break;
            case SOUTH:
                for (int i = 1; i < range + 1; i++) {
                    if (p.getY() - i < 0) {
                        System.out.println("Shooting exceeded limits downwards");
                        break;
                    }
                    if (gameGrid[p.getX()][p.getY() - i] == Tileset.WARRIOR) {
                        hitWarrior(p.getX(), p.getY() - i);
                        System.out.println("Warrior hit down!");
                        hit = true;
                    }
                }
                break;
            case EAST:
                for (int i = 1; i < range + 1; i++) {
                    if (p.getX() + i > WIDTH - 1) {
                        System.out.println("Shooting exceeded limits rightwards");
                        break;
                    }
                    if (gameGrid[p.getX() + i][p.getY()] == Tileset.WARRIOR) {
                        hitWarrior(p.getX() + i, p.getY());
                        System.out.println("Warrior hit right!");

                        hit = true;
                    }
                }
                break;
            case WEST:
                for (int i = 1; i < range + 1; i++) {
                    if (p.getX() + i < 0) {
                        System.out.println("Shooting exceeded limits leftwards");
                        break;
                    }
                    if (gameGrid[p.getX() - i][p.getY()] == Tileset.WARRIOR) {
                        hitWarrior(p.getX() - i, p.getY());
                        System.out.println("Warrior hit left!");
                        hit = true;
                    }
                }
                break;
        }
        return hit;
    }

    // Hit a warrior at x, y.
    private void hitWarrior(int x, int y) {

    }
    /**
     * Move character given "WASD" movement directions
     *
     * @param grid  World to move on
     * @param input Given input character
     */

    /**
     * Moves character to another adjacent tile given move direction and successfully moves if
     * coordinate is a floor. Update character's position if move is successful. Assumes coordinate
     * moving to is always in bounds of the grid
     *
     * @param moveX Change in coordinate in X direction
     * @param moveY Change in coordinate in Y direction
     */
    public boolean move(int moveX, int moveY, Player p) {
        int pX = p.getX();
        int pY = p.getY();
        Coordinate coord = new Coordinate(pX + moveX, pY + moveY);
        if (p.getID() == Tileset.INDIANA) {
            Hero indy = (Hero) p;
            if (!indy.hasAllKeys()) {
                pickUpKey(pX + moveX, pY + moveY, indy);
                if (indy.hasAllKeys()) {
                    Door.id = Tileset.UNLOCKED_DOOR;
                    gameGrid[Door.getX()][Door.getY()] = Door.id;
                }
            } else {
                if (checkBoundary(new Room(Tileset.UNLOCKED_DOOR, pX, pY, 1, 1),
                        Tileset.UNLOCKED_DOOR)) {
                    endGame = true;
                }
            }
        }
        for (int n = 0; n < portals.length; n++) {
            if (portals[n].equals(coord)) {
                return portals[n].transport(coord, p);
            }
        }
        if (gameGrid[pX + moveX][pY + moveY] == Tileset.FLOOR) {
            gameGrid[pX + moveX][pY + moveY] = p.getID();
            gameGrid[pX][pY] = Tileset.FLOOR;
            p.setCoords(pX + moveX, pY + moveY);
            return true;
        }
        return false;
    }

    /**
     * If the coordinate to be moved to is a key then have the hero pick it up
     *
     * @param x
     * @param y
     * @param hero
     */
    public void pickUpKey(int x, int y, Hero hero) {
        if (gameGrid[x][y] == Tileset.TREE) {
            hero.takeKey();
            gameGrid[x][y] = Tileset.FLOOR;
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        return runGame(input, false, true);
    }

    /**
     * 1) Initialize all tiles to NOTHING
     * 2) Add the number of Rooms based off passed in Room array size
     * 3) Room size height and width are limited to the sum of the
     * HEIGHT and WIDTH over 16 CAN CHANGE
     * 4) Connect initialized room wiht previously initialized room if it is not already connected
     *
     * @param rooms Room to create.
     */
    public void createWorld(Room[] rooms) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                gameGrid[x][y] = Tileset.NOTHING;
            }
        }

        int limit = (WIDTH + HEIGHT) / 16;
        TETile roomTile = Tileset.FLOOR;
        TETile hallwayTile = Tileset.FLOOR;
        for (int n = 0; n < rooms.length; n++) {
            int width = Math.abs(rng.nextInt()) % limit + 2;
            int height = Math.abs(rng.nextInt()) % limit + 2;
            rooms[n] = addRoom(width, height, roomTile);
            if (n != 0 && !checkBoundary(rooms[n], roomTile)
                    && !checkBoundary(rooms[n], hallwayTile)) {
                connectRooms(rooms[n - 1], rooms[n], hallwayTile);
            }
        }
    }

    /**
     * Checks borders if any tile equals the tile parameter
     *
     * @param room Room to check border on
     * @param tile Tile to look for around the room
     * @return Returns true if tile found, false if not
     */
    public boolean checkBoundary(Room room, TETile tile) {
        int roomX = room.getX();
        int roomY = room.getY();
        int roomHeight = room.getHeight();
        int roomWidth = room.getWidth();

        int lowerBoundX = Math.max(0, roomX - 1);
        int upperBoundX = Math.min(roomX + roomWidth, WIDTH - 1);
        int lowerBoundY = Math.max(0, roomY - 1);
        int upperBoundY = Math.min(roomY + roomHeight, HEIGHT - 1);
        for (int col = roomX; col < upperBoundX; col++) {
            if (gameGrid[col][lowerBoundY] == tile || gameGrid[col][upperBoundY] == tile) {
                return true;
            }
        }
        for (int row = roomY; row < upperBoundY; row++) {
            if (gameGrid[lowerBoundX][row] == tile || gameGrid[upperBoundX][row] == tile) {
                return true;
            }
        }
        return false;
    }

    /**
     * 1) Set all points within area to be a FLOOR
     * 2) Bound area with WALL if tile is NOTHING
     *
     * @param lLX    Lower Left X-coordinate
     * @param lLY    Lower Left Y-coordinate
     * @param width  Width of the are to be created
     * @param height Height of the area to be created
     */
    public void addArea(int lLX, int lLY,
                        int width, int height, TETile tile) {
        for (int row = Math.max(1, lLY); row < lLY + height && row < HEIGHT - 1; row++) {
            for (int col = Math.max(1, lLX); col < lLX + width && col < WIDTH - 1; col++) {
                gameGrid[col][row] = tile;
            }
        }

        int lowerBoundX = Math.max(0, lLX - 1);
        int upperBoundX = Math.min(lLX + width, WIDTH - 1);
        int lowerBoundY = Math.max(0, lLY - 1);
        int upperBoundY = Math.min(lLY + height, HEIGHT - 1);

        for (int col = lowerBoundX; col < upperBoundX + 1; col++) {
            if (gameGrid[col][lowerBoundY] == Tileset.NOTHING) {
                gameGrid[col][lowerBoundY] = Tileset.WALL;
            }
            if (gameGrid[col][upperBoundY] == Tileset.NOTHING) {
                gameGrid[col][upperBoundY] = Tileset.WALL;
            }
        }

        for (int row = lowerBoundY; row < upperBoundY + 1; row++) {
            if (gameGrid[lowerBoundX][row] == Tileset.NOTHING) {
                gameGrid[lowerBoundX][row] = Tileset.WALL;
            }
            if (gameGrid[upperBoundX][row] == Tileset.NOTHING) {
                gameGrid[upperBoundX][row] = Tileset.WALL;
            }
        }
    }

    /**
     * 1) Get two random numbers for lower left corner point to create area on
     * //TODO ENSURE ROOM AREA LIES COMPLETELY WITHIN GRID
     * 2) Set all points within area to be a FLOOR
     * 3) Bound area with WALL if tile is NOTHING
     *
     * @param width  Width of the are to be created
     * @param height Height of the area to be created
     * @return Returns initialized Room object
     */
    public Room addRoom(int width, int height, TETile tile) {
        int lLX = Math.max(1, Math.abs(rng.nextInt()) % (WIDTH - width - 1));
        int lLY = Math.max(1, Math.abs(rng.nextInt()) % (HEIGHT - height - 1));
        addArea(lLX, lLY, width, height, tile);
        return new Room(tile, lLX, lLY, width, height);
    }

    /**
     * Connects the given rooms by creating hallways
     * Creates hallways based off orientation given by RNG
     * if orientation is true, create hallways in a mirrored L shape or upside-down mirrored L shape
     * else create hallways in a create hallways in a L shape or upside-down L shape
     *
     * @param room1
     * @param room2
     */
    public void connectRooms(Room room1, Room room2, TETile tile) {
        Room leftMost = (room1.getX() < room2.getX()) ? room1 : room2;
        Room rightMost = (leftMost == room1) ? room2 : room1;

        boolean orientation = rng.nextBoolean();
        if (orientation) {
            addHallway(leftMost, rightMost, tile);
        } else {
            addHallway(rightMost, leftMost, tile);
        }
    }

    /**
     * Add hallways connecting the two rooms
     * Length limits are set to the horizontal/vertical
     * distance respectively with added lengths based on room sizes
     *
     * @param room1
     * @param room2
     */
    public void addHallway(Room room1, Room room2, TETile tile) {
        int widthGive = Math.max(room1.getWidth(), room2.getWidth());
        int heightGive = Math.max(room1.getHeight(), room2.getHeight());
        int widthLimit = Math.abs(room1.getX() - room2.getX()) + widthGive;
        int heightLimit = Math.abs(room1.getY() - room2.getY()) + heightGive;

        int Y = (Math.abs(rng.nextInt()) % room1.getHeight()) + room1.getY();
        int X = (Math.abs(rng.nextInt()) % room2.getWidth()) + room2.getX();

        if (room1.getX() > room2.getX()) {
            addArea(Math.max(1, room1.getX() - widthLimit), Y, widthLimit, 1, tile);
        } else {
            addArea(room1.getX() + room1.getWidth(), Y, widthLimit, 1, tile);
        }
        if (Y < room2.getY()) {
            addArea(X, Math.max(1, room2.getY() - heightLimit), 1, heightLimit, tile);
        } else {
            addArea(X, Math.max(1, Y - heightLimit), 1, heightLimit, tile);
        }
    }

    /**
     * Generates floorMap and wallMap. All points in the grid that are floors are added in the
     * floorMap. All points in the grid that are walls and is adjacent to a floor is added to the
     * wallMap.
     */
    public void generateMaps() {
        for (int col = 0; col < gameGrid.length; col++) {
            for (int row = 0; row < gameGrid[0].length; row++) {
                if (gameGrid[col][row] == Tileset.FLOOR) {
                    floorMap.put(floorMap.size(), new Coordinate(col, row));
                } else if (gameGrid[col][row] == Tileset.WALL &&
                        checkBoundary(new Room(Tileset.FLOOR, col, row, 1, 1), Tileset.FLOOR) &&
                        checkBoundary(new Room(Tileset.NOTHING, col, row, 1, 1), Tileset.NOTHING)) {
                    wallMap.put(wallMap.size(), new Coordinate(col, row));
                }
            }
        }
    }
}
