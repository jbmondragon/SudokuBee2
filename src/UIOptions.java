import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UIOptions extends generalPanel {
	private JPanel panel[];
	private JLabel bg;

	private String size[] = {
			"img/exit/size/9x9.png",
			"img/exit/size/16x16.png",
			"img/exit/size/25x25.png"
	};
	private String sound[] = {
			"img/exit/sound/on.png",
			"img/exit/sound/off.png"
	};
	private String penalty[] = {
			"img/exit/penalty/missing.png",
			"img/exit/penalty/sum_in.png",
			"img/exit/penalty/sum_out.png"
	};

	protected JLabel sizeLabel, levelLabel, soundLabel, penaltyLabel;
	protected JButton exit, no;
	protected JButton left[] = new JButton[3];
	protected JButton right[] = new JButton[3];

	protected int sz, lvl, snd, pty, num;

	protected int[] boardSizes = { 9, 16, 25 };

	UIOptions(JPanel panel[]) {
		this.panel = panel;
		panel[1].setOpaque(true);

		exit = addButton(panel[1], "img/exit/okay.png", "img/exit/h_okay.png", 360, 401);

		int[] labelY = { 203, 266, 333 }; // label Y positions

		for (int ctr = 0; ctr < 3; ctr++) {
			left[ctr] = addButton(panel[1],
					"img/exit/left.png", "img/exit/h_left.png",
					354, labelY[ctr]); // was labelY[ctr] + 4 → now labelY[ctr]

			right[ctr] = addButton(panel[1],
					"img/exit/h_right.png", "img/exit/h_right.png",
					566, labelY[ctr]);
		}

		// default selections
		sz = 0;
		num = lvl = snd = pty = 0;

		sizeLabel = addLabel(panel[1], size[sz], 389, 202);
		soundLabel = addLabel(panel[1], sound[snd], 389, 265);
		penaltyLabel = addLabel(panel[1], penalty[pty], 389, 332);

		bg = addLabel(panel[1], "img/bg/options.png", 100, 99);
	}

	// Existing methods
	public int getBoardSize() {
		return boardSizes[sz];
	}

	protected void setSize(boolean isRight) {
		if (isRight)
			sz = (sz + 1) % boardSizes.length;
		else
			sz = (sz - 1 + boardSizes.length) % boardSizes.length;
		changePicture(sizeLabel, size[sz]);
	}

	protected void setSound(boolean isRight) {
		if (isRight)
			snd = (snd + 1) % 2;
		else
			snd = (snd - 1 + 2) % 2;
		changePicture(soundLabel, sound[snd]);
	}

	// NEW: Penalty function cycling
	protected void setPenalty(boolean isRight) {
		if (isRight)
			pty = (pty + 1) % penalty.length;
		else
			pty = (pty - 1 + penalty.length) % penalty.length;
		changePicture(penaltyLabel, penalty[pty]);
	}

	public int getPenaltyType() {
		return pty; // 0 = Missing, 1 = Sum-Product (with), 2 = Sum-Product (no)
	}

	protected void setVisible(boolean isVisible, int num) {
		this.num = num;
		panel[1].setVisible(isVisible);
	}

	protected void decompose() {
		panel[1].removeAll();
		bg = sizeLabel = levelLabel = soundLabel = penaltyLabel = null;
		exit = no = null;
		for (int i = 0; i < 3; i++) {
			left[i] = right[i] = null;
		}
	}
}
