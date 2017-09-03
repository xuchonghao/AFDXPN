package pipe.steadymodules;

import it.unifi.oris.sirio.petrinet.Marking;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class SteadyProbDetail extends JFrame {

	private JPanel contentPane;
	JTextPane textPane;
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Map<Marking, Double> steady = new HashMap<Marking, Double>();
//					Marking a = new Marking();
//					steady.put(a, 12.0);
//					SteadyProbDetail frame = new SteadyProbDetail(steady);
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public SteadyProbDetail(Map<Marking, Double> steady) {
		
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 304, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Set<Marking> mset = steady.keySet();
		StringBuffer sb = new StringBuffer();
		for(Marking m : mset){
			sb.append(m+":"+steady.get(m)+"\n");
		}
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 268, 241);
		contentPane.add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPane.setText(sb.toString());
		System.out.println(textPane.getText());
		
		setVisible(true);
		
		setDefaultCloseOperation(2);
	}
}
