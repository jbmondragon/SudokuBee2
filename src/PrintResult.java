import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class PrintResult{
	private FileWriter writer;
	private PrintWriter print;
	private SimpleDateFormat dateFormat;
	private String filename;
	PrintResult(String filename){
		this.filename=filename;
		try{
			java.io.File file = new java.io.File(filename);
			java.io.File parentDir = file.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}
			writer=new FileWriter(file);
			print= new PrintWriter(writer);
			}
		catch(Exception e){
			e.printStackTrace();
			}
		dateFormat=new SimpleDateFormat("HHmmssSSS");
		}
	protected void print(Object text){
		print.print(text+"\n");
		}
	protected void close(){
		print.close();
		}
	protected void delete(){
		new java.io.File(filename).delete();
		}
	protected double getTime(){
		return (Double.parseDouble(dateFormat.format(Calendar.getInstance().getTime())));
		}
	}
