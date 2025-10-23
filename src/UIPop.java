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

		// Create buttons dynamically
		btn = new JButton[size];
		int gridCols = (int) Math.ceil(Math.sqrt(size)); // number of columns
		int gridRows = (int) Math.ceil((double) size / gridCols); // number of rows
		String sampleIconPath = "img/box/" + size + "x" + size + "/normal/1.png";
		int btnW = 48, btnH = 48;
		java.io.File sampleFile = new java.io.File(sampleIconPath);
		if (sampleFile.exists()) {
			javax.swing.Icon sampleIcon = new javax.swing.ImageIcon(sampleIconPath);
			btnW = sampleIcon.getIconWidth();
			btnH = sampleIcon.getIconHeight();
		}
		int gap = 8;
		int spacingX = btnW + gap, spacingY = btnH + gap;
		int startX = (bgW - (gridCols * btnW + (gridCols - 1) * gap)) / 2;
		int startY = (bgH - (gridRows * btnH + (gridRows - 1) * gap)) / 2;

		for (int i = 0; i < size; i++) {
			int row = i / gridCols;
			int col = i % gridCols;
			String btnFolder = "img/box/" + size + "x" + size + "/normal/";
			String iconPath = btnFolder + (i + 1) + ".png";
			java.io.File f = new java.io.File(iconPath);
			int bx = startX + col * spacingX;
			int by = startY + row * spacingY;
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
		}

		try {
			for (int i = 0; i < size; i++) {
				if (btn[i] != null)
					panel.setComponentZOrder(btn[i], 0);
			}
			panel.setComponentZOrder(bg, panel.getComponentCount() - 1);
		} catch (Exception e) {
		}

		erase = addButton(panel, "img/box/misc/clear.png", 50, 50);
		cancel = addButton(panel, "img/box/misc/cancel.png", 150, 50);

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
