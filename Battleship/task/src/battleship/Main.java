package battleship;

import java.util.Scanner;
import java.lang.Math;

/**
 * Battleships game, completed as part of jetbrains academy java basics course,
 */

public class Main {

    public static void main(String[] args) {

        // Created two boards for each player one public one fogged, the public board is where the player can
        //view their own ship placements, the fogged board is where hits and misses on opposing player are displayed
        gameBoard player1Public = new gameBoard();
        gameBoard player1Fogged = new gameBoard();
        gameBoard player2Public = new gameBoard();
        gameBoard player2Fogged = new gameBoard();

        //Issue each player with a fleet of ships
        fleet player1Fleet = new fleet();
        fleet player2Fleet = new fleet();

        System.out.println("Player 1, place your ships on the game field");

        player1Public.drawBoard();

        player1Fleet.deploy(player1Public);

        System.out.println("Press Enter and pass the move to another player");
        System.out.println("...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        System.out.println("Player 2, place your ships on the game field");

        player2Fleet.deploy(player2Public);

        gameTurn(player1Public, player1Fogged, player2Public, player2Fogged, player1Fleet, player2Fleet);


    }

    /**
     * Main game loop
     * @param player1Public board showing player's ships
     * @param player1Fogged board showing player 2 attacks on player 1
     * @param player2Public board showing player's ships
     * @param player2Fogged board showing player 1 attacks on player 2
     * @param player1Fleet set of ships belonging to player 1
     * @param player2Fleet set of ships belonging to player 2
     */
    public static void gameTurn(gameBoard player1Public, gameBoard player1Fogged,
                                gameBoard player2Public, gameBoard player2Fogged,
                                fleet player1Fleet, fleet player2Fleet) {

        String activePlayer = "player1";
        Scanner scanner = new Scanner(System.in);

        boolean gameOver = false;
        while (!gameOver) {

            System.out.println("Press Enter and pass the move to another player");
            System.out.println("...");
            scanner.nextLine();


            if (activePlayer.equals("player1")) {

                player2Fogged.drawBoard();
                System.out.println("---------------------");
                player1Public.drawBoard();

                System.out.println("Player 1, it's your turn:");

                gameOver = fireAtWill(player2Public, player2Fogged, player2Fleet);

                activePlayer = "player2";

            } else {

                player1Fogged.drawBoard();
                System.out.println("---------------------");
                player2Public.drawBoard();

                System.out.println("Player 2, it's your turn:");
                gameOver = fireAtWill(player1Public, player1Fogged, player1Fleet);

                activePlayer = "player1";
            }


        }
    }

    /**
     * Accepts target coordinates from player and calculates if shot missed or hit and if hit, if ship was sunk, also
     * tracks number of ships each player has afloat
     * @param board  opposing players public board for use in this turn
     * @param fogBoard board showing attacks made by current player
     * @param ships fleet belonging to current player
     * @return true if game over - all ships of opposing player sunk
     */
    public static boolean fireAtWill(gameBoard board, gameBoard fogBoard, fleet ships) {

        Scanner scanner = new Scanner(System.in);

        String target = scanner.next();

        int targetL = (int) target.charAt(0) - 64;
        int targetN = Integer.parseInt(target.substring(1));
        //check target coordinates entered fall within game boundaries
        if (targetL > 0 && targetL < board.getHeight() - 1 && targetN > 0 && targetN < board.getWidth() - 1) {

            //Registers miss - target square contained sea "~" or a previously missed shot "M"
            if (board.getSquare(targetL, targetN).equals("~") || (board.getSquare(targetL, targetN).equals("M"))) {
                board.setSquare(targetL, targetN, "M");
                fogBoard.setSquare(targetL, targetN, "M");
                System.out.println("You missed!");
            //Registers hit - target square contains ship "O"
            } else if (board.getSquare(targetL, targetN).equals("O")) {
                board.setSquare(targetL, targetN, "X");
                fogBoard.setSquare(targetL, targetN, "X");
                //iterate over opposing players fleet to find which ship hit
                for (int q = 0; q < 5; q++) {
                    ship shipX = ships.getShip(q);
                    //iterate over length of ship to find where it was hit
                    for (int i = 0; i < shipX.getSize(); i++) {
                        //Reduces hit points of ship by 1
                        if (shipX.getCellL(i) == targetL && shipX.getCellN(i) == targetN) {
                            shipX.setHit();
                            //Check if ship has remaining hit points
                            if (shipX.sunk()) {

                                ships.setSunk();
                                //Is this the last ship?
                                if (ships.getAfloat() > 0) {
                                    System.out.println("You sank a ship!");
                                } else {
                                    System.out.println("You sank the last ship. You won. Congratulations!");
                                    return true;
                                }
                                //ship survived
                            } else {
                                System.out.println("You hit a ship!");
                            }

                        }
                    }
                }
            //Ship already hit in that location no further damage
            } else if (board.getSquare(targetL, targetN).equals("X")) {
                System.out.println("You hit a ship!\n");
            }
        } else {
            System.out.println("Error! You entered the wrong coordinates! Try again:\n");
        }
        return false;
    }
}

/**
 * Creates a string array to represent the game board, 12*12 grid first column and row hold letter and number
 * labels, an additional row and column below and to the right of the visible game space is included as a buffer, which
 * has been done to simplify the process of checking if a player may legally place a ship at a given location, by
 * removing the need to account for ships touching the board edges - any ship legally placed will be surrounded by
 * "empty" cells.
 */
class gameBoard {

    private String[][] board;
    private final int height;
    private final int width;


    //Constructor for standard board 10*10 play space.

    /**
     * Creates standard 10 * 10 playing space, incl labels and buffer
     */
    public gameBoard() {

        this.height = 12;
        this.width = 12;
        createBoard();
    }

    /**
     *
     * @return height of the board including labels and buffer row
     */
    public int getHeight() {
        return this.height;
    }

    /**
     *
     * @return width of the board including labels and buffer column
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * creates and populates 12 * 12 game board,
     * first row populated with numeric labels
     * first column populated with letter labels
     * remaining space populated with "~"
     */
    public void createBoard() {

        this.board = new String[this.height][this.width];

        //Set top left square as empty string
        this.board[0][0] = " ";

        //set up numbers in first row of board
        for (int i = 1; i < this.width - 1; i++) {
            this.board[0][i] = String.valueOf(i);
        }

        // set up letters A-J in first column
        for (int j = 1; j < this.height - 1; j++) {
            this.board[j][0] = Character.toString((char) j + 64);
        }

        //fill remaining spaces with "~" denoting fog of war
        for (int p = 1; p < this.height; p++) {
            for (int q = 1; q < this.width; q++) {
                board[p][q] = "~";
            }
        }
    }

    //Draws game board to console

    /**
     * Draws board to console
     */
    public void drawBoard() {

        System.out.println();
        for (int i = 0; i < this.height - 1; i++) {

            for (int j = 0; j < this.width - 1; j++) {
                System.out.print(this.board[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println();
    }

    //Returns value of specified square

    /**
     *
     * @param L Letter - vertical coordinate
     * @param N Number - horizontal coordinate
     * @return value of cell
     */
    public String getSquare(int L, int N) {

        return this.board[L][N];
    }

    //Sets value of specified square

    /**
     *
     * @param L Letter - vertical coordinate
     * @param N Number - horizontal coordinate
     * @param value value to be set
     */
    public void setSquare(int L, int N, String value) {

        this.board[L][N] = value;
    }

    // converts input in form G1 G5 to numeric coordinates

    /**
     * gets letter and number coordinates from user and converts to numeric coordinates, may be entered in any order
     * @return  numeric coordinates, in ascending order
     */
    public int[] getCoordinates() {

        int[] coordinates = new int[4];

        Scanner scanner = new Scanner(System.in);
        String Start;
        String End;

        Start = scanner.next();
        End = scanner.next();

        //first letter, last letter - converted to numeric values from ASCII character codes hence the -64
        coordinates[0] = Math.min((int) Start.toUpperCase().charAt(0) - 64, (int) End.charAt(0) - 64);
        coordinates[1] = Math.max((int) Start.toUpperCase().charAt(0) - 64, (int) End.charAt(0) - 64);

        //first number, last number
        coordinates[2] = Math.min(Integer.parseInt(Start.substring(1)), Integer.parseInt(End.substring(1)));
        coordinates[3] = Math.max(Integer.parseInt(Start.substring(1)), Integer.parseInt(End.substring(1)));

        return coordinates;
    }

    // checks all coordinates in range 1-10 (incl)

    /**
     * checks coordinates provided by user for placing a ship fall withing game area
     * @param coordinates list of four integer values representing Start letter, End letter, Start Number, End Number
     * @return true if coordinates valid
     */
    public boolean checkCoordinates(int[] coordinates) {

        return coordinates[0] > 0 && coordinates[0] < this.height - 1
                && coordinates[1] > 0 && coordinates[1] < this.height - 1
                && coordinates[2] > 0 && coordinates[2] < this.width - 1
                && coordinates[3] > 0 && coordinates[3] < this.width - 1;
    }

    /**
     *
     * @param startL Letter of first cell on which ship is to be placed
     * @param startN Number of first cell on which ship is to b placed
     * @param length length of ship
     * @param orientation whether ship to be placed horizontally or vertically.
     * @return true if ship may be placed in given position
     */
    public boolean checkOccupied(int startL, int startN, int length, String orientation) {

        boolean canPlace = false;

        int occupied = 0;
        if (orientation.equals("Horizontal")) {

            occupied = getOccupied(startL, startN, length);

        } else if (orientation.equals("Vertical")) {

            occupied = getOccupied(startN, startL, length);

        }

        if (occupied == 0) {
            canPlace = true;
        }


        return canPlace;
    }

    /**
     * Checks if proposed ship location and adjacent cells are occupied - iterates over rectangular array of
     * cells from 1 cell to right and above proposed location to 1 cell left and below.
     * @param start1 Letter or number of first cell on which ship is to be placed depending on orientation
     * @param start2 Number or letter of first cell on which ship is to b placed depending on orientation
     * @param length length of ship
     * @return count of occupied cells in area checked 
     */
    private int getOccupied(int start1, int start2, int length) {

        int occupied = 0;
        for (int m = start2 - 1; m < start1 + 1; m++) {
            for (int n = start1 - 1; n < start2 + 1 + length; n++) {
                if (this.getSquare(m, n).equals("O")) {
                    occupied += 1;
                }
            }
        }
        return occupied;
    }
}

/**
 * Individual ships of varying sizes for players to place on the game board
 */
class ship {

    private final String name;
    private final int size;
    //Array coordinates occupied by ship once placed on board
    private final int[][] cells;
    private int hits;

    public ship(String name, int size) {

        this.name = name;
        this.size = size;
        this.cells = new int[size][2];

    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Adds cell to list of cells occupied by a ship
     * @param X cell number
     * @param L letter coordinate
     * @param N Number coordinate
     */
    public void setCell(int X, int L, int N) {

        this.cells[X][0] = L;
        this.cells[X][1] = N;
    }

    public int getCellL(int X) {
        return this.cells[X][0];
    }

    public int getCellN(int X) {
        return this.cells[X][1];
    }

    public void setHit() {
        this.hits += 1;
    }

    public boolean sunk() {

        return this.hits == this.size;
    }
}

/**
 * Collection of ships provided to each player
 */
class fleet {

    private final ship[] fleet = new ship[5];
    // tracks number of ships player has afloat reduced by one each time a ship is sunk
    private int afloat = 5;

    /**
     * sets up standard fleet of five ships for each player
     */
    public fleet() {

        ship carrier = new ship("Aircraft Carrier", 5);
        ship battleship = new ship("Battleship", 4);
        ship submarine = new ship("Submarine", 3);
        ship cruiser = new ship("Cruiser", 3);
        ship destroyer = new ship("Destroyer", 2);

        this.fleet[0] = carrier;
        this.fleet[1] = battleship;
        this.fleet[2] = submarine;
        this.fleet[3] = cruiser;
        this.fleet[4] = destroyer;
    }

    /**
     *
     * @param x number of ship in fleet see above
     * @return ship object e.g. submarine
     */
    public ship getShip(int x) {
        return this.fleet[x];
    }

    /**
     *
     * @return number of ships player has afloat
     */
    public int getAfloat() {
        return this.afloat;
    }

    /**
     * Reduces afloat value when ship is sunk
     */
    public void setSunk() {
        this.afloat -= 1;
    }

    /**
     * Allows player to place each ship in their fleet on the board, calls functions to check coordinates provided
     * are valid, that coordinates provided are correct for ship size, and that the ship is not being placed adjacent
     * to another ship
     * @param board class consisting of 12*12 String array
     */
    public void deploy(gameBoard board) {

        for (ship deployment : this.fleet) {

            label:
            while (true) {

                System.out.println("Enter the coordinates of the " + deployment.getName() +
                        "(" + deployment.getSize() + " cells):\n");

                int[] coordinates = board.getCoordinates();

                int StartL = coordinates[0];
                int EndL = coordinates[1];

                int StartN = coordinates[2];
                int EndN = coordinates[3];

                int length = deployment.getSize();

                if (board.checkCoordinates(coordinates)) {

                    String orientation = checkOrientation(StartL, EndL, StartN, EndN);

                    switch (orientation) {
                        case "invalid":
                            System.out.println("Error! Wrong ship location! Try again:\n");
                            break;
                        case "Horizontal":
                            if (checkLength(StartN, EndN, length)) {
                                if (placeHorizontal(board, StartN, StartL, deployment)) {
                                    board.drawBoard();
                                    break label;
                                }
                            }
                            System.out.println("Error! Wrong length of the " + deployment.getName() + " Try again:\n");
                            break;
                        case "Vertical":
                            if (checkLength(StartL, EndL, length)) {
                                if (placeVertical(board, StartL, StartN, deployment)) {
                                    board.drawBoard();
                                    break label;
                                }
                            }
                            System.out.println("Error! Wrong length of the " + deployment.getName() + " Try again:\n");
                            break;
                    }
                }
            }
        }
    }

    public boolean checkLength(int start, int end, int length) {

        return end - start == length - 1;
    }

    /**
     * Checks if ship is being placed horizontally or vertically on game board
     * @param StartL int representing first letter coordinate
     * @param EndL int representing last letter coordinate
     * @param StartN int representing first number coordinate
     * @param EndN int representing last number coordinate
     * @return String to indicate orientation of ship to be placed.
     */
    public String checkOrientation(int StartL, int EndL, int StartN, int EndN) {

        String Orientation;
        if (StartL != EndL && StartN != EndN) {
            Orientation = "invalid";
        } else if (StartL == EndL) {
            Orientation = "Horizontal";
        } else {
            Orientation = "Vertical";
        }
        return Orientation;
    }

    /**
     * Places ship horizontally on game board
     * @param board class consisting of 12*12 String array representing game board
     * @param StartL First letter coordinate - numeric value
     * @param StartN First number coordinate
     * @param ship ship to be placed
     * @return true if ship has been placed
     */
    public boolean placeHorizontal(gameBoard board, int StartN, int StartL, ship ship) {

        boolean placed = false;

        if (board.checkOccupied(StartL, StartN, ship.getSize(), "Horizontal")) {
            for (int j = 0; j < ship.getSize(); j++) {
                board.setSquare(StartL, StartN + j, "O");
                ship.setCell(j, StartL, StartN + j);
                placed = true;
            }
        }
        return placed;
    }

    /**
     * Places ship vertically on game board
     * @param board class consisting of 12*12 String array representing game board
     * @param StartL First letter coordinate - numeric value
     * @param StartN First number coordinate
     * @param ship ship to be placed
     * @return true if ship has been placed
     */
    public boolean placeVertical(gameBoard board, int StartL, int StartN, ship ship) {

        boolean placed = false;

        if (board.checkOccupied(StartL, StartN, ship.getSize(), "Vertical")) {
            for (int j = 0; j < ship.getSize(); j++) {
                board.setSquare(StartL + j, StartN, "O");
                ship.setCell(j, StartL + j, StartN);
                placed = true;
            }

        }
        return placed;
    }
}


