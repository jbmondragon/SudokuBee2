import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class ABC extends Thread {
	private int[][][] problem;
	private int[][] emptyCell;
	private int maxCycle, cycle;
	private int employedSize, numCell, onlookerSize, scoutSize, maxEmptyCell, fitestBee = -1;
	private double maxFit = 0;
	private Bee[] bee;
	private Bee bestBee;
	private Subgrid[] subgrid;
	private Random rand = ThreadLocalRandom.current();
	private String information = "";
	private GreedySelection greedy = new GreedySelection();
	private PrintResult printer;
	private boolean shouldStop = false;

	ABC(PrintResult printer, int[][][] problem, int employedSize, int onlookerSize, int maxCycle) {
		this.problem = problem;
		this.maxCycle = maxCycle;
		this.employedSize = employedSize;
		this.onlookerSize = onlookerSize;
		this.printer = printer;
		numCell = problem.length * problem.length;
		
		// Adaptive scout size based on problem size
		int size = problem.length;
		if (size <= 9) {
			scoutSize = (int) (0.1 * employedSize);
		} else if (size <= 16) {
			scoutSize = (int) (0.05 * employedSize);
		} else {
			scoutSize = (int) (0.02 * employedSize); // Smaller scout size for large grids
		}
		scoutSize = Math.max(1, scoutSize); // Ensure at least 1 scout
		
		initialization();
	}

	public void run() {
		Bee v;
		double sumFitness = 0, beeFitness = 0;

		// Early termination if problem is too large and fitness is not improving
		int noImprovementCount = 0;
		double lastBestFitness = 0;

		for (cycle = 0; cycle < maxCycle && maxFit != 1 && !shouldStop; cycle++) {
			sumFitness = 0;

			// employed bee phase - parallelizable but keeping single-threaded for simplicity
			for (int i = 0; i < bee.length && maxFit != 1 && !shouldStop; i++) {
				v = neighborhoodSearch(i);
				bee[i] = greedy.greedySearch(bee[i], v);
				beeFitness = bee[i].getFitness();
				maxFit = getMaxFit(maxFit, beeFitness, i);
				sumFitness = sumFitness + beeFitness;
			}

			// Check for early termination
			if (Math.abs(maxFit - lastBestFitness) < 0.001) {
				noImprovementCount++;
			} else {
				noImprovementCount = 0;
				lastBestFitness = maxFit;
			}
			
			// Early termination for large grids if no improvement
			if (problem.length >= 16 && noImprovementCount > 100) {
				System.out.println("Early termination: No improvement for 100 cycles");
				break;
			}

			// onlooker bee phase - limit for large grids
			int maxOnlookerIterations = (problem.length >= 16) ? bee.length / 2 : bee.length;
			for (int i = 0; i < maxOnlookerIterations && maxFit != 1 && !shouldStop; i++) {
				if (sumFitness == 0) break; // Prevent division by zero
				double probability = bee[i].getFitness() / sumFitness;
				int maxOnlooker = (int) (probability * onlookerSize);
				maxOnlooker = Math.min(maxOnlooker, 5); // Limit onlookers per employed bee
				
				for (int count = 0; count < maxOnlooker; count++) {
					v = neighborhoodSearch(i);
					bee[i] = greedy.greedySearch(bee[i], v);
					maxFit = getMaxFit(maxFit, bee[i].getFitness(), i);
				}
			}

			// Scout phase - only if needed and less frequent for large grids
			if (scoutSize > 0 && maxFit != 1 && !shouldStop) {
				// Only scout every 10 cycles for large grids to save computation
				if (problem.length < 16 || cycle % 10 == 0) {
					performScouting();
				}
			}
			
			// Print progress less frequently for large grids
			if (problem.length < 16 || cycle % 100 == 0) {
				printer.print((cycle + 1) + "\t" + bestBee.getFitness());
			}
			
			// Adaptive termination for very large grids
			if (problem.length >= 25 && cycle > 1000 && maxFit > 0.8) {
				System.out.println("Early termination for 25x25: Good enough solution found");
				break;
			}
		}
		printer.print((cycle) + "\t" + bestBee.getFitness());
	}

	private void performScouting() {
		double maxMin = 1;
		int minSet[] = new int[scoutSize];
		for (int i = 0; i < scoutSize; i++) {
			minSet[i] = i;
			if (maxMin > bee[i].getFitness())
				maxMin = bee[i].getFitness();
		}
		
		// Find worst bees more efficiently
		for (int i = scoutSize; i < bee.length; i++) {
			if (bee[i].getFitness() <= maxMin) {
				// Replace the worst in minSet
				int worstIndex = 0;
				double worstFitness = bee[minSet[0]].getFitness();
				for (int ctr = 1; ctr < scoutSize; ctr++) {
					if (bee[minSet[ctr]].getFitness() > worstFitness) {
						worstFitness = bee[minSet[ctr]].getFitness();
						worstIndex = ctr;
					}
				}
				if (bee[i].getFitness() < worstFitness) {
					minSet[worstIndex] = i;
					// Update maxMin
					maxMin = bee[minSet[0]].getFitness();
					for (int ctr = 1; ctr < scoutSize; ctr++) {
						if (bee[minSet[ctr]].getFitness() < maxMin) {
							maxMin = bee[minSet[ctr]].getFitness();
						}
					}
				}
			}
		}
		
		for (int i = 0; i < scoutSize && maxFit != 1 && !shouldStop; i++) {
			Bee v = new Bee(getProblemCopy(), subgrid);
			bee[minSet[i]] = greedy.greedySearch(bee[minSet[i]], v);
			maxFit = getMaxFit(maxFit, bee[minSet[i]].getFitness(), minSet[i]);
		}
	}

	protected boolean isDone() {
		return cycle >= maxCycle || maxFit == 1 || shouldStop;
	}

	public void stopExecution() {
		shouldStop = true;
		this.interrupt();
	}

	private void initialization() {
		// Creating subgrids
		subgrid = new Subgrid[problem.length];
		int subDimY = (int) Math.sqrt(problem.length);
		int subDimX = problem.length / subDimY;
		for (int ctr = 0, xCount = 0; ctr < problem.length; ctr++, xCount++) {
			subgrid[ctr] = new Subgrid(xCount * subDimX, ((ctr / subDimY) * subDimY), subDimX, subDimY);
			if ((ctr + 1) % subDimY == 0 && ctr > 0)
				xCount = -1;
		}

		// Adaptive population size based on problem complexity
		int adaptiveEmployedSize = employedSize;
		if (problem.length >= 25) {
			adaptiveEmployedSize = Math.min(employedSize, 50); // Smaller population for large grids
		}
		
		// Initialization of population
		bee = new Bee[adaptiveEmployedSize];
		bestBee = new Bee(subgrid);
		
		int emptyCells = 0;
		for (int ctr = 0; ctr < problem.length; ctr++) {
			for (int count = 0; count < problem.length; count++) {
				if (problem[ctr][count][1] == 1) {
					emptyCells++;
				}
			}
		}
		
		System.out.println("Initializing " + adaptiveEmployedSize + " bees for " + problem.length + "x" + problem.length + " grid with " + emptyCells + " empty cells");

		for (int ctr = 0; ctr < adaptiveEmployedSize; ctr++) {
			bee[ctr] = new Bee(getProblemCopy(), subgrid);
			double fitnessValue = bee[ctr].evaluate(bee[ctr].getSolution());
			bee[ctr].setFitness(fitnessValue);

			if (ctr == 0 || fitnessValue > bestBee.getFitness()) {
				bestBee.copyProblem(bee[ctr].getCopy());
				bestBee.setFitness(fitnessValue);
				maxFit = fitnessValue;
			}
		}

		// Initialize empty cells array
		emptyCell = new int[numCell][3];
		maxEmptyCell = 0;
		for (int ctr = 0; ctr < problem.length; ctr++) {
			for (int count = 0; count < problem.length; count++) {
				if (problem[ctr][count][1] == 1) {
					emptyCell[maxEmptyCell][0] = ctr;
					emptyCell[maxEmptyCell][1] = count;
					for (int ctr2 = 0; ctr2 < subgrid.length; ctr2++) {
						if (subgrid[ctr2].isBelong(emptyCell[maxEmptyCell][1], emptyCell[maxEmptyCell][0])) {
							emptyCell[maxEmptyCell][2] = ctr2;
							break;
						}
					}
					maxEmptyCell++;
				}
			}
		}
		
		for (int ctr = 0; ctr < problem.length; ctr++)
			subgrid[ctr].setNeededNum(bestBee.neededNumbers(subgrid[ctr]));
	}

	protected int[][][] getBestSolution() {
		return bestBee.getSolution();
	}

	protected String getInfo() {
		return bestBee.getFitness() + " " + cycle + " ";
	}

	protected String getCycle() {
		return "cycles:\t" + cycle;
	}

	protected String getCycles() {
		return cycle + "";
	}

	protected double getFitness() {
		return bestBee.getFitness();
	}

	protected int[][][] getProblemCopy() {
		int[][][] copy = new int[problem.length][problem.length][2];
		for (int ctr = 0; ctr < copy.length; ctr++) {
			for (int ct = 0; ct < copy.length; ct++) {
				copy[ctr][ct][0] = problem[ctr][ct][0];
				copy[ctr][ct][1] = problem[ctr][ct][1];
			}
		}
		return copy;
	}

	private double getMaxFit(double maxFit, double beeFitness, int i) {
		if (maxFit <= beeFitness) {
			maxFit = beeFitness;
			bestBee.copyProblem(bee[i].getCopy());
			bestBee.setFitness(beeFitness);
		}
		return maxFit;
	}

	private Bee neighborhoodSearch(int i) {
		if (maxEmptyCell == 0) return bee[i];
		
		int j = rand.nextInt(maxEmptyCell);
		int k = rand.nextInt(bee.length);
		while (k == i && bee.length > 1)
			k = rand.nextInt(bee.length);

		int xij = bee[i].getSolution()[emptyCell[j][0]][emptyCell[j][1]][0];
		int xkj = bee[k].getSolution()[emptyCell[j][0]][emptyCell[j][1]][0];
		
		int[] neededNum = subgrid[emptyCell[j][2]].getNeededNum();
		if (neededNum.length == 0) return bee[i];
		
		// Improved neighborhood search with bounds checking
		int index = (int) (Math.abs(xij + rand.nextDouble() * (xij - xkj)) % neededNum.length);
		int vij = neededNum[Math.max(0, Math.min(index, neededNum.length - 1))];
		
		Bee newBee = new Bee(subgrid);
		newBee.swap(bee[i].getCopy(), emptyCell[j][2], emptyCell[j][0], emptyCell[j][1], xij, vij);
		return newBee;
	}

	protected void decompose() {
		for (int ctr = 0; ctr < bee.length; ctr++)
			bee[ctr] = null;
		bestBee = null;
	}

	public int getCurrentCycle() {
		return cycle;
	}
}
