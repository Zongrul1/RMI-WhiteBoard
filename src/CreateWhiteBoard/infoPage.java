package CreateWhiteBoard;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class infoPage extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					infoPage frame = new infoPage();
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
	public infoPage() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel infoPageIcon = new JLabel("New label");
		infoPageIcon.setBackground(new Color(204, 204, 204));
		Image unimelbImg = new ImageIcon(this.getClass().getResource("/unimelblogo.jpeg")).getImage();
		infoPageIcon.setIcon(new ImageIcon(unimelbImg));
		infoPageIcon.setBounds(22, 63, 128, 128);
		contentPane.add(infoPageIcon);
		
		JEditorPane dtrpnThisProgrmeasy = new JEditorPane();
		dtrpnThisProgrmeasy.setEditable(false);
		dtrpnThisProgrmeasy.setText("This progrm (\"WhiteBoards\") is made for \n               the University of Melboune\n               COMP90015 Assignment 2\n\t   @ L.L.D.S");
		dtrpnThisProgrmeasy.setBounds(162, 100, 282, 70);
		contentPane.add(dtrpnThisProgrmeasy);

	}
}

