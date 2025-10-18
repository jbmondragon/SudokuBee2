import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UIOptions extends generalPanel {
	private JPanel panel[];
	private JLabel bg;

	// Image paths for board size options
	private String size[] = { "img\\exit\\size\\9x9.png",
			"img\\exit\\size\\16x16.png",
			"img\\exit\\size\\25x25.png" };
	private String sound[] = { "img\\exit\\sound\\on.png", "img\\exit\\sound\\off.png" };

	protected JLabel sizeLabel, levelLabel, soundLabel;
	protected JButton exit, no;
	protected JButton left[] = new JButton[2];
	protected JButton right[] = new JButton[2];

	protected int sz, lvl, snd, num;

	// Valid board sizes
	protected int[] boardSizes = { 9, 16, 25 };

	UIOptions(JPanel panel[]) {
		this.panel = panel;
		panel[1].setOpaque(true);

		exit = addButton(panel[1], "img/exit/okay.png", "img/exit/h_okay.png", 385, 401);

		for (int ctr = 0; ctr < 2; ctr++) {
			left[ctr] = addButton(panel[1], "img/exit/left.png", "img/exit/h_left.png", 356, 235 + 70 * ctr);
			right[ctr] = addButton(panel[1], "img/exit/h_right.png", "img/exit/h_right.png", 568, 235 + 70 * ctr);
		}

		// Default selected size is 9x9 (index 0)
		sz = 0;
		num = lvl = snd = 0;

		sizeLabel = addLabel(panel[1], size[sz], 389, 237);
		soundLabel = addLabel(panel[1], sound[snd], 389, 308);
		bg = addLabel(panel[1], "img/bg/options.png", 100, 99);
	}

	// Get the actual board size (9, 16, or 25)
	public int getBoardSize() {
		return boardSizes[sz];
	}

	// Cycle the board size left or right
	protected void setSize(boolean isRight) {
		if (isRight) {
			sz = (sz + 1) % boardSizes.length; // wrap around
		} else {
			sz = (sz - 1 + boardSizes.length) % boardSizes.length; // wrap around
		}
		changePicture(sizeLabel, size[sz]);
	}

	// Toggle sound on/off
	protected void setSound(boolean isRight) {
		if (isRight) {
			snd = (snd + 1) % 2;
		} else {
			snd = (snd - 1 + 2) % 2;
		}
		changePicture(soundLabel, sound[snd]);
	}

	protected void setVisible(boolean isVisible, int num) {
		this.num = num;
		panel[1].setVisible(isVisible);
	}

	protected void decompose() {
		panel[1].removeAll();
		bg = sizeLabel = levelLabel = soundLabel = null;
		exit = no = null;
		left[0] = right[0] = left[1] = right[1] = null;
	}
}
