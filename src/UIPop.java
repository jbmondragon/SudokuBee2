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
		panel.setOpaque(false);

		bg = addLabel(panel, "img/game control/" + size + "x" + size + ".png", 0, 0);
		javax.swing.Icon bgIcon = bg.getIcon();
		int bgW = bgIcon.getIconWidth();
		int bgH = bgIcon.getIconHeight();

		panel.setComponentZOrder(bg, panel.getComponentCount() - 1);

		int[][] positions = {
				{ 100, 180 }, { 182, 180 }, { 255, 180 },
				{ 330, 180 }, { 150, 250 }, { 220, 250 },
				{ 295, 250 }, { 180, 315 }, { 257, 315 }
		};

		btn = new JButton[size];
		for (int i = 0; i < size; i++) {
			int bx = positions[i][0];
			int by = positions[i][1];

			String iconPath = "img/box/" + size + "x" + size + "/normal/" + (i + 1) + ".png";
			java.io.File f = new java.io.File(iconPath);

			if (f.exists() && f.length() > 0) {
				btn[i] = addButton(panel, iconPath, bx, by);
			} else {
				btn[i] = new JButton((i + 1) + "");
				btn[i].setBounds(bx, by, 48, 48);
				btn[i].setOpaque(true);
				btn[i].setContentAreaFilled(true);
				btn[i].setBorderPainted(true);
				panel.add(btn[i]);
			}

			panel.setComponentZOrder(btn[i], 0);
		}

		erase = addButton(panel, "img/box/misc/clear.png", 143, 125);
		cancel = addButton(panel, "img/box/misc/cancel.png", 286, 125);

		panel.setComponentZOrder(erase, 0);
		panel.setComponentZOrder(cancel, 0);

		erase.setOpaque(false);
		erase.setContentAreaFilled(false);
		cancel.setOpaque(false);
		cancel.setContentAreaFilled(false);

		field = addTextField(panel, "", 200, 100, 80, 38);
		field.grabFocus();
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
