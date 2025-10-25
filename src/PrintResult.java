import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;

/**
 * PrintResult
 * - Can display Sudoku puzzles in the UI
 * - Can also write logs or results to file
 */
public class PrintResult {

	// For logging to file
	private FileWriter writer;
	private PrintWriter print;
	private SimpleDateFormat dateFormat;
	private String filename;

	// For GUI display
	private JPanel panel;
	private int size;
	private JLabel[][] cells;

	// ---------- FILE LOGGING CONSTRUCTOR ----------
	public PrintResult(String filename) {
		this.filename = filename;
		try {
			java.io.File file = new java.io.File(filename);
			java.io.File parentDir = file.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}
			writer = new FileWriter(file);
			print = new PrintWriter(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dateFormat = new SimpleDateFormat("HHmmssSSS");
	}

	// ---------- GUI DISPLAY CONSTRUCTOR ----------
	public PrintResult(int size) {
		this.size = size;
		this.panel = new JPanel(new GridLayout(size, size, 2, 2));
		this.panel.setBackground(Color.BLACK);
		this.cells = new JLabel[size][size];

		Font cellFont = new Font("Arial", Font.BOLD, 20);

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				JLabel cell = new JLabel("", SwingConstants.CENTER);
				cell.setOpaque(true);
				cell.setBackground(Color.WHITE);
				cell.setFont(cellFont);
				cell.setPreferredSize(new Dimension(50, 50));
				cell.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				this.panel.add(cell);
				this.cells[r][c] = cell;
			}
		}
	}

	// ---------- FILE METHODS ----------
	protected void print(Object text) {
		if (print != null) {
			print.print(text + "\n");
		}
	}

	protected void close() {
		if (print != null) {
			print.close();
		}
	}

	protected void delete() {
		if (filename != null) {
			new java.io.File(filename).delete();
		}
	}

	protected double getTime() {
		return (Double.parseDouble(dateFormat.format(Calendar.getInstance().getTime())));
	}

	// ---------- GUI METHODS ----------
	public JPanel getPanel() {
		return panel;
	}

	public void printSudoku(int[][][] sudokuArray) {
		if (sudokuArray == null || sudokuArray.length != size)
			return;

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				int num = sudokuArray[r][c][0];
				int type = sudokuArray[r][c][1];
				JLabel cell = cells[r][c];

				if (num == 0)
					cell.setText("");
				else
					cell.setText(String.valueOf(num));

				// Color based on fixed/user-entered
				if (type == 1)
					cell.setBackground(new Color(220, 220, 220)); // fixed cell
				else
					cell.setBackground(Color.WHITE);
			}
		}
	}
}
