public class SudokuSolver {

    // Solve a standard 9x9 Sudoku board using backtracking
    public boolean solve(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) { // empty cell
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (solve(board))
                                return true;
                            board[row][col] = 0; // backtrack
                        }
                    }
                    return false; // no valid number
                }
            }
        }
        return true; // solved
    }

    // Check if placing 'num' in board[row][col] is valid
    private boolean isSafe(int[][] board, int row, int col, int num) {
        for (int x = 0; x < 9; x++) {
            if (board[row][x] == num || board[x][col] == num)
                return false;
        }
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[startRow + i][startCol + j] == num)
                    return false;
        return true;
    }
}
