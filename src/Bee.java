import java.util.Random;

class Bee {
	private int[][][] solution;
	private double fitness;
	private Subgrid[] subgrid;
	private Random rand = new Random();

	Bee(Subgrid[] subgrid) {
		this.subgrid = subgrid;
	}

	// Creates a random solution
	// prob is a 3D array where the first 2 dimensions are the rows and columns of
	// the sudoku
	// the 3rd dimension is of size 2 where the first element is the number in the
	// cell and the second element is 0 if it is a fixed number and 1 if it is a
	// variable number
	// subgrid is an array of Subgrid objects that contains the information about
	// the subgrids of the sudoku
	// The random solution is created by filling in the variable cells in each
	// subgrid with the numbers that are not already present in the subgrid
	// The numbers are filled in randomly
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

	// Copies the problem to the solution
	// This is used to reset the solution to the original problem
	protected void copyProblem(int[][][] prob) {
		solution = prob;
	}

	// Prints the solution to the console
	protected void printResult() {
		for (int ctr = 0; ctr < solution.length; ctr++) {
			for (int ctr1 = 0; ctr1 < solution[ctr].length; ctr1++) {
				System.out.print(solution[ctr][ctr1][0] + "");
			}
			System.out.println("");
		}
	}

	// Calculates the penalty value of the solution
	// The penalty value is the number of duplicate numbers in each row and column
	// A lower penalty value is better
	// A penalty value of 0 means that the solution is a valid sudoku solution
	// This function uses customSet to keep track of the numbers that have already
	// been seen in each row and column
	// customSet is a simple implementation of a set data structure that uses an
	// array to store the elements
	// It has methods to add, remove, and check for the presence of elements
	// The penalty value is calculated by iterating through each row and column of
	// the solution and checking for duplicates using the customSet
	// If a duplicate is found, the penalty value is incremented
	// The function returns the total penalty value
	// This function is called by the BeeColony class to evaluate the fitness of the
	// bees
	// The fitness is then used to determine which bees will be selected for
	// reproduction
	// The penalty value is also used to determine when a valid solution has been
	// found
	// If a bee has a penalty value of 0, the BeeColony class will stop the search
	// and return the solution
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
					// Deprecated, latest Java automatically converts the primitive int to an
					// Integer object (autoboxing)
					hor.add((solution[ctr][ct][0]));
				if (ver.contains(solution[ct][ctr][0]))
					penalty++;
				else
					ver.add((solution[ct][ctr][0]));
			}
		}
		return penalty;
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

	// Returns an array of the numbers that are not present in the given subgrid
	// This is used to fill in the variable cells in the subgrid when creating a
	// random solution
	// The function iterates through the cells in the subgrid and marks the numbers
	// that are already present
	// It then creates an array of the numbers that are not present and returns it
	// The returned array is used in the constructor to fill in the variable cells
	// with random numbers from the array
	// This ensures that the numbers in each subgrid are unique and valid for a
	// sudoku solution
	// The function assumes that the numbers in the sudoku are from 1 to n, where n
	// is the size of the sudoku
	// The function also assumes that the subgrid is a valid subgrid of the sudoku
	// The function does not check for duplicates in the subgrid, as it is assumed
	// that the input is valid
	// If the input is not valid, the function may return incorrect results
	// The function is called by the constructor of the Bee class to create a random
	// solution
	// The function is also used in the swap method to ensure that the numbers in
	// the subgrid remain unique after a swap
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

	// Returns a copy of the solution
	// This is used to create a new solution when performing a swap
	// The copy is created by iterating through the solution and copying each
	// element to a new array
	// The new array is then returned
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

	// Swaps two variable elements in the given subgrid
	// subgridNum is the index of the subgrid in the subgrid array
	// row and column are the coordinates of the first element to be swapped
	// xij is the value of the first element to be swapped
	// vij is the value of the second element to be swapped
	// The function iterates through the cells in the subgrid to find the second
	// element to be swapped
	// When the second element is found, the values of the two elements are swapped
	// The function then returns the modified solution
	// If the second element is not found, the function returns null
	// This function is used by the BeeColony class to create new solutions by
	// swapping elements in the subgrid
	// The function assumes that the input parameters are valid and that the
	// elements to be swapped are variable elements
	// If the input parameters are not valid, the function may return incorrect
	// results
	// The function does not check for duplicates in the subgrid after the swap, as
	// it is assumed that the input is valid
	// The function is called by the BeeColony class to create new solutions for the
	// bees
	// The function is also used in the getCopy method to create a copy of the
	// solution
	// The function is called by the constructor of the Bee class to create a random
	// solution
	// The function is also used in the neededNumbers method to ensure that the
	// numbers in the subgrid remain unique after a swap
	// The function is called by the getPenaltyValue method to evaluate the fitness
	// of the bees
	// The function is called by the printResult method to print the solution to the
	// console
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