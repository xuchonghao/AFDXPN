package pipe.samodules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JTextField;

public class FileUtils {

	public static void readfile(final JTextField textField) 
	{
		new Thread(new Runnable() {
			public void run() {
				
				
					// 使用RandomAccessFile , 从后找最后一行数据  
					RandomAccessFile raf;
						while(true)
						{
							try {
								FileReader a  = new FileReader("E:/pipelog.txt");
								BufferedReader bufferReader = new BufferedReader(a);
								raf = new RandomAccessFile("E:/pipelog.txt", "r");
								long len = raf.length();  
								System.out.println("len="+len);
								String lastLine = "";  
								if (len != 0L) {  
								  long pos = len - 1;  
								  while (pos > 0) {   
								    pos--;  
								    raf.seek(pos);  
								    if (raf.readByte() == '\n') {  
								      lastLine = raf.readLine();  
								      break;  
								    }  
								  }  
								}  
								raf.close();  
							//	String line = bufferReader.readLine();
								textField.setText(lastLine); 
							//	System.out.println(lastLine);
								
								Thread.sleep(100);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
	
			}
		}).start();
		
		
	}
	
	public static void writefile(final String param) 
	{
		new Thread(new Runnable() {
			public void run() {
		
					try {
						 FileWriter fileWritter = new FileWriter("E:/pipelog.txt",true);
			             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
						bufferWritter.write(param);
						bufferWritter.write('\n');
						bufferWritter.close();
					//	i++;
						Thread.sleep(1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
			}
		}).start();
	}
}
