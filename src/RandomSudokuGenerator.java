import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

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
        
        // Ensure minimum of 1 given cell for non-zero percentage
        if (givenPercentage > 0 && numGivenCells == 0) {
            numGivenCells = 1;
        }

        int[][][] sudoku = new int[size][size][2];
        
        if (startEmpty) {
            // Start from completely empty board
            initializeEmptyBoard(sudoku);
            System.out.println("Starting from empty board, generating " + numGivenCells + " given cells (" + givenPercentage + "%)");
        } else if (userBoard != null) {
            // Copy user-provided board
            copyBoard(sudoku, userBoard);
            System.out.println("Using user-provided board as base");
        } else {
            // If no user board provided but startEmpty is false, start with empty
            initializeEmptyBoard(sudoku);
            System.out.println("No user board provided, starting from empty");
        }

        int existingGiven = countGiven(sudoku);
        System.out.println("Existing given cells: " + existingGiven);
        
        // Validate user input
        if (!startEmpty && existingGiven > numGivenCells) {
            String errorMsg = "User provided " + existingGiven + " given cells, but maximum allowed is " + numGivenCells;
            System.out.println(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        if (startEmpty) {
            // Generate complete solution first, then remove cells
            sudoku = generateFromCompleteSolution(numGivenCells);
        } else {
            // Add missing givens randomly without violating user's cells
            int cellsToAdd = numGivenCells - existingGiven;
            System.out.println("Adding " + cellsToAdd + " more given cells");
            if (cellsToAdd > 0) {
                addMissingGivenCells(sudoku, cellsToAdd);
            }
        }

        // Final validation
        if (!validateFullBoard(sudoku)) {
            System.out.println("Generated board invalid! Using fallback generation...");
            sudoku = generateFallback(givenPercentage, numGivenCells);
        }

        int finalGivenCount = countGiven(sudoku);
        System.out.println("Final Sudoku generated with " + finalGivenCount + " given cells (target: " + numGivenCells + ")");
        
        return sudoku;
    }

    private void initializeEmptyBoard(int[][][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j][0] = 0;  // Empty value
                board[i][j][1] = 1;  // Editable (not given)
            }
        }
    }

    private void copyBoard(int[][][] target, int[][][] source) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                target[i][j][0] = source[i][j][0];
                target[i][j][1] = source[i][j][1];
            }
        }
    }

    private int[][][] generateFromCompleteSolution(int numGivenCells) {
        System.out.println("Generating complete solution...");
        int[][][] complete = generateCompleteSolution();
        
        if (complete == null) {
            System.out.println("Failed to generate complete solution, using fallback");
            return generateFallback(25.0, numGivenCells); // Fallback with 25% given cells
        }
        
        // Remove cells to achieve desired percentage
        return removeCellsFromSolution(complete, numGivenCells);
    }

    private int[][][] generateCompleteSolution() {
        int[][][] solution = new int[size][size][2];
        initializeEmptyBoard(solution);
        
        // Use backtracking with random number selection
        boolean solved = solveSudoku(solution, 0, 0);
        
        if (!solved) {
            System.out.println("Backtracking failed to find solution");
            return null;
        }
        
        return solution;
    }

    private boolean solveSudoku(int[][][] board, int row, int col) {
        if (row == size) {
            return true; // Solved
        }
        
        if (col == size) {
            return solveSudoku(board, row + 1, 0);
        }
        
        // Skip already filled cells
        if (board[row][col][0] != 0) {
            return solveSudoku(board, row, col + 1);
        }
        
        // Try numbers in random order
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        
        for (int num : numbers) {
            if (isValidPlacement(board, row, col, num)) {
                board[row][col][0] = num;
                
                if (solveSudoku(board, row, col + 1)) {
                    return true;
                }
                
                board[row][col][0] = 0; // Backtrack
            }
        }
        
        return false;
    }

    private boolean isValidPlacement(int[][][] board, int row, int col, int num) {
        // Check row
        for (int j = 0; j < size; j++) {
            if (board[row][j][0] == num) {
                return false;
            }
        }
        
        // Check column
        for (int i = 0; i < size; i++) {
            if (board[i][col][0] == num) {
                return false;
            }
        }
        
        // Check subgrid
        int boxRowStart = (row / subSize) * subSize;
        int boxColStart = (col / subSize) * subSize;

        for (int r = boxRowStart; r < boxRowStart + subSize; r++) {
            for (int c = boxColStart; c < boxColStart + subSize; c++) {
                if (board[r][c][0] == num) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private int[][][] removeCellsFromSolution(int[][][] solution, int numGivenCells) {
        int[][][] puzzle = new int[size][size][2];
        
        // Copy the complete solution
        copyBoard(puzzle, solution);
        
        // Mark all as given initially
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                puzzle[i][j][1] = 0; // Given cells
            }
        }
        
        // Remove cells to achieve desired number of givens
        int cellsToRemove = (size * size) - numGivenCells;
        ArrayList<int[]> positions = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                positions.add(new int[]{i, j});
            }
        }
        
        Collections.shuffle(positions);
        
        int removed = 0;
        for (int[] pos : positions) {
            if (removed >= cellsToRemove) break;
            
            int row = pos[0];
            int col = pos[1];
            
            puzzle[row][col][0] = 0;
            puzzle[row][col][1] = 1; // Make editable
            removed++;
        }
        
        return puzzle;
    }

    private void addMissingGivenCells(int[][][] board, int cellsToAdd) {
        if (cellsToAdd <= 0) return;
        
        ArrayList<int[]> emptyPositions = new ArrayList<>();
        
        // Find all empty positions
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j][0] == 0) {
                    emptyPositions.add(new int[]{i, j});
                }
            }
        }
        
        Collections.shuffle(emptyPositions);
        
        int added = 0;
        int attempts = 0;
        int maxAttempts = emptyPositions.size() * 3; // Prevent infinite loop
        
        while (added < cellsToAdd && attempts < maxAttempts && !emptyPositions.isEmpty()) {
            int[] pos = emptyPositions.remove(0);
            int row = pos[0];
            int col = pos[1];
            
            // Try to find a valid number for this position
            ArrayList<Integer> validNumbers = new ArrayList<>();
            for (int num = 1; num <= size; num++) {
                if (isValid(board, row, col, num)) {
                    validNumbers.add(num);
                }
            }
            
            if (!validNumbers.isEmpty()) {
                int num = validNumbers.get(rand.nextInt(validNumbers.size()));
                board[row][col][0] = num;
                board[row][col][1] = 0; // Mark as given
                added++;
                System.out.println("Added given cell at (" + row + "," + col + ") = " + num);
            } else {
                // No valid number, put back and try another position
                emptyPositions.add(pos);
            }
            
            attempts++;
        }
        
        if (added < cellsToAdd) {
            System.out.println("Warning: Only added " + added + " of " + cellsToAdd + " requested given cells");
        }
    }

    private int[][][] generateFallback(double givenPercentage, int numGivenCells) {
        // Simple fallback: generate minimal valid puzzle
        System.out.println("Using fallback generation");
        int[][][] sudoku = new int[size][size][2];
        initializeEmptyBoard(sudoku);
        
        // Just add a few valid numbers in diagonal pattern
        int cellsToAdd = Math.min(numGivenCells, size);
        for (int i = 0; i < cellsToAdd; i++) {
            int num = (i % size) + 1;
            sudoku[i][i][0] = num;
            sudoku[i][i][1] = 0; // Given
        }
        
        return sudoku;
    }

    private int countGiven(int[][][] board) {
        int count = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board[i][j][0] != 0 && board[i][j][1] == 0) // Count only given cells
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
        // Check rows and columns
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
