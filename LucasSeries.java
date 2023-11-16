import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.awt.Toolkit;
import java.awt.Window.Type;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LucasSeries extends JFrame {

	private static final long serialVersionUID = 1L;
	// Initialize the variables of the program
	private JPanel contentPane;
	private JTextField rowsField;
	private JTextField txtSum;
	private JButton sumBtn;
	private JButton cancelBtn;
	private JProgressBar progressBar;
	private JTextArea textArea;
	private SwingWorker<Void, Integer> worker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LucasSeries frame = new LucasSeries();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LucasSeries() {
		setTitle("Finding the sum of Lucas Series ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 100, 627, 371);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel rowsLabel = new JLabel("Number of rows in Lucas Series:");
		rowsLabel.setBounds(20, 10, 207, 22);
		contentPane.add(rowsLabel);

		rowsField = new JTextField();
		rowsField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		rowsField.setBounds(227, 7, 58, 29);
		contentPane.add(rowsField);
		rowsField.setColumns(10);

		sumBtn = new JButton("Get Sum of Lucas Series");

		sumBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Calculating the sum of the lucas numbers by calling a new method
				startLucasSeriesCalculation();
			}
		});
		sumBtn.setBounds(297, 7, 198, 31);
		contentPane.add(sumBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setEnabled(false); // Initially disable the cancelBtn before the process starts
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Assign the thread.cancel to true, while it is not null or already completed
				if (worker != null && !worker.isDone()) {
					worker.cancel(true);
				}

			}
		});
		cancelBtn.setBounds(493, 7, 117, 31);
		contentPane.add(cancelBtn);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(0, 308, 363, 35);
		contentPane.add(progressBar);

		txtSum = new JTextField();
		txtSum.setText("Sum = 0");
		txtSum.setBounds(370, 308, 257, 35);
		contentPane.add(txtSum);
		txtSum.setColumns(10);

		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(6, 39, 615, 257);
		contentPane.add(scrollPane);
	}
	
	//defining the Lucas sequence
	private int lucasNum(int num) {
		if (num == 0) {
			return 2;
		}
		if (num == 1) {
			return 1;
		}
		return lucasNum(num - 1) + lucasNum(num - 2);
	}
	
	//method to calculate the sum of Lucas numbers
	private void startLucasSeriesCalculation() {
		int rows;
		try {
			rows = Integer.parseInt(rowsField.getText()); // Converting a string to integer for rowsField
		} catch (NumberFormatException e) { // Check for only digit inputs
			JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.", "Warning",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// Enable the cancelBtn when process starts
	    cancelBtn.setEnabled(true);
	    
		// Initialize the fields and methods
		textArea.setText("");
		txtSum.setText("Sum = ");
		progressBar.setValue(0);
		cancelBtn.setEnabled(true);
		
		// SwingWorker thread object 
		worker = new SwingWorker<Void, Integer>() {
			private int sum = 0;

			@Override
			protected Void doInBackground() throws Exception {
				for (int i = 0; i < rows; i++) {
					if (isCancelled()) { // cancelBtn -> stop the thread
						break;
					}
					// Sum of each lucas number in array
					int lucasNumTemp = lucasNum(i);
					sum += lucasNumTemp;
					publish(lucasNumTemp);
					progressBar.setValue((i + 1) * 100 / rows); // Define the formula to count the value of progressBar

					// Introduce a delay between the iterations
					try {
						Thread.sleep(200); // 200 milliseconds = 0.2 seconds
					} catch (InterruptedException e) {
						// interruption 
						break;
					}
				}
				return null;
			}
			

			@Override
			protected void process(List<Integer> chunks) {
				// Displaying each number
				for (int number : chunks) {
					textArea.append(number + "\n");

				}
			}

			@Override
			protected void done() {
				// Final result after stopping the thread
				txtSum.setText("Sum = " + sum);

				cancelBtn.setEnabled(false);

				try { // Store the result in a text file
					FileWriter writer = new FileWriter("lucas_series.txt");
					writer.write(textArea.getText());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		worker.execute(); // Enable the thread

	}
}
