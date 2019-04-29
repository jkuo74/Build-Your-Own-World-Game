package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;

    // private static final long SEED = 287313;
    // private static final Random RANDOM = new Random(SEED);

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    //TODO MORE DETAILED
    private class Hero {
        int x;
        int y;

        public Hero(int xPos, int yPos) {
            x = xPos;
            y = yPos;
        }
    }

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
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];


        Object[] commands = generateSeed(input);
        // If seed could not be generated, return
        if (commands[0] == null|| commands[1] == null) {
            return finalWorldFrame;
        }

        Random rnd = (Random) commands[0];
        int maxGenFactor = (WIDTH + HEIGHT) / 2;
        int minGenFactor = (WIDTH + HEIGHT) / 10;
        int numRooms = (Math.abs(rnd.nextInt()) % (maxGenFactor - minGenFactor)) + minGenFactor;
        Room[] rooms = new Room[numRooms];
        createWorld(finalWorldFrame, rooms, rnd);
        Hero hero = placeHero(finalWorldFrame, rnd);

        if (isLoad) {
            String rest = (String) commands[1];
            for (int j = 0; j < rest.length(); j++) {
                keyboardInput(finalWorldFrame, rest.charAt(j), hero);
            }
        }

        ter.renderFrame(finalWorldFrame);

        // Only if keyboard is allowed
        if (isKeyboard) {
            boolean endReached = false;

            String inputSequence = "";
            while (!endReached) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = StdDraw.nextKeyTyped();
                    if (inputSequence.length() > 0
                            && inputSequence.charAt(inputSequence.length() - 1) == ':'
                            && (c == 'q' || c == 'Q')) {
                        inputSequence += c;
                        quitAndSave(input + inputSequence);
                    }
                    inputSequence += c;
                    keyboardInput(finalWorldFrame, c, hero);
                    ter.renderFrame(finalWorldFrame);
                }
                //TODO create a door/objective to reach
                if (endReached) {
                    endReached = !endReached;
                }
            }
        }
        return finalWorldFrame;
    }

    // Function returns an array with Random object and "rest" string
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

    // @Source: Editor class
    public void loadSavedGame() {
        // get list of commands from file
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
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
        System.exit(0);
    }

    private Hero placeHero(TETile[][] grid, Random rnd) {
        boolean validPoint = false;
        int x = 0, y = 0;
        while (!validPoint) {
            x = rnd.nextInt(WIDTH);
            y = rnd.nextInt(HEIGHT);
            if (grid[x][y] == Tileset.FLOOR) {
                grid[x][y] = Tileset.AVATAR;
                validPoint = true;
            }
        }
        return new Hero(x,y);
    }
    /**
     * Move character given "WASD" movement directions
     *
     * @param grid  World to move on
     * @param input Given input character
     */
    public void keyboardInput(TETile[][] grid, char input, Hero hero) {
        switch (input) {
            case 'W':
            case 'w':
                move(grid, 0, 1,hero);
                break;
            case 'A':
            case 'a':
                move(grid, -1, 0,hero);
                break;
            case 'S':
            case 's':
                move(grid, 0, -1,hero);
                break;
            case 'D':
            case 'd':
                move(grid, 1, 0,hero);
                break;
            default:
                break;
        }
    }
    // TODO implement quit and save

    /**
     * Moves character to another adjacent tile given move direction and successfully moves if
     * coordinate is a floor. Update character's position if move is successful. Assumes coordinate
     * moving to is always in bounds of the grid
     *
     * @param grid  World to move on
     * @param moveX Change in coordinate in X direction
     * @param moveY Change in coordinate in Y direction
     */
    public static void move(TETile[][] grid, int moveX, int moveY, Hero hero) {
        int hX = hero.x;
        int hY = hero.y;

        if (grid[hX + moveX][hY + moveY] == Tileset.FLOOR) {
            grid[hX + moveX][hY + moveY] = Tileset.AVATAR;
            grid[hX][hY] = Tileset.FLOOR;
            hero.x += moveX;
            hero.y += moveY;
        } else {
            System.out.println("Invalid Move"); //TODO TAKE OUT LATER
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
     * @param grid  World to create on.
     * @param rooms Room to create.
     */
    public static void createWorld(TETile[][] grid, Room[] rooms, Random rnd) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                grid[x][y] = Tileset.NOTHING;
            }
        }

        int limit = (WIDTH + HEIGHT) / 16;
        TETile roomTile = Tileset.FLOOR;
        TETile hallwayTile = Tileset.FLOOR;
        for (int n = 0; n < rooms.length; n++) {
            int width = Math.abs(rnd.nextInt()) % limit + 2;
            int height = Math.abs(rnd.nextInt()) % limit + 2;
            rooms[n] = addRoom(grid, width, height, rnd, roomTile);
            if (n != 0 && !checkBoundary(grid, rooms[n], roomTile)
                    && !checkBoundary(grid, rooms[n], hallwayTile)) {
                connectRooms(grid, rooms[n - 1], rooms[n], rnd, hallwayTile);
            }
        }
    }

    /**
     * Checks borders if any tile equals the tile parameter
     *
     * @param grid Grid to check on
     * @param room Room to check border on
     * @param tile Tile to look for around the room
     * @return Returns true if tile found, false if not
     */
    public static boolean checkBoundary(TETile[][] grid, Room room, TETile tile) {
        for (int col = room.x; col < room.x + room.width && col < WIDTH; col++) {
            if (room.y - 1 >= 0 && grid[col][room.y - 1] == tile || ((room.y + room.height) < HEIGHT
                    && grid[col][room.y + room.height] == tile)) {
                return true;
            }
        }
        for (int row = room.y; row < room.y + room.height && row < grid.length; row++) {
            if ((room.x - 1 >= 0 && grid[room.x - 1][row] == tile)
                    || (room.x + room.width < WIDTH && grid[room.x + room.width][row] == tile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 1) Set all points within area to be a FLOOR
     * 2) Bound area with WALL if tile is NOTHING
     *
     * @param grid   World to create on
     * @param lLX    Lower Left X-coordinate
     * @param lLY    Lower Left Y-coordinate
     * @param width  Width of the are to be created
     * @param height Height of the area to be created
     */
    public static void addArea(TETile[][] grid, int lLX, int lLY,
                               int width, int height, TETile tile) {
        for (int row = Math.max(1, lLY); row < lLY + height && row < HEIGHT - 1; row++) {
            for (int col = Math.max(1, lLX); col < lLX + width && col < WIDTH - 1; col++) {
                grid[col][row] = tile;
            }
        }
        int lowerBoundX = lLX - 1;
        int lowerBoundY = lLY - 1;

        for (int col = Math.max(0, lowerBoundX); col < WIDTH && col < lLX + width + 1; col++) {
            if (lowerBoundY >= 0 && grid[col][lowerBoundY] == Tileset.NOTHING) {
                grid[col][lowerBoundY] = Tileset.WALL;
            }
            if (lLY + height < HEIGHT && grid[col][lLY + height] == Tileset.NOTHING) {
                grid[col][lLY + height] = Tileset.WALL;
            }
        }

        for (int row = lLY; row < HEIGHT && row < lLY + height; row++) {
            if (lowerBoundX >= 0 && grid[lowerBoundX][row] == Tileset.NOTHING) {
                grid[lowerBoundX][row] = Tileset.WALL;
            }
            if (lLX + width < WIDTH && grid[lLX + width][row] == Tileset.NOTHING) {
                grid[lLX + width][row] = Tileset.WALL;
            }
        }
    }

    /**
     * 1) Get two random numbers for lower left corner point to create area on
     * //TODO ENSURE ROOM AREA LIES COMPLETELY WITHIN GRID
     * 2) Set all points within area to be a FLOOR
     * 3) Bound area with WALL if tile is NOTHING
     *
     * @param grid   World to create on
     * @param width  Width of the are to be created
     * @param height Height of the area to be created
     * @return Returns initialized Room object
     */
    public static Room addRoom(TETile[][] grid, int width, int height, Random rnd, TETile tile) {
        int lLX = Math.max(1, Math.abs(rnd.nextInt()) % (WIDTH - width - 1));
        int lLY = Math.max(1, Math.abs(rnd.nextInt()) % (HEIGHT - height - 1));
        addArea(grid, lLX, lLY, width, height, tile);
        return new Room(lLX, lLY, width, height, -1);
    }

    /**
     * Connects the given rooms by creating hallways
     * Creates hallways based off orientation given by RNG
     * if orientation is true, create hallways in a mirrored L shape or upside-down mirrored L shape
     * else create hallways in a create hallways in a L shape or upside-down L shape
     *
     * @param grid
     * @param room1
     * @param room2
     */
    public static void connectRooms(TETile[][] grid, Room room1, Room room2, Random rnd, TETile tile) {
        Room leftMost = (room1.x < room2.x) ? room1 : room2;
        Room rightMost = (leftMost == room1) ? room2 : room1;

        boolean orientation = rnd.nextBoolean();
        if (orientation) {
            addHallway(grid, leftMost, rightMost, rnd, tile);
        } else {
            addHallway(grid, rightMost, leftMost, rnd, tile);
        }
    }

    /**
     * Add hallways connecting the two rooms
     * Length limits are set to the horizontal/vertical
     * distance respectively with added lengths based on room sizes
     *
     * @param grid
     * @param room1
     * @param room2
     */
    public static void addHallway(TETile[][] grid, Room room1, Room room2, Random rnd, TETile tile) {
        int give = 1;
        int widthLimit = Math.abs(room1.x - room2.x) + give;
        int heightLimit = Math.abs(room1.y - room2.y) + give;

        int Y = (Math.abs(rnd.nextInt()) % room1.height) + room1.y;
        int X = (Math.abs(rnd.nextInt()) % room2.width) + room2.x;

        if (room1.x > room2.x) {
            addArea(grid, Math.max(1, room1.x - widthLimit), Y, widthLimit, 1, tile);
        } else {
            addArea(grid, room1.x + room1.width, Y, widthLimit, 1, tile);
        }
        if (Y < room2.y) {
            addArea(grid, X, Math.max(1, room2.y - heightLimit), 1, heightLimit, tile);
        } else {
            addArea(grid, X, Math.max(1, Y - heightLimit), 1, heightLimit, tile);
        }
    }
}
