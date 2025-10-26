import java.util.Random;

class Bee {
	private int[][][] solution;
	private double fitness;
	private Subgrid[] subgrid;
	private Random rand = new Random();

	Bee(Subgrid[] subgrid) {
		this.subgrid = subgrid;
	}

	Bee(int[][][] prob, Subgrid[] subgrid) {
		solution = prob;
		this.subgrid = subgrid;
		for (int ctr = 0; ctr < subgrid.length; ctr++) {
			int[] needed = neededNumbers(subgrid[ctr]);
			for (int y = subgrid[ctr].getStartY(), indexRand = needed.length,
					limY = y + subgrid[ctr].getDimY(); y < limY; y++) {
				for (int x = subgrid[ctr].getStartX(), limX = x + subgrid[ctr].getDimX(); x < limX; x++) {
					if (solution[y][x][1] == 1) {
						int tmp = rand.nextInt(indexRand);
						solution[y][x][0] = needed[tmp];
						needed[tmp] = needed[indexRand - 1];
						needed[indexRand - 1] = solution[y][x][0];
						indexRand = indexRand - 1;
					}
				}
			}
		}
	}

	protected void copyProblem(int[][][] prob) {
		solution = prob;
	}

	protected void printResult() {
		for (int ctr = 0; ctr < solution.length; ctr++) {
			for (int ctr1 = 0; ctr1 < solution[ctr].length; ctr1++) {
				System.out.print(solution[ctr][ctr1][0] + "");
			}
			System.out.println("");
		}
	}

	protected int getPenaltyValue() {
		int penalty = 0;
		customSet hor = new customSet();
		customSet ver = new customSet();
		for (int ctr = 0; ctr < solution.length; ctr++) {
			hor.clear();
			ver.clear();
			for (int ct = 0; ct < solution.length; ct++) {
				if (hor.contains(solution[ctr][ct][0]))
					penalty++;
				else
					hor.add((solution[ctr][ct][0]));
				if (ver.contains(solution[ct][ctr][0]))
					penalty++;
				else
					ver.add((solution[ct][ctr][0]));
			}
		}
		return penalty;
	}

	public double evaluate(int[][][] sudoku) {
		double fitness = 0.0;

		switch (SudokuBee2.penaltyType) {
			case 0:
				// System.out.println("Using penalty: Missing numbers with sub-grid
				// constraint");
				fitness = missingNumbersPenalty(sudoku, true);
				break;
			case 1:
				// System.out.println("Using penalty: Sum-Product with sub-grid constraint");
				fitness = sumProductPenalty(sudoku, true);
				break;
			case 2:
				// System.out.println("Using penalty: Sum-Product without sub-grid constraint");
				fitness = sumProductPenalty(sudoku, false);
				break;
			default:
				// System.out.println("Using default penalty: Missing numbers");
				fitness = missingNumbersPenalty(sudoku, true);
		}

		return fitness;
	}

	private double missingNumbersPenalty(int[][][] sudoku, boolean subgridConstraint) {
		int penalty = 0;
		int size = sudoku.length;

		// Row & Column constraint
		for (int i = 0; i < size; i++) {
			boolean[] rowSeen = new boolean[size + 1];
			boolean[] colSeen = new boolean[size + 1];
			for (int j = 0; j < size; j++) {
				int rowVal = sudoku[i][j][0];
				int colVal = sudoku[j][i][0];
				if (rowVal != 0 && rowSeen[rowVal])
					penalty++;
				if (colVal != 0 && colSeen[colVal])
					penalty++;
				rowSeen[rowVal] = true;
				colSeen[colVal] = true;
			}
		}

		// Sub-grid constraint if enabled
		if (subgridConstraint) {
			int sub = (int) Math.sqrt(size);
			for (int boxRow = 0; boxRow < sub; boxRow++) {
				for (int boxCol = 0; boxCol < sub; boxCol++) {
					boolean[] boxSeen = new boolean[size + 1];
					for (int r = 0; r < sub; r++) {
						for (int c = 0; c < sub; c++) {
							int val = sudoku[boxRow * sub + r][boxCol * sub + c][0];
							if (val != 0 && boxSeen[val])
								penalty++;
							boxSeen[val] = true;
						}
					}
				}
			}
		}

		return 1.0 / (1.0 + penalty);
	}

	private double sumProductPenalty(int[][][] sudoku, boolean subgridConstraint) {
		int size = sudoku.length;
		int idealSum = (size * (size + 1)) / 2;
		int idealProduct = 1;
		for (int i = 1; i <= size; i++)
			idealProduct *= i;

		double penalty = 0;

		// Row and column penalties
		for (int i = 0; i < size; i++) {
			int rowSum = 0, colSum = 0;
			int rowProd = 1, colProd = 1;

			for (int j = 0; j < size; j++) {
				int rowVal = sudoku[i][j][0];
				int colVal = sudoku[j][i][0];

				rowSum += rowVal;
				colSum += colVal;

				rowProd *= (rowVal == 0 ? 1 : rowVal);
				colProd *= (colVal == 0 ? 1 : colVal);
			}

			penalty += Math.abs(idealSum - rowSum) + Math.abs(idealSum - colSum);
			penalty += Math.abs(idealProduct - rowProd) + Math.abs(idealProduct - colProd);
		}

		// Subgrid penalty if enabled
		if (subgridConstraint) {
			int sub = (int) Math.sqrt(size);
			for (int boxRow = 0; boxRow < sub; boxRow++) {
				for (int boxCol = 0; boxCol < sub; boxCol++) {
					int gridSum = 0;
					int gridProd = 1;

					for (int r = 0; r < sub; r++) {
						for (int c = 0; c < sub; c++) {
							int val = sudoku[boxRow * sub + r][boxCol * sub + c][0];
							gridSum += val;
							gridProd *= (val == 0 ? 1 : val);
						}
					}

					penalty += Math.abs(idealSum - gridSum) + Math.abs(idealProduct - gridProd);
				}
			}
		}

		return 1.0 / (1.0 + penalty);
	}

	protected int[][][] getSolution() {
		return solution;
	}

	protected void setFitness(double fit) {
		fitness = fit;
	}

	protected double getFitness() {
		return fitness;
	}

	protected int getElement(int j) {
		int row = j / solution.length, column = j % solution.length;
		if (solution[row][column][1] == 0)
			return 0;
		return solution[row][column][0];
	}

	protected int[] neededNumbers(Subgrid grid) {
		int[] needed = new int[solution.length];
		int removed = 0;
		for (int ctr = 1; ctr <= solution.length; ctr++)
			needed[ctr - 1] = ctr;
		for (int y = grid.getStartY(), limY = y + grid.getDimY(); y < limY; y++) {
			for (int x = grid.getStartX(), limX = x + grid.getDimX(); x < limX; x++) {
				if (solution[y][x][1] == 0) {
					needed[solution[y][x][0] - 1] = 0;
					removed = removed + 1;
				}
			}
		}
		int[] neededNum = new int[solution.length - removed];
		for (int ctr = 0, ctr2 = 0; ctr < solution.length; ctr++) {
			if (needed[ctr] > 0) {
				neededNum[ctr2] = needed[ctr];
				ctr2 = ctr2 + 1;
			}
		}
		return neededNum;
	}

	protected int[][][] getCopy() {
		int[][][] copy = new int[solution.length][solution.length][2];
		for (int ctr = 0; ctr < copy.length; ctr++) {
			for (int ct = 0; ct < copy.length; ct++) {
				copy[ctr][ct][0] = solution[ctr][ct][0];
				copy[ct][ctr][0] = solution[ct][ctr][0];
				copy[ctr][ct][1] = solution[ctr][ct][1];
				copy[ct][ctr][1] = solution[ct][ctr][1];
			}
		}
		return copy;
	}

	protected int[][][] swap(int[][][] solution, int subgridNum, int row, int column, int xij, int vij) {
		this.solution = solution;
		for (int y = subgrid[subgridNum].getStartY(), limY = y + subgrid[subgridNum].getDimY(); y < limY; y++) {
			for (int x = subgrid[subgridNum].getStartX(), limX = x + subgrid[subgridNum].getDimX(); x < limX; x++) {
				if (solution[y][x][0] == vij) {
					solution[y][x][0] = xij;
					solution[row][column][0] = vij;
					return solution;
				}
			}
		}
		return null;
	}
}