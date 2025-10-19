import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class UIBoard{
	private JPanel pane;
	private JLabel board;
	private int sudokuArray[][][];
	private int size, startX, startY, inc, btnX, btnY, ans;
	protected JButton btn[][];
	private generalPanel gp=new generalPanel();
	UIBoard(){}
	UIBoard(int sudokuArray[][][], JPanel pane){
		this.pane=pane;
		this.sudokuArray=sudokuArray;
		setConstants(false);
		}
	UIBoard(int sudokuArray[][][],boolean isNull, JPanel pane){
		this.pane=pane;
		this.sudokuArray=sudokuArray;
		ans=0;
		if(isNull)
			fill();
		setConstants(true);
		}
	private void fill(){
		size=sudokuArray.length;
		for(int ctr=0; ctr<size;ctr++){
			for(int count=0; count<size;count++){
				sudokuArray[ctr][count][0]=0;
				sudokuArray[ctr][count][1]=1;
				}
			}
		}
	private void setConstants(boolean setCursor){
		size = sudokuArray.length;
		int cellSize; // Size of each cell image
		int thickGap;  // Extra pixels to add after each subgrid separator
		int subSize = (int) Math.sqrt(size);
		// Base alignment and spacing per board size
		if (size == 9) {
			startX = 7;
			inc = 56;
			cellSize = 53;
			thickGap = 0; 
		} else if (size == 16) {
			startX = 7;
			inc = 31  ;
			cellSize = 29;
			thickGap = 3; 
		} else { // 25x25
			startX = 7;
			inc = 20;
			cellSize = 18;
			thickGap = 2; 
		}
		startY = 86;
		btn = new JButton[size][size];
		for (int row = 0; row < size; row++) {
			int posY = startY + row * inc + (row / subSize) * thickGap;
			for (int col = 0; col < size; col++) {
				int posX = startX + col * inc + (col / subSize) * thickGap;
				String img = "normal";
				if (sudokuArray[row][col][1] == 0)
					img = "given";
				btn[row][col] = gp.gameButton(pane,
						"img/box/" + size + "x" + size + "/" + img + "/" + sudokuArray[row][col][0] + ".png",
						posX, posY, cellSize, cellSize);
				if (setCursor && img.equals("normal"))
					btn[row][col].setCursor(new Cursor(12));
				else
					btn[row][col].setCursor(new Cursor(0));
				if (sudokuArray[row][col][0] != 0)
					ans++;
			}
		}
		board = gp.addLabel(pane, "img/board/" + size + "x" + size + ".png", 0, 84);
		}
	protected JButton getButton(){
		return btn[btnX][btnY];
		}
	protected int getStatus(int x, int y){
		return sudokuArray[x][y][1];
		}
	protected int[][][] getSudokuArray(){
		return sudokuArray;
		}
	protected void changeCursor(){
		for(int row=0; row<size; row++){
			for(int col=0; col<size; col++){
				btn[row][col].setCursor(new Cursor(0));
				btn[col][row].setCursor(new Cursor(0));
				}
			}
		}
	protected void changePic(){
		for(int row=0; row<size; row++){
			for(int col=row; col<size; col++){
				if(sudokuArray[row][col][1]==1)
					sudokuArray[row][col][0]=0;
				if(sudokuArray[col][row][1]==1)
					sudokuArray[col][row][0]=0;
				}
			}
		}
	protected void setSudoku(int solution[][][]){
		sudokuArray=solution;
		}
	protected void setSudokuArray(int value, boolean isAns, int x, int y){
		if(sudokuArray[x][y][0]==0 && value!=0)
			ans++;
		if(sudokuArray[x][y][0]!=0 && value==0)
			ans--;
		sudokuArray[x][y][0]=value;
		int num=1;
		if(!isAns && value!=0)
			num=0;
		sudokuArray[x][y][1]=num;
		sudokuArray[x][y][0]=value;
		}
	protected int getValue(int x, int y){
		return sudokuArray[x][y][0];
		}
	protected int getSize(){
		return size;
		}
	protected int getAns(){
		return ans;
		}
	protected void decompose(){
		pane.removeAll();
		board=null;
		sudokuArray=null;
		btn=null;
		gp=null;
		}
	}