package pipe.samodules;

public class TestThread {

	static int i=0;
	public static void test( final ConsoleTextArea resultlabel)
	{
		i++;
		new Thread(new Runnable() {
			public void run() {
				
					resultlabel.out().println(i);
					try {
						Thread.sleep(100);
					}catch(InterruptedException e1) {}
				
			}
		}).start();
	}
}
