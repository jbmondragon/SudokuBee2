import java.util.HashSet;
import java.util.Random;

public class RandomSudokuGenerator {

    private int size;
    private int subSize;
    private Random rand = new Random();

    public RandomSudokuGenerator(int size) {
        this.size = size;
        this.subSize = (int) Math.sqrt(size);
    }

    // --- Main entry point ---
    public int[][][] generate(double givenPercentage, boolean startEmpty, int[][][] userBoard) {
        int totalCells = size * size;
        int numGivenCells = (int) Math.round((givenPercentage / 100.0) * totalCells);

        int[][][] sudoku = new int[size][size][2];
        if (!startEmpty && userBoard != null) {
            // Copy user-provided board
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    sudoku[i][j][0] = userBoard[i][j][0];
                    sudoku[i][j][1] = userBoard[i][j][1];
                }
            }
        }

        int existingGiven = countGiven(sudoku);
        if (existingGiven > numGivenCells) {
            System.out.println("⚠ Too many given cells (" + existingGiven + " > " + numGivenCells + ")");
            return null; // invalid input
        }

        // Add missing givens randomly
        while (countGiven(sudoku) < numGivenCells) {
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            if (sudoku[x][y][0] == 0) {
                int val = rand.nextInt(size) + 1;
                if (isValid(sudoku, x, y, val)) {
                    sudoku[x][y][0] = val;
                    sudoku[x][y][1] = 1; // mark as given
                }
            }
        }

        if (!validateFullBoard(sudoku)) {
            System.out.println("❌ Generated board invalid! Regenerating...");
            return generate(givenPercentage, startEmpty, userBoard);
        }

        System.out.println("✅ Random Sudoku generated (" + numGivenCells + " givens)");
        return sudoku;
    }

    private int countGiven(int[][][] board) {
        int count = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board[i][j][0] != 0)
                    count++;
        return count;
    }

    private boolean isValid(int[][][] board, int row, int col, int num) {
        // Row and column check
        for (int i = 0; i < size; i++) {
            if (board[row][i][0] == num || board[i][col][0] == num)
                return false;
        }

        // Subgrid check
        int boxRowStart = (row / subSize) * subSize;
        int boxColStart = (col / subSize) * subSize;

        for (int r = boxRowStart; r < boxRowStart + subSize; r++) {
            for (int c = boxColStart; c < boxColStart + subSize; c++) {
                if (board[r][c][0] == num)
                    return false;
            }
        }
        return true;
    }

    private boolean validateFullBoard(int[][][] board) {
        for (int r = 0; r < size; r++) {
            HashSet<Integer> rowSet = new HashSet<>();
            HashSet<Integer> colSet = new HashSet<>();
            for (int c = 0; c < size; c++) {
                int rv = board[r][c][0];
                int cv = board[c][r][0];
                if (rv != 0 && !rowSet.add(rv))
                    return false;
                if (cv != 0 && !colSet.add(cv))
                    return false;
            }
        }

        // Subgrid validation
        for (int boxRow = 0; boxRow < size; boxRow += subSize) {
            for (int boxCol = 0; boxCol < size; boxCol += subSize) {
                HashSet<Integer> boxSet = new HashSet<>();
                for (int r = 0; r < subSize; r++) {
                    for (int c = 0; c < subSize; c++) {
                        int val = board[boxRow + r][boxCol + c][0];
                        if (val != 0 && !boxSet.add(val))
                            return false;
                    }
                }
            }
        }

        return true;
    }
}
