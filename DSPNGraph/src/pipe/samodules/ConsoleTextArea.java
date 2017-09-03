package pipe.samodules;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

//利用ByteArrayOutputStream和管道流更改输出到GUI
public class ConsoleTextArea extends JTextField {
	
	private PrintStream ps;
	public final LoopedStreams ls;
	
	public ConsoleTextArea(InputStream[] inStreams) {
		ls = null;
		for(int i = 0; i < inStreams.length; ++i)
			startConsoleReaderThread(inStreams[i]);
	} // ConsoleTextArea()

	public ConsoleTextArea() throws IOException {
		//final LoopedStreams ls = new LoopedStreams();
		ls = new LoopedStreams();
		// 重定向System.out和System.err
		ps = new PrintStream(ls.getOutputStream());
//		System.setOut(ps);
//		System.setErr(ps);
		
		startConsoleReaderThread(ls.getInputStream());
	} // ConsoleTextArea()

	public PrintStream out()
	{
		return ps;
	}
	
	private void startConsoleReaderThread(
		InputStream inStream) {
		final BufferedReader br =
			new BufferedReader(new InputStreamReader(inStream));
		new Thread(new Runnable() {
			public void run() {
				StringBuffer sb = new StringBuffer();
				try {
					String s;
					Document doc = getDocument();
					while((s = br.readLine()) != null) {
						boolean caretAtEnd = false;
						caretAtEnd = getCaretPosition() == doc.getLength() ?
							true : false;
						sb.setLength(0);
						//将JTextArea转换成JTextField
						setText(sb.append(s).append('\n').toString());
						
						//append(sb.append(s).append('\n').toString());
						if(caretAtEnd)
							setCaretPosition(doc.getLength());
					}
				}
				catch(IOException e) {
					JOptionPane.showMessageDialog(null,
						"从BufferedReader读取错误：" + e);
					System.exit(1);
				}
			}
		}).start();
	} // startConsoleReaderThread()


	// 该类剩余部分的功能是进行测试
	public static void main(String[] args) {
		JFrame f = new JFrame("ConsoleTextArea测试");
		ConsoleTextArea consoleTextArea = null;

		try {
			consoleTextArea = new ConsoleTextArea();
		}
		catch(IOException e) {
			System.err.println(
				"不能创建LoopedStreams:" + e);
			System.exit(1);
		}

		consoleTextArea.setFont(java.awt.Font.decode("monospaced"));
		f.getContentPane().add(new JScrollPane(consoleTextArea),
			java.awt.BorderLayout.CENTER);
		f.setBounds(50, 50, 300, 300);
		f.setVisible(true);

		f.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(
				java.awt.event.WindowEvent evt) {
				System.exit(0);
			}
		});

		for(int i=0;i<500;i++)
		{
			consoleTextArea.out().println(i);
			try {
				Thread.sleep(10);
			}catch(InterruptedException e) {}
		}
			
//		// 启动几个写操作线程向
//				// System.out和System.err输出
//				startWriterTestThread(
//					"写操作线程 #1", System.err, 920, 50);
//				startWriterTestThread(
//					"写操作线程 #2", System.out, 500, 50);
//				startWriterTestThread(
//					"写操作线程 #3", System.out, 200, 50);
//				startWriterTestThread(
//					"写操作线程 #4", System.out, 1000, 50);
//				startWriterTestThread(
//					"写操作线程 #5", System.err, 850,	50);
	} // main()


	private static void startWriterTestThread(
		final String name, final PrintStream ps, 
		final int delay, final int count) {
		new Thread(new Runnable() {
			public void run() {
				for(int i = 1; i <= count; ++i) {
					ps.println("***" + name + ", hello !, i=" + i);
					try {
						Thread.sleep(delay);
					}
					catch(InterruptedException e) {}
				}
			}
		}).start();
	} // startWriterTestThread()
} // ConsoleTextArea
