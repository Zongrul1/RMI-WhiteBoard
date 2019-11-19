package CreateWhiteBoard;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Stroke;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import java.awt.Button;
import java.awt.Component;

import javax.imageio.ImageIO;
import javax.swing.Box;
import java.awt.Canvas;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import whiteboard_remote.iwhiteboard;

import java.awt.Color;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JInternalFrame;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.JTextPane;
import javax.swing.JList;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;

public class whiteBroadGUI_create extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField textField;
	private paintpanel panel;
	private JFileChooser fileChooser;
	private File file;
    private String dirpath;
    private JList list;
    private JTextArea textArea;
    private String username;
    private iwhiteboard wb;
	/**
	 * Launch the application.
	 */

	//get panel
	public paintpanel getpanel() {
		return panel;
	}
	//get list 
	public JList getJlist() {
		return list;
	}
	//get textarea
	public JTextArea gettextArea() {
		return textArea;
	}
	//set username
	public void setUsername(String username) {
		this.username = username;
	}
	//set wb
	public void set_wb(iwhiteboard wb) {
		this.wb = wb;
	}
	/**
	 * Create the frame.
	 */
	public whiteBroadGUI_create() {
		//avoid JFileChooser exception on MACOS
		try { 
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
		} 
		catch(Exception e) { 
			System.out.println("Error setting Java LAF: " + e); 
		}
		panel = new paintpanel();
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		    	int flag = JOptionPane.showConfirmDialog(null,"save or not?","INFO", JOptionPane.YES_NO_OPTION);
				if(flag == 0) {
					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
		                fileChooser.setCurrentDirectory(new File("."));	              
    	                String str;
    	                try {
    	                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
    	                	str = filter.getEnds(); 
    	                }
    	                catch(Exception e2) {
    	                	str = ".png";
    	                }
		                file = fileChooser.getSelectedFile();
		                File newFile = null;
		                try {
		                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
		                		    newFile = file;
		                		    dirpath = file.getAbsolutePath();
		                		  } else {
		                		    newFile = new File(file.getAbsolutePath() + str);
		                		    dirpath = file.getAbsolutePath() + str;
		                		  }
		                	str = str.substring(1);//remove the point
							ImageIO.write(panel.save(),str, newFile);
							JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
		    	try {
					wb.end();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					System.out.println("exit");
					//e1.printStackTrace();
				}
		        //MainPage m = new MainPage();
		        //m.setVisible(true);
		        e.getWindow().dispose();
		    }
		});
		fileChooser = new JFileChooser();
		MyFileFilter jpgFilter = new MyFileFilter(".jpg", "jpg file (*.jpg)");
		MyFileFilter pngFilter = new MyFileFilter(".png", "png file (*.png)");
        fileChooser.addChoosableFileFilter(jpgFilter);
        fileChooser.addChoosableFileFilter(pngFilter);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 880, 520);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setFont(new Font("Monaco", Font.PLAIN, 14));
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);
		mntmNew.setFont(new Font("Monaco", Font.PLAIN, 14));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int flag = JOptionPane.showConfirmDialog(null,"save or not?","INFO", JOptionPane.YES_NO_OPTION);
				if(flag == 0) {
					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
		                fileChooser.setCurrentDirectory(new File("."));	              
    	                String str;
    	                try {
    	                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
    	                	str = filter.getEnds(); 
    	                }
    	                catch(Exception e2) {
    	                	str = ".png";
    	                }
		                file = fileChooser.getSelectedFile();
		                File newFile = null;
		                try {
		                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
		                		    newFile = file;
		                		    dirpath = file.getAbsolutePath();
		                		  } else {
		                		    newFile = new File(file.getAbsolutePath() + str);
		                		    dirpath = file.getAbsolutePath() + str;
		                		  }
		                	str = str.substring(1);//remove the point
							ImageIO.write(panel.save(),str, newFile);
							JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				dirpath = null;
				panel.clear();
				panel.repaint();
				panel.synchronize();
			}
		});
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		mntmOpen.setFont(new Font("Monaco", Font.PLAIN, 14));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	int flag = JOptionPane.showConfirmDialog(null,"save or not?","INFO", JOptionPane.YES_NO_OPTION);
				if(flag == 0) {
					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
		                fileChooser.setCurrentDirectory(new File("."));	              
		                String str;
		                try {
		                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
		                	str = filter.getEnds(); 
		                }
		                catch(Exception e2) {
		                	str = ".png";
		                }
		                file = fileChooser.getSelectedFile();
		                File newFile = null;
		                try {
		                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
		                		    newFile = file;
		                		    dirpath = file.getAbsolutePath();
		                		  } else {
		                		    newFile = new File(file.getAbsolutePath() + str);
		                		    dirpath = file.getAbsolutePath() + str;
		                		  }
		                	str = str.substring(1);//remove the point
							ImageIO.write(panel.save(),str, newFile);
							JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					fileChooser.setCurrentDirectory(new File("."));          
	                dirpath = fileChooser.getSelectedFile().getAbsolutePath();
	                if (dirpath == null) {
	                    return;
	                }
	                else {
	                    file=new File(dirpath);
	                }                
	                try {
	                	BufferedImage bufImage = ImageIO.read(file);
	                	panel.load(bufImage);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                panel.synchronize();
				}
            }
		});
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		mntmSave.setFont(new Font("Monaco", Font.PLAIN, 14));
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                //fileChooser.setVisible(true);              
                if (dirpath == null) {
                	if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
    	                fileChooser.setCurrentDirectory(new File("."));	   
    	                String str;
    	                try {
    	                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
    	                	str = filter.getEnds(); 
    	                }
    	                catch(Exception e2) {
    	                	str = ".png";
    	                }
    	                file = fileChooser.getSelectedFile();
    	                File newFile = null;
    	                try {
    	                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
    	                		    newFile = file;
    	                		    dirpath = file.getAbsolutePath();
    	                		  } else {
    	                		    newFile = new File(file.getAbsolutePath() + str);
    	                		    dirpath = file.getAbsolutePath() + str;
    	                		  }
    	                	str = str.substring(1);//remove the point
    						ImageIO.write(panel.save(),str, newFile);
    						JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
    					} catch (IOException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
    				}
                    return;
                }
                else {
                    file = new File(dirpath);
                }                
                try {
                	String[] format = dirpath.split("\\.");
					ImageIO.write(panel.save(), format[format.length - 1],file);
					JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
		});
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnFile.add(mntmSaveAs);
		mntmSaveAs.setFont(new Font("Monaco", Font.PLAIN, 14));
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
	                fileChooser.setCurrentDirectory(new File("."));	              
	                String str;
	                try {
	                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
	                	str = filter.getEnds(); 
	                }
	                catch(Exception e2) {
	                	str = ".png";
	                }
	                file = fileChooser.getSelectedFile();
	                File newFile = null;
	                try {
	                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
	                		    newFile = file;
	                		    dirpath = file.getAbsolutePath();
	                		  } else {
	                		    newFile = new File(file.getAbsolutePath() + str);
	                		    dirpath = file.getAbsolutePath() + str;
	                		  }
	                	str = str.substring(1);//remove the point
						ImageIO.write(panel.save(),str, newFile);
						JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
            }
		});
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mntmClose.setFont(new Font("Monaco", Font.PLAIN, 14));
		mnFile.add(mntmClose);
		mntmClose.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
	    	int flag = JOptionPane.showConfirmDialog(null,"save or not?","INFO", JOptionPane.YES_NO_OPTION);
			if(flag == 0) {
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
	                fileChooser.setCurrentDirectory(new File("."));	              
	                String str;
	                try {
	                	MyFileFilter filter = (MyFileFilter)fileChooser.getFileFilter();
	                	str = filter.getEnds(); 
	                }
	                catch(Exception e2) {
	                	str = ".png";
	                }
	                file = fileChooser.getSelectedFile();
	                File newFile = null;
	                try {
	                	  if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
	                		    newFile = file;
	                		    dirpath = file.getAbsolutePath();
	                		  } else {
	                		    newFile = new File(file.getAbsolutePath() + str);
	                		    dirpath = file.getAbsolutePath() + str;
	                		  }
	                	str = str.substring(1);//remove the point
						ImageIO.write(panel.save(),str, newFile);
						JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
	    	try {
				wb.end();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				System.out.println("exit");
				//e1.printStackTrace();
			}
	    	System.exit(0);
			}
		});
		
		JMenu mnClientControl = new JMenu("Client Manage");
		mnClientControl.setFont(new Font("Monaco", Font.PLAIN, 14));
		menuBar.add(mnClientControl);
		
		JMenuItem mntmKickOut = new JMenuItem("Kick Out");
		mnClientControl.add(mntmKickOut);
		mntmKickOut.setFont(new Font("Monaco", Font.PLAIN, 14));
		mntmKickOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog( "enter the username" );
				try {
					wb.removeUser(input);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					System.out.println("err in GUI");
					//e1.printStackTrace();
				}
            }
		});
		
		JMenu mnInfo = new JMenu("Info");
		mnInfo.setFont(new Font("Monaco", Font.PLAIN, 14));
		menuBar.add(mnInfo);
		
		JMenuItem mntmAboutUs = new JMenuItem("About Us");
		mntmAboutUs.setFont(new Font("Monaco", Font.PLAIN, 14));
		mntmAboutUs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoPage logFrame = new infoPage();
				logFrame.setVisible(true);
			}
		});
		mnInfo.add(mntmAboutUs);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		Image recImg = new ImageIcon(this.getClass().getResource("/rectIcon.png")).getImage();
		Image ovalImg = new ImageIcon(this.getClass().getResource("/ovalIcon.png")).getImage();
		Image pencilImg = new ImageIcon(this.getClass().getResource("/paintIcon.png")).getImage();
		Image lineImg = new ImageIcon(this.getClass().getResource("/lineIcon.png")).getImage();
		Image circleImg = new ImageIcon(this.getClass().getResource("/circleIcon.png")).getImage();
		Image eraserImg = new ImageIcon(this.getClass().getResource("/eraserIcon.png")).getImage();
		Image textBoxImg = new ImageIcon(this.getClass().getResource("/textBoxIcon.png")).getImage();
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBounds(0, 0, 625, 41);
		contentPane.add(toolBar);
		
		JButton btnRectangle = new JButton("Rectangle");
		btnRectangle.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnRectangle);
		btnRectangle.setIcon(new ImageIcon(recImg));
		
		JButton btnOval = new JButton("Oval");
		btnOval.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnOval);
		btnOval.setIcon(new ImageIcon(ovalImg));
		
		JButton btnPencil = new JButton("Pencil");
		btnPencil.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnPencil);
		btnPencil.setIcon(new ImageIcon(pencilImg));
		
		JButton btnLine = new JButton("Line");
		btnLine.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnLine);
		btnLine.setIcon(new ImageIcon(lineImg));
		
		JButton btnCircle = new JButton("Circle");
		btnCircle.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnCircle);
		btnCircle.setIcon(new ImageIcon(circleImg));
		
		JButton btnText = new JButton("Text");
		btnText.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnText);
		btnText.setIcon(new ImageIcon(textBoxImg));
		
		JButton btnEraser = new JButton("Eraser");
		btnEraser.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		toolBar.add(btnEraser);
		btnEraser.setIcon(new ImageIcon(eraserImg));
		
		//button_listener
		btnRectangle.addActionListener(this);
		btnOval.addActionListener(this);
		btnPencil.addActionListener(this);
		btnLine.addActionListener(this);
		btnCircle.addActionListener(this);
		btnText.addActionListener(this);
		btnEraser.addActionListener(this);
		
		JComboBox comboBox = new JComboBox();
		toolBar.add(comboBox);
		comboBox.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Small", "Medium", "Large"}));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s=(String)comboBox.getSelectedItem();
				Stroke selectStroke = new BasicStroke(1.0f);
				if(s.contentEquals("Small")) {
					selectStroke = new BasicStroke(1.0f);
				}
				else if(s.contentEquals("Medium")) {
					selectStroke = new BasicStroke(2.0f);
				}
				else if(s.contentEquals("Large")) {
					selectStroke = new BasicStroke(3.0f);
				}
				panel.setStroke(selectStroke);
			}
		});
		
		JButton btnSel = new JButton("");
		btnSel.setEnabled(false);
		btnSel.setBackground(Color.BLACK);
		btnSel.setBounds(142, 42, 22, 22);
		btnSel.setOpaque(true);
		btnSel.setBorderPainted(false);
		contentPane.add(btnSel);
		
		JLabel lblSelectedColor = new JLabel("Selected Color:");
		lblSelectedColor.setFont(new Font("Monaco", Font.PLAIN, 14));
		lblSelectedColor.setBounds(10, 48, 120, 16);
		contentPane.add(lblSelectedColor);
		
		JButton btnBlue = new JButton("");
		btnBlue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.BLUE);
				panel.setColor(Color.BLUE);
			}
		});
		btnBlue.setBounds(243, 42, 22, 22);
		btnBlue.setBackground(Color.BLUE);
		btnBlue.setOpaque(true);
		btnBlue.setBorderPainted(false);
		contentPane.add(btnBlue);
		btnBlue.setOpaque(true);
		btnBlue.setBorderPainted(false);
		btnBlue.setOpaque(true);
		btnBlue.setBorderPainted(false);
		
		JButton btnGree = new JButton("");
		btnGree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.GREEN);
				panel.setColor(Color.GREEN);
			}
		});
		btnGree.setBounds(355, 42, 22, 22);
		contentPane.add(btnGree);
		btnGree.setBackground(Color.GREEN);
		btnGree.setOpaque(true);
		btnGree.setBorderPainted(false);
		
		JButton btnPurp = new JButton("");
		btnPurp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.MAGENTA);
				panel.setColor(Color.MAGENTA);
			}
		});
		btnPurp.setBounds(299, 42, 22, 22);
		contentPane.add(btnPurp);
		btnPurp.setBackground(Color.MAGENTA);
		btnPurp.setOpaque(true);
		btnPurp.setBorderPainted(false);
		
		JButton btnOthers = new JButton("Others");
		btnOthers.setFont(new Font("Apple Color Emoji", Font.PLAIN, 14));
		btnOthers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color initialColor = null;
	            Color selectedColor = JColorChooser.showDialog(null, "Choose a color you like", initialColor);
	            System.out.println("");
	            btnSel.setBackground(selectedColor);
	            btnOthers.setBackground(selectedColor);
	            panel.setColor(selectedColor);
			}
		});
		btnOthers.setBounds(467, 42, 92, 22);
		contentPane.add(btnOthers);
		btnOthers.setBackground(Color.LIGHT_GRAY);
		
		
		JButton btnYelo = new JButton("");
		btnYelo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.YELLOW);
				panel.setColor(Color.YELLOW);

			}
		});
		btnYelo.setBounds(327, 42, 22, 22);
		btnYelo.setBackground(Color.YELLOW);
		btnYelo.setOpaque(true);
		btnYelo.setBorderPainted(false);
		
		contentPane.add(btnYelo);
		
		JButton btnOrag = new JButton("");
		btnOrag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.ORANGE);
				panel.setColor(Color.ORANGE);
			}
		});
		btnOrag.setBounds(271, 42, 22, 22);
		contentPane.add(btnOrag);
		btnOrag.setBackground(Color.ORANGE);
		btnOrag.setOpaque(true);
		btnOrag.setBorderPainted(false);
		
		JButton btnRed = new JButton("");
		btnRed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.RED);
				panel.setColor(Color.RED);
			}
		});
		btnRed.setBounds(383, 42, 22, 22);
		contentPane.add(btnRed);
		btnRed.setBackground(Color.RED);
		btnRed.setOpaque(true);
		btnRed.setBorderPainted(false);
		
		JLabel lblPalette = new JLabel("Palette:");
		lblPalette.setFont(new Font("Monaco", Font.PLAIN, 14));
		lblPalette.setBounds(176, 48, 73, 16);
		contentPane.add(lblPalette);
		
		panel.setBounds(10, 74, 615, 396);
		panel.setBackground(Color.white);
		contentPane.add(panel);		  
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setBounds(8, 72, 617, 398);
		contentPane.add(panel_1);
		
		
		//Color Listener
		btnEraser.addActionListener(this);
		
		JLabel lblUsers = new JLabel("Users");
		lblUsers.setFont(new Font("Monaco", Font.PLAIN, 14));
		lblUsers.setBounds(637, 0, 66, 25);
		contentPane.add(lblUsers);
		
		JLabel lblChatbox = new JLabel("ChatBox");
		lblChatbox.setFont(new Font("Monaco", Font.PLAIN, 14));
		lblChatbox.setBounds(637, 161, 66, 25);
		contentPane.add(lblChatbox);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBounds(611, 187, 183, 207);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
//		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(637, 184, 207, 207);
		contentPane.add(scroll);
		
		
		textField = new JTextField();
		textField.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, Color.LIGHT_GRAY));
		textField.setBounds(637, 403, 130, 26);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				//no client info at the moment
				//add client ID in front of their messages when we have client
				
				String Msg = textField.getText();
				try {
					if(!Msg.equals("")) {
						wb.broadcast("[" + username +"]" + ": " + Msg);
					}
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "the manager has left the room", "error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
					System.exit(0);
				}
				//textArea.append(Msg+"\n");
				textField.setText(null);;	
			}
		});
		btnSend.setBounds(774, 403, 80, 29);
		contentPane.add(btnSend);
		
		list = new JList();
		list.setBounds(637, 29, 207, 131);
		list.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, Color.LIGHT_GRAY));
		contentPane.add(list);
		
		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.WHITE);
				panel.setColor(Color.WHITE);
			}
		});
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setBackground(Color.WHITE);
		button.setBounds(411, 42, 22, 22);
		contentPane.add(button);
		
		JButton btnBlack = new JButton("");
		btnBlack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("");
				btnSel.setBackground(Color.BLACK);
				panel.setColor(Color.BLACK);
			}
		});
		btnBlack.setBounds(439, 42, 22, 22);
		contentPane.add(btnBlack);
		btnBlack.setBackground(Color.BLACK);
		btnBlack.setOpaque(true);
		btnBlack.setBorderPainted(false);
		
//		JButton button_1 = new JButton("");
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				System.out.println("");
//				btnSel.setBackground(Color.BLACK);
//				panel.setColor(Color.BLACK);
//			}
//		});
//		button_1.setOpaque(true);
//		button_1.setBorderPainted(false);
//		button_1.setBackground(Color.BLACK);
//		button_1.setBounds(439, 42, 22, 22);
//		contentPane.add(button_1);
		this.setTitle("Manager");
		setVisible(true);

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String str = e.getActionCommand();
		if(str.contentEquals("Line")) {
			 panel.setType("line");
		}
		else if(str.contentEquals("Rectangle")) {
			panel.setType("rect");
		}
		else if(str.contentEquals("Circle")) {
			panel.setType("circle");
		}
		else if(str.contentEquals("Pencil")) {
			panel.setType("freedraw");
		}
		else if(str.contentEquals("Oval")) {
			panel.setType("oval");
		}
		else if(str.contentEquals("Eraser")) {
			panel.setType("erase");
		}
		else if(str.contentEquals("Text")) {
			panel.setType("text");
		}
		else if(str.contentEquals("Exit")) {
			System.exit(0);
			
		}
	}
}
