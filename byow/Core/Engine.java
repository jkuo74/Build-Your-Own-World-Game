package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    // private static final long SEED = 287313;
    // private static final Random RANDOM = new Random(SEED);

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        char prefix = input.charAt(0);
        char suffix = input.charAt(input.length() - 1);
        String seedString = input.substring(1, input.length() - 1);

        // Check prefix, suffix, and that input contains only digits.
        if ((prefix != 'N' && prefix != 'n') || (suffix != 'S'
            && suffix != 's') || !seedString.matches("\\d+")) {
            return finalWorldFrame;
        }

        long seed = Long.parseLong(seedString);
        Random rnd = new Random(seed);

        int maxGenFactor = (WIDTH + HEIGHT) / 2;
        int minGenFactor = (WIDTH + HEIGHT) / 10;

        // numRooms is a between minGenFactor to maxGenFactor
        int numRooms = (Math.abs(rnd.nextInt()) % (maxGenFactor - minGenFactor)) + minGenFactor;

        // numHalls is a between numRooms to 2*numRooms
        int numHalls = (Math.abs(rnd.nextInt()) % numRooms) + numRooms;
        Room[] rooms = new Room[numRooms];

        createWorld(finalWorldFrame, rooms, rnd);
        ter.renderFrame(finalWorldFrame);

        return finalWorldFrame;
    }


    /**
     * 1) Initialize all tiles to NOTHING
     * 2) Add the number of Rooms based off passed in Room array size
     * 3) Room size height and width are limited to the sum of the HEIGHT and WIDTH over 16 CAN CHANGE
     * 4) Connect initialized room wiht previously initialized room if it is not already connected
     * //TODO CREATE HALLWAY OBJECTS?
     * @param grid World to create on.
     * @param rooms Room to create.
     */
    public static void createWorld(TETile[][] grid, Room[] rooms, Random rnd) {//, ArrayList<Hallway> hallways) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                grid[x][y] = Tileset.NOTHING;
            }
        }

        int limit = (WIDTH + HEIGHT) / 16;
        for (int n = 0; n < rooms.length; n++) {
            int width = Math.abs(rnd.nextInt()) % limit + 2;
            int height = Math.abs(rnd.nextInt()) % limit + 2;
            rooms[n] = addRoom(grid, width, height, rnd);
            if (n != 0 && !checkBoundary(grid, rooms[n], Tileset.FLOOR)) {
                connectRooms(grid, rooms[n - 1], rooms[n], rnd);
            }
        }
    }

    /**
     * Checks borders if any tile equals the tile parameter
     * @param grid Grid to check on
     * @param room Room to check border on
     * @param tile Tile to look for around the room
     * @return Returns true if tile found, false if not
     */
    public static boolean checkBoundary(TETile[][] grid, Room room, TETile tile) {
        for (int col = room.x; col < room.x + room.width; col++) {
            if (grid[col][room.y] == tile || grid[col][room.y + room.height] == tile) {
                return true;
            }
        }
        for (int row = room.y; row < room.y + room.height && row < grid.length; row++) {
            if (grid[room.x][row] == tile || grid[room.x + room.width][row] == tile) {
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
     * @param LLX    Lower Left X-coordinate
     * @param LLY    Lower Left Y-coordinate
     * @param width  Width of the are to be created
     * @param height Height of the area to be created
     */
    public static void addArea(TETile[][] grid, int LLX, int LLY, int width, int height) {
        for (int row = LLY + 1; row < LLY + height - 1 && row < HEIGHT - 1; row++) {
            for (int col = LLX + 1; col < LLX + width - 1 && col < WIDTH - 1; col++) {
                grid[col][row] = Tileset.FLOOR;
            }
        }
        for (int col = LLX; col < WIDTH && col < LLX + width; col++) {
            if (grid[col][LLY] == Tileset.NOTHING) {
                grid[col][LLY] = Tileset.WALL;
            }
            if (grid[col][LLY + height - 1] == Tileset.NOTHING) {
                grid[col][LLY + height - 1] = Tileset.WALL;
            }
        }

        for (int row = LLY; row < HEIGHT && row < LLY + height; row++) {
            if (grid[LLX][row] == Tileset.NOTHING) {
                grid[LLX][row] = Tileset.WALL;
            }
            if (grid[LLX + width - 1][row] == Tileset.NOTHING) {
                grid[LLX + width - 1][row] = Tileset.WALL;
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
    public static Room addRoom(TETile[][] grid, int width, int height, Random rnd) {
        int LLX = Math.max(1, Math.abs(rnd.nextInt()) % (WIDTH - width - 1));
        int LLY = Math.max(1, Math.abs(rnd.nextInt()) % (HEIGHT - height - 1));
        addArea(grid, LLX, LLY, width, height);
        return new Room(LLX, LLY, width, height, -1);
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
    public static void connectRooms(TETile[][] grid, Room room1, Room room2, Random rnd) {
        Room leftMost = (room1.x < room2.x) ? room1 : room2;
        Room rightMost = (leftMost == room1) ? room2 : room1;

        boolean orientation = rnd.nextBoolean();
        if (orientation) {
            addHallway(grid, leftMost, rightMost, rnd);
        } else {
            addHallway(grid, rightMost, leftMost, rnd);
        }
    }

    /**
     * Add hallways connecting the two rooms
     * Length limits are set to the horizontal/vertical distance respectively with added lengths based on room sizes
     *
     * @param grid
     * @param room1
     * @param room2
     */
    public static void addHallway(TETile[][] grid, Room room1, Room room2, Random rnd) {
        int widthLimit = Math.abs(room1.x - room2.x) + (Math.abs(rnd.nextInt()) % Math.max(room1.width, room2.width));
        int heightLimit = Math.abs(room1.y - room2.y) + (Math.abs(rnd.nextInt()) % Math.max(room1.height, room2.height));

        int Y = (Math.abs(rnd.nextInt()) % room1.height) + room1.y;
        int X = (Math.abs(rnd.nextInt()) % room2.width) + room2.x;

        if (room1.x > room2.x) {
            addArea(grid, Math.max(0, room1.x - widthLimit), Y, widthLimit, 3);
        } else {
            addArea(grid, room1.x + room1.width, Y, widthLimit, 3);
        }
        if (room1.y < room2.y) {
            addArea(grid, X, Math.max(0, room2.y - heightLimit), 3, heightLimit);
        } else {
            addArea(grid, X, Math.max(0, Y - heightLimit), 3, heightLimit);
        }
    }

}
