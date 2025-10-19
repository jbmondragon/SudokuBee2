import javax.swing.JLabel;
import javax.swing.JPanel;

public class Animation {
	private JPanel pane;
	private JLabel board;
	private int sudokuArray[][][];
	private int size, startX, startY, inc, btnX, btnY, ans;
	protected JLabel btn[][][];
	private generalPanel gp = new generalPanel();

	Animation() {
	}

	Animation(int sudokuArray[][][], JPanel pane) {
		this.pane = pane;
		this.sudokuArray = sudokuArray;
		pane.setVisible(true);
		setConstants();
	}

	private void setConstants() {
		size = sudokuArray.length;
		int cellSize; // Size of each cell image
		int thickGap;  // Extra pixels to add after each subgrid separator
		int subSize = (int) Math.sqrt(size);

		// Adjust startX and inc based on board size for better alignment
		if (size == 9) {
			startX = 7;
			inc = 56;
			cellSize = 53;
			thickGap = 0;
		} else if (size == 16) {
			startX = 7;
			inc = 31;
			cellSize = 29;
			thickGap = 3;
		} else { // 25x25
			startX = 7;
			inc = 20;
			cellSize = 18;
			thickGap = 2;
		}
		startY = 86;
		btn = new JLabel[size][size][size + 1];
		for (int row = 0; row < size; row++) {
			int posY = startY + row * inc + (row / subSize) * thickGap;
			for (int col = 0; col < size; col++) {
				int posX = startX + col * inc + (col / subSize) * thickGap;
				String img = "normal";
				if (sudokuArray[row][col][1] == 0)
					img = "given";
				for (int counter = 0; counter < size + 1; counter++)
					btn[row][col][counter] = gp.addInvisibleLabel(pane,
							"img/box/" + size + "x" + size + "/" + img + "/" + counter + ".png", posX, posY, cellSize, cellSize);
				if (img.equals("given"))
					btn[row][col][sudokuArray[row][col][0]].setVisible(true);
			}
		}
		board = gp.addLabel(pane, "img/board/" + size + "x" + size + ".png", 0, 84);
	}

	protected int[][][] getSudokuArray() {
		return sudokuArray;
	}

	protected void changePic(int solution[][][]) {
		for (int row = 0, row2 = size - 1; row < size; row++, row2--) {
			for (int col = row, col2 = size - 1; col < size; col++, col2--) {
				if (sudokuArray[row][col][1] == 1) {
					btn[row][col][sudokuArray[row][col][0]].setVisible(false);
					btn[row][col][solution[row][col][0]].setVisible(true);
				}
				if (sudokuArray[col][row][1] == 1) {
					btn[col][row][sudokuArray[col][row][0]].setVisible(false);
					btn[col][row][solution[col][row][0]].setVisible(true);
				}
				if (sudokuArray[row2][col2][1] == 1) {
					btn[row2][col2][sudokuArray[row2][col2][0]].setVisible(false);
					btn[row2][col2][solution[row2][col2][0]].setVisible(true);
				}
				if (sudokuArray[col2][row2][1] == 1) {
					btn[col2][row2][sudokuArray[col2][row2][0]].setVisible(false);
					btn[col2][row2][solution[col2][row2][0]].setVisible(true);
				}
			}
		}
		sudokuArray = solution;
	}

	protected void setSudoku(int solution[][][]) {
		sudokuArray = solution;
	}

	protected void decompose() {
		pane.removeAll();
		board = null;
		sudokuArray = null;
		btn = null;
		gp = null;
		pane.setVisible(false);
	}
}