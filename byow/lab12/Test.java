package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.Random;


public class Test {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    private static final long SEED = 287313;
    private static final Random RANDOM = new Random(SEED);


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        int numRooms = Math.abs(RANDOM.nextInt()) % ((WIDTH + HEIGHT) / 2);
        int numHalls = Math.abs(RANDOM.nextInt()) % ((WIDTH + HEIGHT) / 2);
        createWorld(world, numRooms, numHalls);
        ter.renderFrame(world);
    }

    /**
     * 1) Initialize all tiles to NOTHING
     * 2) Get random numbers for number of rooms and number of halls to create
     * 3) Add the number of objects to the grid
     * a) Room size height and width are limited to at most one tenth of the world's dimension and at least 2x2
     * 2 <= Room Width <= World Width / 10 //CAN CHANGE IF WE WANT TO
     * b) Hallway length is limited to at most a third of the world's width or height depending on it's orientation.
     * Orientation is based on returned boolean value of the RNG.
     * 0 <= Hallway length <= World Height / 3 or 0 <= Hallway length <= World Width
     *
     * @param grid World to create on.
     */
    public static void createWorld(TETile[][] grid, int numRooms, int numHalls) {

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                grid[x][y] = Tileset.NOTHING;
            }
        }

        WeightedQuickUnionUF set = new WeightedQuickUnionUF(WIDTH * HEIGHT + 1);//TODO: make all connected

        for (int n = 0; n < numRooms; n++) {
            int width = Math.abs(RANDOM.nextInt()) % ((WIDTH / 5) - 2) + 2;
            int height = Math.abs(RANDOM.nextInt()) % ((HEIGHT / 5) - 2) + 2;
            addArea(grid, set, width, height);
        }

        for (int n = 0; n < numHalls; n++) {
            boolean orientation = RANDOM.nextBoolean();
            int width, height;
            if (orientation) {
                width = 1;
                height = Math.abs(RANDOM.nextInt()) % (HEIGHT / 3);
            } else {
                width = Math.abs(RANDOM.nextInt()) % (WIDTH / 3);
                height = 1;
            }
            addArea(grid, set, width, height);
        }
    }

    /**
     * 1) Get two random numbers for lower left corner point to create area on
     * 2) Set all points within area to be a FLOOR
     * 3) Bound area with WALL if tile is NOTHING
     *
     * @param grid   World to create on
     * @param set    Keep track the connectivity of the world
     * @param width  Width of the are to be created
     * @param height Height of the area to be created
     */
    public static void addArea(TETile[][] grid, WeightedQuickUnionUF set, int width, int height) {
        int LLX = Math.abs(RANDOM.nextInt()) % WIDTH;
        int LLY = Math.abs(RANDOM.nextInt()) % HEIGHT;
        //int connected = WIDTH * HEIGHT;

        for (int row = LLY; row < LLY + height && row < HEIGHT; row++) {
            for (int col = LLX; col < LLX + width && col < WIDTH; col++) {
                grid[col][row] = Tileset.FLOOR;
                //set.union(connected, row * WIDTH + col);
            }
        }

        for (int col = Math.max(0, LLX - 1); col < WIDTH && col < LLX + width + 1; col++) {
            if (LLY - 1 >= 0 && grid[col][LLY - 1] == Tileset.NOTHING) {
                grid[col][LLY - 1] = Tileset.WALL;
            }
            if (LLY + height < HEIGHT && grid[col][LLY + height] == Tileset.NOTHING) {
                grid[col][LLY + height] = Tileset.WALL;
            }
        }

        for (int row = LLY; row < LLY + height && row < HEIGHT; row++) {
            if (LLX - 1 >= 0 && grid[LLX - 1][row] == Tileset.NOTHING) {
                grid[LLX - 1][row] = Tileset.WALL;
            }
            if (LLX + width < WIDTH && grid[LLX + width][row] == Tileset.NOTHING) {
                grid[LLX + width][row] = Tileset.WALL;
            }
        }
    }
}
