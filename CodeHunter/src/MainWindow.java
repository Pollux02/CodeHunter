import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainWindow {
	public static void main(String[] args) {
		MainWin mainWindow = new MainWin();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}

}

class MainWin extends JFrame{
	public static final long serialVersionUID = 1;
	
	private static final int PIXELS_WIDTH = 1280, PIXELS_LENGTH= 720;
	
	private static final Color DARK_GRAY = new Color(15, 15, 15), MEDIUM_GRAY = new Color(50, 50, 50);
	
	private static final String EMPTY_STRING = "",
						 BUTTON_REVIEW_CODES_TEXT = "Revisión de códigos",
						 BUTTON_GENERATE_TEST_FILE_TEXT = "Generar archivo de pruebas",
						 TITLE_FONT = "Freemono",
						 CODE_HUNTER = "Code Hunter"; 
	
	private JPanel panelMenu, panelMain;
	
	private JButton buttonReviewCodes, buttonGenerateTestFile;
	
	private JLabel titleLabel = new JLabel("C<?>H");
	
	public MainWin() {
		super(CODE_HUNTER);
		WindowController mv = new WindowController();
		
		panelMenu = new JPanel(new GridLayout(9, 1));
		panelMain = new JPanel(new GridLayout(1, 1));
		
		buttonReviewCodes = new JButton(BUTTON_REVIEW_CODES_TEXT);
		buttonReviewCodes.setBackground(DARK_GRAY);
		buttonReviewCodes.setForeground(Color.WHITE);
		
		buttonGenerateTestFile = new JButton(BUTTON_GENERATE_TEST_FILE_TEXT );
		buttonGenerateTestFile.setBackground(DARK_GRAY);
		buttonGenerateTestFile.setForeground(Color.WHITE);
		
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font(TITLE_FONT, Font.BOLD, 40));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setVerticalAlignment(SwingConstants.CENTER);
		
		//PANEL MENU
		panelMenu.add(titleLabel);
		panelMenu.add(new JLabel(EMPTY_STRING));
		panelMenu.add(new JLabel(EMPTY_STRING));
		panelMenu.add(buttonReviewCodes);
		panelMenu.add(new JLabel(EMPTY_STRING));
		panelMenu.add(buttonGenerateTestFile);
		panelMenu.add(new JLabel(EMPTY_STRING));
		panelMenu.add(new JLabel(EMPTY_STRING));
		panelMenu.add(new JLabel(EMPTY_STRING));
		
		panelMenu.setBackground(DARK_GRAY);
		
		//PANEL MAIN
		panelMain.setBackground(MEDIUM_GRAY);
		
		add("West", panelMenu);
		add("Center", panelMain);
		setSize(PIXELS_WIDTH, PIXELS_LENGTH);
		addWindowListener(mv);
		
		buttonReviewCodes.addActionListener(new ButtonsController());
		buttonGenerateTestFile.addActionListener(new ButtonsController());
		
		loadSecondaryPanel(new ReviewCodesPanel());
	}
	
	private class ButtonsController implements ActionListener {
		
		public void actionPerformed(ActionEvent e)
		{
			String comando = e.getActionCommand();

			switch(comando)
			{
			case BUTTON_REVIEW_CODES_TEXT:
					loadSecondaryPanel(new ReviewCodesPanel());
				break;
				
			case BUTTON_GENERATE_TEST_FILE_TEXT :	
					loadSecondaryPanel(new GenerateTestFilePanel());
				break;
			}
		}
	}
	
	private void loadSecondaryPanel(JPanel secondaryPanel) {
		panelMain.removeAll();
        panelMain.add(secondaryPanel);
        panelMain.revalidate();
        panelMain.repaint();
	}
	
	public class WindowController implements WindowListener {
		
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}

		public void windowActivated(WindowEvent e)
		{
		}

		public void windowDeactivated(WindowEvent e)
		{
		}

		public void windowClosed(WindowEvent e)
		{
		}

		public void windowDeiconified(WindowEvent e)
		{
		}

		public void windowIconified(WindowEvent e)
		{
		}

		public void windowOpened(WindowEvent e)
		{
		}
	}
}
