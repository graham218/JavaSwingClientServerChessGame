import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.rmi.registry.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChessServer extends JFrame 
{
	
	private static final long serialVersionUID = 1L;
	
	private Toolkit tk = Toolkit.getDefaultToolkit();
	private JPanel pan;
	private JLabel lab1, lab2, lab3, lab4, lab5;
	private static ChessServer server;
	
	public static void main(String[] args) 
	{
		server = new ChessServer();
		if(hostGames())
		{
			server.lab5.setText(server.lab5.getText() + "\n...Hosting successful!");
		}
		else
		{
			server.lab5.setText(server.lab5.getText() + "\n...Hosting Failed!");
		}
	}
	public ChessServer()
	{
		this.setSize(300,200);
		
		//use awt.Toolkit to get screensize and set xPos and yPos to the center of the screen
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (this.getWidth() / 2);
		int yPos = (dim.height / 2) - (this.getHeight() / 2);
		//set location to center defined by xPos and yPos
		this.setLocation(xPos, yPos);
		
		//turn off resizability, set close operation, turn off decoration, and set title of window		
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("JavaChess Server");
		
		pan = new JPanel();
		pan.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		lab1 = new JLabel("Server starting up");	
		lab2 = new JLabel("");
		lab3 = new JLabel("");
		lab4 = new JLabel("");
		lab5 = new JLabel("");
		
		lab1.setHorizontalAlignment(JLabel.CENTER);
		lab2.setHorizontalAlignment(JLabel.CENTER);
		lab3.setHorizontalAlignment(JLabel.CENTER);
		lab4.setHorizontalAlignment(JLabel.CENTER);
		lab5.setHorizontalAlignment(JLabel.CENTER);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 15;
		pan.add(lab1, c);
		
		c.gridx = 0;
		c.gridy = 1;
		pan.add(lab2, c);
		
		c.gridx = 0;
		c.gridy = 2;
		pan.add(lab3, c);
		
		c.gridx = 0;
		c.gridy = 3;
		pan.add(lab4, c);
		
		c.gridx = 0;
		c.gridy = 4;
		pan.add(lab5, c);
		
		
		this.add(pan);
		this.setVisible(true);
	}
	public static boolean hostGames()
	{
		boolean success = false;
		try {
			//create GameSession objects to host
			GameSession sesh1 = new GameSession();
			GameSession sesh2 = new GameSession();
			GameSession sesh3 = new GameSession();
			
			//Create 3 registry objects for each session
			Registry registry1;
			Registry registry2;
			Registry registry3;
			//registry = LocateRegistry.getRegistry();

			//Start up the registries 
			registry1 = LocateRegistry.createRegistry(4200); 
			System.out.println("registry1 created at port 4200");
			registry2 = LocateRegistry.createRegistry(4201);
			System.out.println("registry2 created at port 4201");
			registry3 = LocateRegistry.createRegistry(4202);
			System.out.println("registry2 created at port 4202");

			//Bind each Session with a registry and give it a name
			registry1.rebind("Session1", sesh1);
			server.lab2.setText("Sesh1 up and running");
			registry2.rebind("Session2", sesh2);
			server.lab3.setText("Sesh2 up and running");
			registry3.rebind("Session3", sesh3);
			server.lab4.setText("Sesh3 up and running");
			success = true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return success;
	}
}