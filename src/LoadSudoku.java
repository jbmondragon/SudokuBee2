import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadSudoku {
	private int[][][] sudokuArray;
	private int[][][] original;
	private int size;
	private boolean valid = false;

	public LoadSudoku(String path) {
		BufferedReader buff = null;

		try {
			buff = new BufferedReader(new FileReader(path));

			// Read all lines, skipping empty ones
			List<String> lines = new ArrayList<>();
			String line;
			while ((line = buff.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty())
					lines.add(line);
			}

			if (lines.isEmpty()) {
				System.err.println("❌ Empty .sav file: " + path);
				valid = false;
				return;
			}

			// --- First line: puzzle size ---
			size = Integer.parseInt(lines.get(0).trim());
			sudokuArray = new int[size][size][2];
			original = new int[size][size][2];

			// --- Read grid data ---
			int expectedLines = size * 2;
			int availableLines = lines.size() - 1;

			if (availableLines < size) {
				System.err.println("⚠️ File has too few data lines: " + availableLines);
				valid = false;
				return;
			}

			boolean hasFlags = (availableLines >= expectedLines);

			int lineIndex = 1;
			for (int i = 0; i < size; i++) {
				// --- Values line ---
				String[] vals = lines.get(lineIndex++).trim().split("\\s+");
				if (vals.length < size) {
					System.err.println("⚠️ Missing numbers in row " + i);
					valid = false;
					return;
				}

				for (int j = 0; j < size; j++) {
					sudokuArray[i][j][0] = Integer.parseInt(vals[j]);
				}

				// --- Flags line (optional) ---
				if (hasFlags) {
					String[] flags = lines.get(lineIndex++).trim().split("\\s+");
					for (int j = 0; j < size; j++) {
						sudokuArray[i][j][1] = Integer.parseInt(flags[j]);
					}
				} else {
					// If flags are missing, mark nonzero values as given
					for (int j = 0; j < size; j++) {
						sudokuArray[i][j][1] = (sudokuArray[i][j][0] != 0) ? 1 : 0;
					}
				}
			}

			// --- Build original array ---
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (sudokuArray[i][j][1] == 1) {
						original[i][j][0] = 0;
						original[i][j][1] = 1;
					} else {
						original[i][j][0] = sudokuArray[i][j][0];
						original[i][j][1] = 0;
					}
				}
			}

			// ✅ Validation phase
			Subgrid[] subgrid = new Subgrid[size];
			int subDimY = (int) Math.sqrt(size);
			int subDimX = size / subDimY;
			for (int ctr = 0, xCount = 0; ctr < size; ctr++, xCount++) {
				subgrid[ctr] = new Subgrid(xCount * subDimX, ((ctr / subDimY) * subDimY), subDimX, subDimY);
				if ((ctr + 1) % subDimY == 0 && ctr > 0)
					xCount = -1;
			}

			Validator val = new Validator(original, subgrid);
			valid = val.checkValidity();
			if (!valid)
				System.err.println("❌ Invalid Sudoku structure in: " + path);

		} catch (FileNotFoundException e) {
			System.err.println("❌ File not found: " + path);
			valid = false;
		} catch (Exception e) {
			System.err.println("❌ Error reading " + path + ": " + e.getMessage());
			valid = false;
		} finally {
			try {
				if (buff != null)
					buff.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public boolean getStatus() {
		return valid;
	}

	public int[][][] getArray() {
		return sudokuArray;
	}

	public int getSize() {
		return size;
	}
}
