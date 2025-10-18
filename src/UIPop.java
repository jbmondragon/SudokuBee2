import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UIPop extends generalPanel {
	private JPanel pane;
	private JPanel panel;
	private JLabel bg;
	protected int size, btnX, btnY;
	protected JButton cancel, erase;
	protected JButton btn[];
	protected JTextField field;

	UIPop(int size, JPanel pane) {
		this.pane = pane;
		this.size = size;
		panel = addPanel(pane, 5, 84, 500, 500);
		panel.setOpaque(true);

		// Create buttons dynamically
		btn = new JButton[size];
		int gridCols = (int) Math.ceil(Math.sqrt(size)); // number of columns
		int gridRows = (int) Math.ceil((double) size / gridCols); // number of rows
		int startX = 50, startY = 150; // base coordinates
		int spacingX = 60, spacingY = 60; // button spacing

		for (int i = 0; i < size; i++) {
			int row = i / gridCols;
			int col = i % gridCols;
			btn[i] = addButton(panel,
					"img/box/12x12/normal/" + (i + 1) + ".png",
					startX + col * spacingX,
					startY + row * spacingY);
		}

		erase = addButton(panel, "img/box/misc/clear.png", 50, 50);
		cancel = addButton(panel, "img/box/misc/cancel.png", 150, 50);

		field = addTextField(panel, "", 200, 100, 80, 38);
		field.grabFocus();

		bg = addLabel(panel, "img/game control/" + size + "x" + size + ".png", 0, 0);
	}

	protected void setVisible(boolean isVisible, int btnX, int btnY, int num) {
		pane.setVisible(isVisible);
		if (num == 0)
			field.setText("");
		else
			field.setText(num + "");
		field.grabFocus();
		this.btnX = btnX;
		this.btnY = btnY;
	}

	protected void decompose() {
		pane.removeAll();
		panel = null;
		bg = null;
		cancel = erase = null;
		for (int i = 0; i < size; i++)
			btn[i] = null;
	}
}
