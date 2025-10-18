import java.util.Random;

class GenerateSudoku {
	private int[][][] sudoku;
	private Random rand = new Random();

	GenerateSudoku(int[][][] sudoku) {
		this.sudoku = sudoku;
		for (int ctr = 0; ctr < sudoku.length; ctr++) {
			for (int ct = ctr; ct < sudoku.length; ct++) {
				double first = rand.nextDouble(), second = rand.nextDouble();
				if (first > 1 - second) {
					this.sudoku[ct][ctr][0] = 0;
					this.sudoku[ct][ctr][1] = 1;
				} else {
					this.sudoku[ct][ctr][1] = 0;
				}
				if (ct != ctr && first > 1 - second) {
					this.sudoku[ctr][ct][0] = 0;
					this.sudoku[ctr][ct][1] = 1;
				} else if (ct != ctr) {
					this.sudoku[ctr][ct][1] = 0;
				}
			}
		}
	}

	protected int[][][] getSudoku() {
		return sudoku;
	}

	private void sop(Object obj) {
		System.out.println(obj + "");
	}

	public void printSudoku() {
		System.out.println("Generated Sudoku-like 3D array:");
		for (int i = 0; i < sudoku.length; i++) {
			for (int j = 0; j < sudoku[i].length; j++) {
				System.out.print("[");
				for (int k = 0; k < sudoku[i][j].length; k++) {
					System.out.print(sudoku[i][j][k]);
					if (k < sudoku[i][j].length - 1)
						System.out.print(",");
				}
				System.out.print("] ");
			}
			System.out.println();
		}
		System.out.println();
	}

}