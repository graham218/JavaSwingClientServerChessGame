import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AddressFrame extends JFrame
{	
	//declare components globally so they can be edited from any event handler
	private static final long serialVersionUID = 1L; //once again this is needed
	JPanel pan; //panel for components
	JLabel lab; //label
	JTextField tf; //text field 
	JButton button; //enter button
	
	public AddressFrame()
	{
		//set size of window
		this.setSize(400,65);
		
		//use awt.Toolkit to get screensize and set xPos and yPos to the center of the screen
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (this.getWidth() / 2);
		int yPos = (dim.height / 2) - (this.getHeight() / 2);
		//set location to center defined by xPos and yPos
		this.setLocation(xPos, yPos);
		
		//turn off resizability, set close operation, turn off decoration, and set title of window
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Enter IP Address of Server");
		
		//create panel 
		pan = new JPanel();
		
		//Create components
		
		//label
		lab = new JLabel("Enter IP Address of Server");
		lab.setToolTipText("In the box below, please enter the IP address " + 
						   "of the Server hosting the game. This should be in" +
						   "format: \"XXX.XXX.XXX.XXX\" where every x is a number");
		
		//text field
		tf = new JTextField("", 10);
		tf.setToolTipText("Enter the IP address here with the format: " +
						  "\"XXX.XXX.XXX.XXX\" where every x is a number");
		//button
		button = new JButton("Enter");
		button.setToolTipText("Press to confirm IP address entered and connect to Server");
		ButtonListener butListener = new ButtonListener();
		button.addActionListener(butListener);
		
		
		//add components to panel and then add the panel to the Frame
		pan.add(lab);
		pan.add(tf);
		pan.add(button);
		this.add(pan);

		this.setVisible(false);
		
	}
	
	//define button listener class
	private class ButtonListener implements ActionListener
	{	
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == button)
			{	
				ChessClient.serverIP = tf.getText();
				ChessClient.ipEntered = true;
			}
		}
	}
	
}
