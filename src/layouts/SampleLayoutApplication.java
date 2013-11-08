package layouts;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public final class SampleLayoutApplication implements PropertyChangeListener,
		ActionListener, KeyListener {

	//VARIABLES
	static Locale locale = new Locale("en");
	final static Context cx = new Context("Languages");
	ResourceBundle rb = cx.getBundle();

	static JButton viewButton = null;
	static JButton editButton = null;
	static JButton copyButton = null;
	static JButton moveButton = null;
	static JButton newFolderButton = null;
	static JButton deleteButton = null;
	static JButton exitButton = null;
	JButton buttonCancel=null;
	JButton buttonOk=null;

	static JLabel leftFreeSpace = null;
	static JLabel rightFreeSpace = null;
	static JLabel fileNumberLeft = null;
	static JLabel fileNumberRight = null;
	static JLabel leftPathDescription = null;
	static JLabel rightPathDescription = null;
	static JLabel label = new JLabel("DUPA");

	static int index = 1;
	static int indexRight = 1;
	static File[] roots = File.listRoots();
	File back = new File("...");

	static ArrayList<Integer> leftSelectedCells = new ArrayList<Integer>();
	static ArrayList<Integer> rightSelectedCells = new ArrayList<Integer>();

	static JFrame framex = null;
	
	static DialogYesNo dial = null;
	static String newFolderName = null;

	static String pathLeft = ""+roots[1];
	static String pathRight = ""+roots[1];
	
	static File folder = new File(pathLeft);
	static File folderRight = new File(pathRight);
	static File[] listOfFilesLeft = folder.listFiles();
	static File[] listOfFilesRight = folderRight.listFiles();

	static ArrayList<File> arrayOfFilesLeft = new ArrayList<File>();
	static ArrayList<File> arrayOfFilesRight = new ArrayList<File>();

	static JTable tableLeft = null;
	static JTable tableRight = null;

	int leftSelectedFile = -1;
	int rightSelectedFile = -1;
	static Sort whichSort = Sort.ALPHA;
	static Sort whichSortRight = Sort.ALPHA;
	JPanel panelNorthS = null;
	JPanel panelNorthSRight = null;

	static TableColumnModel tcm;
	static TableColumnModel tcmRight;

	private ProgressMonitor progressMonitor;
	private Task task;
	private String operation = "";
	private String which = "";
	File progressedFile=null;
	//--
	
	//CONSTRUCTOR
	private SampleLayoutApplication() {
		locale = new Locale("en");
		cx.setLocale(locale);
	}
	//--
	
	//ENUMS
	public enum Sort {
		ALPHA, ALPHB, SIZEA, SIZEB
	}
	public enum Type {
		START, END;
	}
	//--
	
	//CREATE FUNCTIONS
	private JSeparator createSeparator() {
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		Dimension size = separator.getPreferredSize();
		size.height = 30;
		separator.setPreferredSize(size);
		return separator;
	}
	private JButton createImageButton(String iconn) {
		ImageIcon icon = new ImageIcon("icons/" + iconn + ".png");
		final JButton buttonIcon = new JButton(icon);

		buttonIcon.setBorder(null);
		buttonIcon.setBorderPainted(false);
		buttonIcon.setFocusPainted(false);
		buttonIcon.setContentAreaFilled(false);
		buttonIcon.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				buttonIcon.setContentAreaFilled(true);
				buttonIcon.setToolTipText(cx.getBundle().getString("tooltip"));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				buttonIcon.setContentAreaFilled(false);
			}
		});
		return buttonIcon;
	}
	private JButton createButton(String text) {
		final JButton button = new JButton(rb.getString(text));
		button.setContentAreaFilled(false);
		button.setOpaque(true);
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setContentAreaFilled(true);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setContentAreaFilled(false);
			}
		});
		return button;
	}
	private static int numberOfFiles(File[] listOfFiless) {
		int a = 0;
		for (int i = 0; i < listOfFiless.length; i++) {
			if (listOfFiless[i].isFile()) {
				++a;
			}
		}

		return a;
	}
	private static int numberOfDirectories(File[] listOfFiless) {
		int a = 0;
		for (int i = 0; i < listOfFiless.length; i++) {
			if (listOfFiless[i].isDirectory()) {
				++a;
			}
		}
		return a;
	}
	//--
	
	//SORTING
	private void sortAlphA(File[] listOfFilles,
			ArrayList<File> arrayOfFilesLeft2) {
		arrayOfFilesLeft2.clear();
		ArrayList<File> foldery = new ArrayList<File>();
		ArrayList<File> pliki = new ArrayList<File>();

		for (int i = 0; i < listOfFilles.length; i++) {

			if (listOfFilles[i].isDirectory()) {
				foldery.add(listOfFilles[i]);
			} else {
				pliki.add(listOfFilles[i]);
			}
		}

		if (pathLeft.length() > 3 && arrayOfFilesLeft2 == arrayOfFilesLeft) {
			arrayOfFilesLeft2.add(back);
		}
		if (pathRight.length() > 3 && arrayOfFilesLeft2 == arrayOfFilesRight) {
			arrayOfFilesLeft2.add(back);
		}
		arrayOfFilesLeft2.addAll(foldery);
		arrayOfFilesLeft2.addAll(pliki);

	}
	private void sortSizeD(File[] listOf, ArrayList<File> arrayOfFilesLeft2) {
		ArrayList<File> foldery = new ArrayList<File>();
		arrayOfFilesLeft2.clear();
		File[] files = new File[numberOfFiles(listOf)];
		int j = 0;
		for (int i = 0; i < listOf.length; i++) {

			if (listOf[i].isDirectory()) {
				foldery.add(listOf[i]);
			} else {
				files[j] = listOf[i];
				j++;
			}
		}
		for (int b = 0; b < numberOfFiles(listOf); b++) {
			for (int i = 1; i < numberOfFiles(listOf); i++) {
				if (files[i].length() > files[i - 1].length()) {
					File tmp = files[i];
					files[i] = files[i - 1];
					files[i - 1] = tmp;
				}
			}
		}
		ArrayList<File> spisPlikow = new ArrayList<File>(foldery);
		for (int i = 0; i < numberOfFiles(listOf); i++) {
			spisPlikow.add(files[i]);
		}

		if (pathLeft.length() > 3 && arrayOfFilesLeft2 == arrayOfFilesLeft) {
			arrayOfFilesLeft2.add(back);
		}
		if (pathRight.length() > 3 && arrayOfFilesLeft2 == arrayOfFilesRight) {
			arrayOfFilesLeft2.add(back);
		}
		arrayOfFilesLeft2.addAll(spisPlikow);

	}
	private void sortSizeA(File[] listOf, ArrayList<File> arrayOfFilesLeft2) {
		ArrayList<File> foldery = new ArrayList<File>();
		arrayOfFilesLeft2.clear();
		File[] files = new File[numberOfFiles(listOf)];
		int j = 0;
		for (int i = 0; i < listOf.length; i++) {

			if (listOf[i].isDirectory()) {
				foldery.add(listOf[i]);
			} else {
				files[j] = listOf[i];
				j++;
			}
		}
		for (int b = 0; b < numberOfFiles(listOf); b++) {
			for (int i = 1; i < numberOfFiles(listOf); i++) {
				if (files[i].length() < files[i - 1].length()) {
					File tmp = files[i];
					files[i] = files[i - 1];
					files[i - 1] = tmp;
				}
			}
		}
		ArrayList<File> spisPlikow = new ArrayList<File>(foldery);
		for (int i = 0; i < numberOfFiles(listOf); i++) {
			spisPlikow.add(files[i]);
		}
		if (pathLeft.length() > 3 && arrayOfFilesLeft2 == arrayOfFilesLeft) {
			arrayOfFilesLeft2.add(back);
		}
		if (pathRight.length() > 3 && arrayOfFilesLeft2 == arrayOfFilesRight) {
			arrayOfFilesLeft2.add(back);
		}
		arrayOfFilesLeft2.addAll(spisPlikow);
	}
	//--
	
	// INITIALIZE
	private void initializeDataNew() {
		folder = new File(pathLeft);
		listOfFilesLeft = folder.listFiles();
		sortAlphA(listOfFilesLeft, arrayOfFilesLeft);

		folderRight = new File(pathRight);
		listOfFilesRight = folderRight.listFiles();
		sortAlphA(listOfFilesRight, arrayOfFilesRight);
	}	
	public static Object[][] initializeData1(ArrayList<File> listOfFiles) {
		Object[][] data = new Object[listOfFiles.size()][5];
		int i;
		for (i = 0; i < listOfFiles.size(); i++) {
			String name = listOfFiles.get(i).getName();
			String ext = "";
			boolean extS = false;
			for (int j = 0; j < name.length(); j++) {
				if (name.charAt(j) == ('.')) {
					extS = true;
					continue;
				}
				if (extS) {
					ext += name.charAt(j);
				}
			}
			data[i][0] = listOfFiles.get(i).getName();
			if (!listOfFiles.get(i).isDirectory()) {
				data[i][1] = ext;
			} else {
				data[i][1] = "";
			}
			if (!listOfFiles.get(i).isDirectory()) {
				data[i][2] = (listOfFiles.get(i)).length();
			} else {
				data[i][2] = "<DIR>";
			}
			String datee = SimpleDateFormat.getDateInstance(
					SimpleDateFormat.LONG, cx.getLocale()).format(
					listOfFiles.get(i).lastModified())
					+ " "
					+ SimpleDateFormat.getTimeInstance(DateFormat.FULL,
							cx.getLocale()).format(
							listOfFiles.get(i).lastModified());
			data[i][3] = datee;
		}

		return data;
	}
	private void leftReload() {
		leftSelectedFile=0;
		rightSelectedFile=-1;
		initializeDataNew();
		tableLeft.setModel(new MyTableModel(initializeData1(arrayOfFilesLeft)));
		tcm.getColumn(0).setCellRenderer(
				new IconTextCellRemderer(arrayOfFilesLeft, listOfFilesLeft));
	}
	private void rightReload() {
		leftSelectedFile=-1;
		rightSelectedFile=0;
		initializeDataNew();
		tableRight
				.setModel(new MyTableModel(initializeData1(arrayOfFilesRight)));
		tcmRight.getColumn(0).setCellRenderer(
				new IconTextCellRemderer(arrayOfFilesRight, listOfFilesRight));

	}
	//--
	
	//OPERATIONS
	public static void deleteF(File folder) throws IOException {
		if (folder.isDirectory()) {
			if (folder.list().length == 0) {
				folder.delete();
			} else {
				String files[] = folder.list();
				for (String temp : files) {
					File fileDelete = new File(folder, temp);
					deleteF(fileDelete);
				}
				if (folder.list().length == 0) {
					folder.delete();
				}
			}
		} else {
			folder.delete();
		}
	}
	private void copyFolder(File file, String path) {
		if (file.isDirectory()) {
			new File(path + file.getName()).mkdir();
			if (file.list().length > 0) {
				File[] files = file.listFiles();
				for (File temp : files) {
					leftFreeSpace.setText("" + path + temp.getName() + "/");
					copyFolder(temp, path + file.getName() + "/");
				}

			}
		} else {
			copyFile(file, path);
		}
	}
	private void moveFilesOrDirectories(File file, String path) {
		if (file.isFile()) {
			file.renameTo(new File(path + file.getName()));
		} else {
			if (file.list().length > 0) {
				copyFolder(file, path);
				try {
					deleteF(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				new File(path + file.getName()).mkdir();
				file.delete();
			}

		}
	}
	private void copyFile(File file, String path) {
		File newFile = new File(file.getName());
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			inStream = new FileInputStream(file);
			outStream = new FileOutputStream(newFile);

			byte[] buffer = new byte[1024];

			int length;
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
				// setProgress = (int)(100 * read/overall);
			}

			inStream.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		newFile.renameTo(new File(path + newFile.getName()));
	}
	private void copyFilesOrDirectories(File file, String path) {
		if (file.isFile()) {
			copyFile(file, path);
		} else {
			if (file.list().length != 0) {
				copyFolder(file, path);
			} else {

				new File(path + file.getName()).mkdir();
			}

		}
	}
	private void deleteFiles(){
		if ((pathLeft.length() > 3 && leftSelectedFile == 0)
				|| (pathRight.length() > 3 && rightSelectedFile == 0)
				|| (leftSelectedFile < 0 && rightSelectedFile < 0)) {
			// JOptionPane.showMessageDialog(framex,
			// cx.getBundle().getString("nofiles"));
			JOptionPane.showMessageDialog(framex, cx.getBundle()
					.getString("nofiles"),
					cx.getBundle().getString("warning"),
					JOptionPane.PLAIN_MESSAGE);
		} else {
			String name = "";
			if (leftSelectedCells.size() > 1
					|| rightSelectedCells.size() > 1) {
				if (leftSelectedCells.size() > rightSelectedCells
						.size()) {
					for (int k : leftSelectedCells) {
						name += arrayOfFilesLeft.get(k).getName()
								+ "\n";
					}
				} else {
					for (int k : rightSelectedCells) {
						name += arrayOfFilesRight.get(k).getName()
								+ "\n";
					}
				}

			} else {
				if (leftSelectedCells.size() > 0) {
				} else {
					if (leftSelectedFile >= 0) {
						name = arrayOfFilesLeft.get(leftSelectedFile)
								.getName();
					} else {
						name = arrayOfFilesRight.get(rightSelectedFile)
								.getName();
					}
				}
			}
			dial = new DialogYesNo(framex, name);

		}
	}
	private void actionOnFiles(int row){
		if(leftSelectedFile>rightSelectedFile){
		if (pathLeft.length() > 0 && row == 0) {
			int numbers = 0;

			for (int i = pathLeft.length() - 2; i > 0; i--) {
				if (pathLeft.charAt(i) == '/'
						|| pathLeft.charAt(i) == '\\') {
					break;
				}
				numbers++;
			}
			pathLeft = pathLeft.substring(0, pathLeft.length()
					- numbers - 1);
			initializeDataNew();
			tableLeft.setModel(new MyTableModel(
					initializeData1(arrayOfFilesLeft)));
		} else if (arrayOfFilesLeft.get(row).isFile()) {
			try {
				Desktop.getDesktop().open(
						new File(pathLeft
								+ arrayOfFilesLeft.get(row)
										.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			pathLeft += "" + arrayOfFilesLeft.get(row).getName()
					+ "/";
			
		}

		fileNumberLeft.setText("" + numberOfFiles(listOfFilesLeft)
				+ " " + rb.getString("file") + " "
				+ numberOfDirectories(listOfFilesLeft) + " "
				+ rb.getString("dir"));
		leftPathDescription.setText("" + pathLeft + "*.*");
		leftReload();
	}
		else {

			if (pathRight.length() > 0 && row == 0) {
				int numbers = 0;

				for (int i = pathRight.length() - 2; i > 0; i--) {
					if (pathRight.charAt(i) == '/'
							|| pathRight.charAt(i) == '\\') {
						break;
					}
					numbers++;
				}
				pathRight = pathRight.substring(0, pathRight.length()
						- numbers - 1);
				initializeDataNew();
				tableRight.setModel(new MyTableModel(
						initializeData1(arrayOfFilesRight)));
			} else if (arrayOfFilesRight.get(row).isFile()) {
				try {
					Desktop.getDesktop().open(
							new File(pathRight
									+ arrayOfFilesRight.get(row)
											.getName()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {

				pathRight += "" + arrayOfFilesRight.get(row).getName()
						+ "/";
			}

			fileNumberRight.setText(""
					+ numberOfFiles(listOfFilesRight) + " "
					+ rb.getString("file") + " "
					+ numberOfDirectories(listOfFilesRight) + " "
					+ rb.getString("dir"));
			rightPathDescription.setText("" + pathRight + "*.*");
			rightReload();
		}
	}
	private void createNewFolder(){
		UIManager.put("OptionPane.okButtonText", cx.getBundle()
				.getString("ok"));
		UIManager.put("OptionPane.cancelButtonText", cx.getBundle()
				.getString("cancel"));
		newFolderName = JOptionPane.showInputDialog(framex, cx
				.getBundle().getString("newfoldername"), cx.getBundle()
				.getString("title"), JOptionPane.PLAIN_MESSAGE);
		if (newFolderName != null) {
			if (leftSelectedFile >= 0) {
				new File(pathLeft + newFolderName).mkdir();

			} else {
				new File(pathRight + newFolderName).mkdir();

			}
			rightReload();
			leftReload();
			fileNumberRight.setText(""
					+ numberOfFiles(listOfFilesRight) + " "
					+ rb.getString("file") + " "
					+ numberOfDirectories(listOfFilesRight) + " "
					+ rb.getString("dir"));
			fileNumberLeft.setText("" + numberOfFiles(listOfFilesLeft)
					+ " " + rb.getString("file") + " "
					+ numberOfDirectories(listOfFilesLeft) + " "
					+ rb.getString("dir"));
		}	
	}
	//--
	
	// THREADS
	private void delete() {

		new Thread() {
			public void run() {
				if (leftSelectedCells.size() > 1
						|| rightSelectedCells.size() > 1) {
					if (leftSelectedCells.size() > rightSelectedCells.size()) {
						for (int idx : leftSelectedCells) {
							if (arrayOfFilesLeft.get(idx).isFile()) {
								arrayOfFilesLeft.get(idx).delete();
							} else {
								try {
									deleteF(arrayOfFilesLeft.get(idx));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} else {
						for (int idx : rightSelectedCells) {
							if (arrayOfFilesRight.get(idx).isFile()) {
								arrayOfFilesRight.get(idx).delete();
							} else {
								try {
									deleteF(arrayOfFilesRight.get(idx));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

				} else {
					if (leftSelectedFile > rightSelectedFile) {
						if (arrayOfFilesLeft.get(leftSelectedFile).isFile()) {
							arrayOfFilesLeft.get(leftSelectedFile).delete();
						} else {
							try {
								deleteF(arrayOfFilesLeft.get(leftSelectedFile));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						if (arrayOfFilesRight.get(rightSelectedFile).isFile()) {
							arrayOfFilesRight.get(rightSelectedFile).delete();
						} else {
							try {
								deleteF(arrayOfFilesRight
										.get(rightSelectedFile));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						leftReload();
						rightReload();
						fileNumberLeft.setText(""
								+ numberOfFiles(listOfFilesLeft) + " "
								+ rb.getString("file") + " "
								+ numberOfDirectories(listOfFilesLeft) + " "
								+ rb.getString("dir"));
						fileNumberRight.setText(""
								+ numberOfFiles(listOfFilesRight) + " "
								+ rb.getString("file") + " "
								+ numberOfDirectories(listOfFilesRight) + " "
								+ rb.getString("dir"));
						leftSelectedCells.clear();
						rightSelectedCells.clear();
					}
				});

			}
		}.start();

	}
	//--
	
	// PROGRESS MONITOR
	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			Random random = new Random();
			int progress = 0;
			setProgress(0);
			try {
				Thread.sleep(1000);
				while (progress < 100 && !isCancelled()) {
					Thread.sleep(random.nextInt(1000));
					progress += random.nextInt(20);
					setProgress(Math.min(progress, 100));
				}
			} catch (InterruptedException ignore) {
			}
			return null;
		}

	}
	//--
	
	// DIALOGS
	private class DialogYesNo {
		public DialogYesNo(JFrame parent, String filename) {
			Object[] options = { cx.getBundle().getString("yes"),
					cx.getBundle().getString("no") };
			Object[] text = { cx.getBundle().getString("deletingconf"),
					cx.getBundle().getString("deletingtitle") };
			int n = JOptionPane.showOptionDialog(parent, text[0] + " \n"
					+ filename, (String) text[1], JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (n == JOptionPane.YES_OPTION) {
				if (pathLeft.length() > 3 && leftSelectedFile == 0) {
				} else {

					delete();

				}

			} else if (n == JOptionPane.NO_OPTION) {
				leftFreeSpace.setText("NO");

			}
		}
	}
	public class OverrideDialogs extends JDialog implements ActionListener, PropertyChangeListener{
		  public OverrideDialogs(JFrame parent, String title, String message) {
			    super(parent, title, true);
			    if (parent != null) {
			      Dimension parentSize = parent.getSize(); 
			      Point p = parent.getLocation(); 
			      setLocation(p.x + parentSize.width / 2, p.y + parentSize.height / 2);
			    }
			    JPanel messagePane = new JPanel();
			    messagePane.add(new JLabel(message));
			    getContentPane().add(messagePane);
			    JPanel buttonPane = new JPanel();
			    buttonOk = new JButton(cx.getBundle().getString("override"));
			    buttonCancel= new JButton(cx.getBundle().getString("cancel"));
			    buttonPane.add(buttonOk); 
			    buttonPane.add(buttonCancel);
			    buttonOk.addActionListener(this);
			    buttonCancel.addActionListener(this);
			    getContentPane().add(buttonPane, BorderLayout.SOUTH);
			    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			    pack(); 
			    setVisible(true);
			  }
			  public void actionPerformed(ActionEvent e) {
				  if (e.getSource() == buttonCancel){
			    setVisible(false); 
			    dispose(); 
				  }
				  else {
					  
				  File delete=null;
					  if(which.equals("left")){ for(File f: arrayOfFilesRight){ if(f.getName().equals(progressedFile.getName())) { delete=f;}}}
					  else { for(File f: arrayOfFilesLeft){ if(f.getName().equals(progressedFile.getName())){ delete=f;}}}
						
						progressMonitor = new ProgressMonitor(framex, "Running a Long Task",
								"", 0, 100);
						progressMonitor.setProgress(0);
						task = new Task();
						task.addPropertyChangeListener(this);
						task.execute(); 
					  
					  try {
						deleteF(delete);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					  leftReload(); rightReload();
					  setVisible(false); 
					    dispose(); 
				  }
			  }
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress" == evt.getPropertyName()) {
					int progress = (Integer) evt.getNewValue();
					progressMonitor.setProgress(progress);
					String message = String.format("Completed %d%%.\n", progress);
					progressMonitor.setNote(message);
					if (progressMonitor.isCanceled() || task.isDone()) {
						Toolkit.getDefaultToolkit().beep();
						if (progressMonitor.isCanceled()) {
							task.cancel(true);

						} else {

							new Thread() {
								public void run() {

									if (operation.equals("moveButton")) {
										if (which.equals("left")) {
											moveFilesOrDirectories(progressedFile, pathRight);
										} else {
											moveFilesOrDirectories(progressedFile, pathLeft);
										}
									} else {
										if (which.equals("left")) {
											copyFilesOrDirectories(progressedFile, pathRight);
										} else {
											copyFilesOrDirectories(progressedFile, pathLeft);
										}
									}

									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											leftReload();
											rightReload();
										}
									});
								}
							}.start();

						}

					}
				}

			}
			

			}
	// --
	
	//TABLE CONTENT
	@SuppressWarnings("serial")
	static class MyTableModel extends AbstractTableModel {
		Object[][] datka;

		MyTableModel(Object[][] data) {
			datka = data;
		}

		String[] columnNames = { cx.getBundle().getString("name"),
				cx.getBundle().getString("ext"),
				cx.getBundle().getString("size"),
				cx.getBundle().getString("date") };

		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return datka.length;
		}

		public Object getValueAt(int row, int column) {

			return datka[row][column];
		}

		public String getColumnName(int column) {

			return columnNames[column];
		}

		public Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}

		public void setValueAt(Object value, int row, int column) {
			datka[row][column] = value;
			fireTableCellUpdated(row, column);
		}

	}
	class IconTextCellRemderer extends DefaultTableCellRenderer {
		private ArrayList<File> pliki = null;
		IconTextCellRemderer(ArrayList<File> pliki, File[] which) {
			this.pliki = pliki;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			ImageIcon backA = new ImageIcon("icons/backarrow.png");
			for (int i = 0; i < pliki.size(); i++) {
				if (pliki.get(i).getName().equals("...")) {
					setText("[..]");
					setIcon(backA);
				} else if (row == i) {
					setText(pliki.get(i).getName());
					setIcon(FileSystemView.getFileSystemView().getSystemIcon(
							pliki.get(i)));
				}
			}

			return this;
		}
	}
	class DotBorder extends EmptyBorder {

		private final BasicStroke dashed = new BasicStroke(1.0f,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
				(new float[] { 1.0f }), 0.0f);
		private final Color dotColor = Color.black;

		public DotBorder(int top, int left, int bottom, int right) {
			super(top, left, bottom, right);
		}

		public EnumSet type = EnumSet.noneOf(Type.class);

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int w,
				int h) {
			Graphics2D g2 = (Graphics2D) g;
			g2.translate(x, y);
			g2.setPaint(dotColor);
			g2.setStroke(dashed);
			if (type.contains(Type.START)) {
				g2.drawLine(0, 0, 0, h);
			}
			if (type.contains(Type.END)) {
				g2.drawLine(w - 1, 0, w - 1, h);
			}
			if (c.getBounds().x % 2 == 0) {
				g2.drawLine(0, 0, w, 0);
				g2.drawLine(0, h - 1, w, h - 1);
			} else {
				g2.drawLine(1, 0, w, 0);
				g2.drawLine(1, h - 1, w, h - 1);
			}
			g2.translate(-x, -y);
		}
	}
	//--
	
	//CREATING TABLES
	private JComponent createTable() {
		initializeDataNew();
		tableLeft = new JTable(new MyTableModel(
				initializeData1(arrayOfFilesLeft))) {
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				final DotBorder dotBorder = new DotBorder(2, 2, 2, 2);

				if (isRowSelected(row)) {
					comp.setBackground(Color.white);
					comp.setForeground(Color.black);
					((JComponent) comp).setBorder(dotBorder);

				}
				return comp;
			}

		};
		tableLeft.setShowGrid(false);
		tableLeft.setFocusable(true);
		tableLeft.addKeyListener(this);
		tableLeft.getColumnModel().setColumnSelectionAllowed(true);
		tableLeft
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tableLeft.setDragEnabled(true);
		tcm = tableLeft.getColumnModel();
		tcm.getColumn(0).setCellRenderer(
				new IconTextCellRemderer(arrayOfFilesLeft, listOfFilesLeft));

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(tableLeft), BorderLayout.CENTER);

		tableLeft.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int col = tableLeft.columnAtPoint(e.getPoint());
				String name = tableLeft.getColumnName(col);
				if (col == 0) {
					whichSort = Sort.ALPHA;
					sortAlphA(listOfFilesLeft, arrayOfFilesLeft);

				} else if (col == 2) {
					if (whichSort == Sort.SIZEA) {
						whichSort = Sort.SIZEB;
						sortSizeD(listOfFilesLeft, arrayOfFilesLeft);
					} else {
						whichSort = Sort.SIZEA;
						sortSizeA(listOfFilesLeft, arrayOfFilesLeft);
					}
					tableLeft.setModel(new MyTableModel(
							initializeData1(arrayOfFilesLeft)));
					tcm.getColumn(0).setCellRenderer(
							new IconTextCellRemderer(arrayOfFilesLeft,
									listOfFilesLeft));
				}
			}
		});

		tableLeft.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Point pnt = evt.getPoint();
				int row = tableLeft.rowAtPoint(pnt);
				leftSelectedFile = row;
				rightSelectedFile = -1;
				rightSelectedCells.clear();
				tableRight.setModel(new MyTableModel(
						initializeData1(arrayOfFilesRight)));
				tcmRight.getColumn(0).setCellRenderer(
						new IconTextCellRemderer(arrayOfFilesRight,
								listOfFilesRight));

				if (evt.getClickCount() == 2) {
					actionOnFiles(row);

				} else {
					panelNorthS.setBackground(Color.GRAY);
					panelNorthSRight.setBackground(UIManager
							.getColor(copyButton));
					if ((evt.getModifiers() & InputEvent.CTRL_MASK) != 0) {
						leftSelectedCells.add(row);

					}

				}

			}
		});
		return panel;
	}
	private JComponent createTableRight() {
		initializeDataNew();
		tableRight = new JTable(new MyTableModel(
				initializeData1(arrayOfFilesRight))) {
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				final DotBorder dotBorder = new DotBorder(2, 2, 2, 2);
				if (isRowSelected(row)) {
					comp.setBackground(Color.white);
					comp.setForeground(Color.black);
					((JComponent) comp).setBorder(dotBorder);
				}
				return comp;
			}

		};
		tableRight.setShowGrid(false);
		tableRight.setFocusable(true);
		tableRight.addKeyListener(this);
		tableRight.getColumnModel().setColumnSelectionAllowed(true);
		tableRight
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tcmRight = tableRight.getColumnModel();
		tcmRight.getColumn(0).setCellRenderer(
				new IconTextCellRemderer(arrayOfFilesRight, listOfFilesRight));
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(tableRight), BorderLayout.CENTER);

		tableRight.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int col = tableRight.columnAtPoint(e.getPoint());
				String name = tableRight.getColumnName(col);
				if (col == 0) {
					whichSortRight = Sort.ALPHA;
					sortAlphA(listOfFilesRight, arrayOfFilesRight);
				} else if (col == 2) {
					if (whichSortRight == Sort.SIZEA) {
						whichSortRight = Sort.SIZEB;
						sortSizeD(listOfFilesRight, arrayOfFilesRight);
					} else {
						whichSortRight = Sort.SIZEA;
						sortSizeA(listOfFilesRight, arrayOfFilesRight);
					}
				}
				tableRight.setModel(new MyTableModel(
						initializeData1(arrayOfFilesRight)));
				tcmRight.getColumn(0).setCellRenderer(
						new IconTextCellRemderer(arrayOfFilesRight,
								listOfFilesRight));
			}
		});

		tableRight.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Point pnt = evt.getPoint();
				int row = tableRight.rowAtPoint(pnt);
				rightSelectedFile = row;
				leftSelectedFile = -1;
				leftSelectedCells.clear();
				tableLeft.setModel(new MyTableModel(
						initializeData1(arrayOfFilesLeft)));
				tcm.getColumn(0).setCellRenderer(
						new IconTextCellRemderer(arrayOfFilesLeft,
								listOfFilesLeft));
				if (evt.getClickCount() == 2) {
					actionOnFiles(row);

				} else {
					panelNorthSRight.setBackground(Color.GRAY);
					panelNorthS.setBackground(UIManager.getColor(copyButton));
					if ((evt.getModifiers() & InputEvent.CTRL_MASK) != 0) {

						rightFreeSpace.setText("" + leftSelectedCells.size());
						rightSelectedCells.add(row);

					}
				}
			}
		});

		return panel;
	}
	// --

	//CREATING PANELS
	private JComponent createNorthPanel() {
		final JPanel panel = new JPanel(new MyFlowLayout(0));
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createImageButton("icon"));
		panel.add(createSeparator());
		panel.add(createImageButton("icon"));
		return panel;
	}
	private JComponent createCenterPanel() {
		JButton buttonS = new JButton("ABC");
		JButton buttonB = new JButton("DBE");

		// podglad.setBorder(null);
		// podglad.setFocusPainted(false);
		/*
		 * buttonB.setContentAreaFilled(false); buttonB.setOpaque(true);
		 * buttonB.setBackground(Color.RED); buttonB.setForeground(Color.BLACK);
		 */

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				createCenterLeftPanel(), createCenterRightPanel());
		splitPane.setDividerLocation(150);
		Dimension minimumSize = new Dimension(200, 200);
		// buttonS.setMinimumSize(minimumSize);
		// buttonB.setMinimumSize(minimumSize);
		buttonS.setPreferredSize(minimumSize);
		// buttonB.setPreferredSize(minimumSize);
		// splitPane.setDividerLocation(100 + splitPane.getInsets().left);
		splitPane.setDividerLocation(0.5);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.5);
		splitPane.setBorder(null);

		return splitPane;
	}
	private JComponent createCenterLeftPanel() {
		JPanel centerLeftPanel = new JPanel(new BorderLayout());

		final JComboBox volumens = new JComboBox(roots);
		volumens.setSelectedIndex(1);

		String spaceDescription = "" + roots[index].getUsableSpace() + " k "
				+ rb.getString("of") + " " + roots[index].getTotalSpace()
				+ " k " + rb.getString("free");
		final JPanel panelNorth = new JPanel();
		final JPanel panelSouth = new JPanel();
		final JPanel panelNorthBig = new JPanel();
		panelNorthS = new JPanel();
		panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelSouth.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelNorthS.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelNorthBig.setLayout(new BoxLayout(panelNorthBig, BoxLayout.Y_AXIS));
		leftFreeSpace = new JLabel(spaceDescription);
		fileNumberLeft = new JLabel("" + numberOfFiles(listOfFilesLeft) + " "
				+ rb.getString("file") + " "
				+ numberOfDirectories(listOfFilesLeft) + " "
				+ rb.getString("dir"));
		leftPathDescription = new JLabel("" + pathLeft + "*.*");
		panelNorth.add(volumens);
		panelNorth.add(leftFreeSpace);
		panelSouth.add(fileNumberLeft);
		panelNorthS.add(leftPathDescription);
		panelNorthBig.add(panelNorth);
		panelNorthBig.add(panelNorthS);
		panelNorthS.setBackground(Color.GRAY);

		// LISTENERY
		volumens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				volumens.setBackground(Color.red);
				JComboBox cb = (JComboBox) e.getSource();
				index = cb.getSelectedIndex();
				String spaceDescription = ""
						+ roots[cb.getSelectedIndex()].getUsableSpace() + " k "
						+ rb.getString("of") + " "
						+ roots[cb.getSelectedIndex()].getTotalSpace() + " k "
						+ rb.getString("free");
				leftFreeSpace.setText(spaceDescription);
				Object selectedDrive = cb.getSelectedItem();
				// TABLE
				pathLeft = "" + selectedDrive;
				initializeDataNew();
				tableLeft.setModel(new MyTableModel(
						initializeData1(arrayOfFilesLeft)));
				fileNumberLeft.setText("" + numberOfFiles(listOfFilesLeft)
						+ " " + rb.getString("file") + " "
						+ numberOfDirectories(listOfFilesLeft) + " "
						+ rb.getString("dir"));
				leftPathDescription.setText("" + pathLeft + "*.*");
				tcm.getColumn(0).setCellRenderer(
						new IconTextCellRemderer(arrayOfFilesLeft,
								listOfFilesLeft));
				// --
			}
		});
		//

		centerLeftPanel.add(panelNorthBig, BorderLayout.NORTH);
		centerLeftPanel.add(createTable(), BorderLayout.CENTER);
		centerLeftPanel.add(panelSouth, BorderLayout.SOUTH);

		return centerLeftPanel;
	}
	private JComponent createCenterRightPanel() {
		JPanel centerRightPanel = new JPanel(new BorderLayout());

		final JComboBox volumensR = new JComboBox(roots);
		volumensR.setSelectedIndex(1);
		String spaceDescription = "" + roots[index].getUsableSpace() + " k "
				+ rb.getString("of") + " " + roots[index].getTotalSpace()
				+ " k " + rb.getString("free");
		final JPanel panelNorth = new JPanel();
		final JPanel panelSouth = new JPanel();
		final JPanel panelNorthBig = new JPanel();
		panelNorthSRight = new JPanel();
		panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelSouth.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelNorthSRight.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelNorthBig.setLayout(new BoxLayout(panelNorthBig, BoxLayout.Y_AXIS));
		rightFreeSpace = new JLabel(spaceDescription);
		fileNumberRight = new JLabel("" + numberOfFiles(listOfFilesRight) + " "
				+ rb.getString("file") + " "
				+ numberOfDirectories(listOfFilesRight) + " "
				+ rb.getString("dir"));
		rightPathDescription = new JLabel("" + pathRight + "*.*");
		panelNorth.add(volumensR);
		panelNorth.add(rightFreeSpace);
		panelSouth.add(fileNumberRight);
		panelNorthSRight.add(rightPathDescription);
		panelNorthBig.add(panelNorth);
		panelNorthBig.add(panelNorthSRight);

		// LISTENERY
		volumensR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				volumensR.setBackground(Color.red);
				JComboBox cb = (JComboBox) e.getSource();
				indexRight = cb.getSelectedIndex();
				String spaceDescription = ""
						+ roots[cb.getSelectedIndex()].getUsableSpace() + " k "
						+ rb.getString("of") + " "
						+ roots[cb.getSelectedIndex()].getTotalSpace() + " k "
						+ rb.getString("free");
				rightFreeSpace.setText(spaceDescription);
				Object selectedDrive = cb.getSelectedItem();
				// TABLE
				pathRight = "" + selectedDrive;
				initializeDataNew();
				tableRight.setModel(new MyTableModel(
						initializeData1(arrayOfFilesRight)));
				tcmRight.getColumn(0).setCellRenderer(
						new IconTextCellRemderer(arrayOfFilesRight,
								listOfFilesRight));
				fileNumberRight.setText("" + numberOfFiles(listOfFilesRight)
						+ " " + rb.getString("file") + " "
						+ numberOfDirectories(listOfFilesRight) + " "
						+ rb.getString("dir"));
				rightPathDescription.setText("" + pathRight + "*.*");
				// --
			}
		});
		//

		centerRightPanel.add(panelNorthBig, BorderLayout.NORTH);
		centerRightPanel.add(createTableRight(), BorderLayout.CENTER);
		centerRightPanel.add(panelSouth, BorderLayout.SOUTH);
		return centerRightPanel;
	}
	private JComponent createSouthPanel() {
		final GridLayout layout = new GridLayout(1, 7);
		layout.setHgap(0);
		layout.setVgap(5);

		final JPanel panel = new JPanel();
		panel.setLayout(layout);

		viewButton = createButton("view");
		editButton = createButton("edit");
		copyButton = createButton("copy");
		moveButton = createButton("move");
		newFolderButton = createButton("newfolder");
		deleteButton = createButton("delete");
		exitButton = createButton("exit");

		panel.add(viewButton);
		panel.add(editButton);
		panel.add(copyButton);
		panel.add(moveButton);
		panel.add(newFolderButton);
		panel.add(deleteButton);
		panel.add(exitButton);

		moveButton.addActionListener(this);
		copyButton.addActionListener(this);

		viewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			
			}
		});

		deleteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
		deleteFiles();
			}
		});

		newFolderButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				createNewFolder();

			}
		});

		return panel;
	}
	private static JMenuBar createMenu() {
		JMenuBar menuBar;
		final JMenu languageMenu, filesMenu, markMenu, commandsMenu, netMenu, showMenu, configMenu, startMenu, helpMenu;
		final JRadioButtonMenuItem eng, pl;
		final JMenuItem menuItem, menuItemTwo, menuItemThree, menuItemFour, menuItemFive;

		menuBar = new JMenuBar();
		languageMenu = new JMenu(cx.getBundle().getString("language"));
		filesMenu = new JMenu(cx.getBundle().getString("files"));
		markMenu = new JMenu(cx.getBundle().getString("mark"));
		commandsMenu = new JMenu(cx.getBundle().getString("commands"));
		netMenu = new JMenu(cx.getBundle().getString("net"));
		showMenu = new JMenu(cx.getBundle().getString("show"));
		configMenu = new JMenu(cx.getBundle().getString("configuration"));
		startMenu = new JMenu(cx.getBundle().getString("start"));
		helpMenu = new JMenu(cx.getBundle().getString("help"));
		languageMenu.setMnemonic(KeyEvent.VK_A);

		menuBar.add(filesMenu);
		menuBar.add(markMenu);
		menuBar.add(commandsMenu);
		menuBar.add(netMenu);
		menuBar.add(showMenu);
		menuBar.add(configMenu);
		menuBar.add(startMenu);
		menuBar.add(languageMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(helpMenu);

		languageMenu.addSeparator();

		ButtonGroup group = new ButtonGroup();
		eng = new JRadioButtonMenuItem(cx.getBundle().getString("eng"));
		eng.setSelected(true);
		eng.setMnemonic(KeyEvent.VK_R);
		group.add(eng);
		languageMenu.add(eng);
		pl = new JRadioButtonMenuItem(cx.getBundle().getString("pl"));
		pl.setMnemonic(KeyEvent.VK_0);
		group.add(pl);
		languageMenu.add(pl);

		menuItem = new JMenuItem(cx.getBundle().getString("pack"));
		filesMenu.add(menuItem);
		menuItemTwo = new JMenuItem(cx.getBundle().getString("unpack"),
				new ImageIcon("icons/backarrow.png"));
		filesMenu.add(menuItemTwo);
		menuItemThree = new JMenuItem(cx.getBundle().getString("print"));
		filesMenu.add(menuItemThree);
		filesMenu.addSeparator();
		menuItemFour = new JMenuItem(cx.getBundle().getString("combine"));
		filesMenu.add(menuItemFour);
		filesMenu.addSeparator();
		menuItemFive = new JMenuItem(cx.getBundle().getString("quit"),
				KeyEvent.VK_T);
		filesMenu.add(menuItemFive);

		eng.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				cx.setLocale(new Locale("en"));

			}
		});

		pl.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				cx.setLocale(new Locale("pl"));

			}
		});

		cx.addContextChangeListener(new ContextChangeListener() {

			public void contextChanged() {

				viewButton.setText(cx.getBundle().getString("view"));
				editButton.setText(cx.getBundle().getString("edit"));
				copyButton.setText(cx.getBundle().getString("copy"));
				moveButton.setText(cx.getBundle().getString("move"));
				newFolderButton.setText(cx.getBundle().getString("newfolder"));
				deleteButton.setText(cx.getBundle().getString("delete"));
				exitButton.setText(cx.getBundle().getString("exit"));
				tableLeft.setModel(new MyTableModel(
						initializeData1(arrayOfFilesLeft)));
				tableRight.setModel(new MyTableModel(
						initializeData1(arrayOfFilesRight)));
				fileNumberLeft.setText("" + numberOfFiles(listOfFilesLeft)
						+ " " + cx.getBundle().getString("file") + " "
						+ numberOfDirectories(listOfFilesLeft) + " "
						+ cx.getBundle().getString("dir"));
				fileNumberRight.setText("" + numberOfFiles(listOfFilesRight)
						+ " " + cx.getBundle().getString("file") + " "
						+ numberOfDirectories(listOfFilesRight) + " "
						+ cx.getBundle().getString("dir"));

				String spaceDescription = "" + roots[index].getUsableSpace()
						+ " k " + cx.getBundle().getString("of") + " "
						+ roots[index].getTotalSpace() + " k "
						+ cx.getBundle().getString("free");
				String spaceDescriptionRight = ""
						+ roots[indexRight].getUsableSpace() + " k "
						+ cx.getBundle().getString("of") + " "
						+ roots[indexRight].getTotalSpace() + " k "
						+ cx.getBundle().getString("free");
				leftFreeSpace.setText(spaceDescription);
				rightFreeSpace.setText(spaceDescriptionRight);

				languageMenu.setText(cx.getBundle().getString("language"));
				filesMenu.setText(cx.getBundle().getString("files"));
				markMenu.setText(cx.getBundle().getString("mark"));
				commandsMenu.setText(cx.getBundle().getString("commands"));
				netMenu.setText(cx.getBundle().getString("net"));
				showMenu.setText(cx.getBundle().getString("show"));
				configMenu.setText(cx.getBundle().getString("configuration"));
				startMenu.setText(cx.getBundle().getString("start"));
				helpMenu.setText(cx.getBundle().getString("help"));

				eng.setText(cx.getBundle().getString("eng"));
				pl.setText(cx.getBundle().getString("pl"));
				menuItem.setText(cx.getBundle().getString("pack"));
				menuItemTwo.setText(cx.getBundle().getString("unpack"));
				menuItemThree.setText(cx.getBundle().getString("print"));
				menuItemFour.setText(cx.getBundle().getString("combine"));
				menuItemFive.setText(cx.getBundle().getString("quit"));

			}

		});

		return menuBar;
	}
	//--
	
	//MAIN 
	private JComponent createMainPanel() {
		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
		mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);

		return mainPanel;
	}
	private static void createAndShowGUI() {
		final SampleLayoutApplication instance = new SampleLayoutApplication();

		final JFrame frame = new JFrame(
				"Total Commander- Swing Project- 111569");
		framex = frame;
		frame.getContentPane().add(instance.createMainPanel());
		frame.setJMenuBar(createMenu());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 600);
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		// locale = new Locale("en");
		cx.setLocale(locale);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	//--
	
	//ACTION && propertyChange
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message = String.format(cx.getBundle().getString("completed")+" %d%%.\n", progress);
			progressMonitor.setNote(message);
			if (progressMonitor.isCanceled() || task.isDone()) {
				Toolkit.getDefaultToolkit().beep();
				if (progressMonitor.isCanceled()) {
					task.cancel(true);

				} else {

					new Thread() {
						public void run() {

							if (operation.equals("moveButton")) {
								if (which.equals("left")) {
									moveFilesOrDirectories(progressedFile, pathRight);
								} else {
									moveFilesOrDirectories(progressedFile, pathLeft);
								}
							} else {
								if (which.equals("left")) {
									copyFilesOrDirectories(progressedFile, pathRight);
								} else {
									copyFilesOrDirectories(progressedFile, pathLeft);
								}
							}

							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									leftReload();
									rightReload();
								}
							});
						}
					}.start();

				}

			}
		}

	}
	public void actionPerformed(ActionEvent evt) {
		String overridingFiles="";
		if ((pathLeft.length() > 3 && leftSelectedFile == 0)
				|| (pathRight.length() > 3 && rightSelectedFile == 0)
				|| (leftSelectedFile < 0 && rightSelectedFile < 0)) {

			JOptionPane.showMessageDialog(framex, cx.getBundle()
					.getString("nofiles"),
					cx.getBundle().getString("warning"),
					JOptionPane.PLAIN_MESSAGE);
		} else {
		if (evt.getSource() == moveButton) {
			operation = "moveButton";
			if (leftSelectedFile > rightSelectedFile) {
				which = "left";
				progressedFile = arrayOfFilesLeft.get(leftSelectedFile);
				for(File f: arrayOfFilesRight){
					if(f.getName().equals(arrayOfFilesLeft.get(leftSelectedFile).getName())){ overridingFiles=f.getName();}
				}
				
			} else {
				which = "right";
				progressedFile = arrayOfFilesRight.get(rightSelectedFile);
				for(File f: arrayOfFilesLeft){
					if(f.getName().equals(arrayOfFilesRight.get(rightSelectedFile).getName())){ overridingFiles=f.getName();}
				}
			}
		} else {
			operation = "copyButton";
			if (leftSelectedFile > rightSelectedFile) {
				which = "left";
				progressedFile = arrayOfFilesLeft.get(leftSelectedFile);
				for(File f: arrayOfFilesRight){
					if(f.getName().equals(arrayOfFilesLeft.get(leftSelectedFile).getName())){ overridingFiles=f.getName();}
				}
				
			} else {
				which = "right";
				progressedFile = arrayOfFilesRight.get(rightSelectedFile);
				for(File f: arrayOfFilesLeft){
					if(f.getName().equals(arrayOfFilesRight.get(rightSelectedFile).getName())){ overridingFiles=f.getName();}
				}
			}
		}
	
		
		if(overridingFiles.length()>0){
			OverrideDialogs dlg = new OverrideDialogs(framex, cx.getBundle().getString("warning"), cx.getBundle().getString("overridebody")+" \n"+overridingFiles);
		}
		else{
		
		progressMonitor = new ProgressMonitor(framex, cx.getBundle().getString("longtask"),
				"", 0, 100);
		progressMonitor.setProgress(0);
		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
		}
		}
	}
	public void keyPressed(KeyEvent evt) {
		  if (evt.getKeyCode() == KeyEvent.VK_F8){
				deleteFiles();
		  }
		  if(evt.getKeyCode() == KeyEvent.VK_UP){
			  if(leftSelectedFile>rightSelectedFile){leftSelectedFile--;}
			  else{ rightSelectedFile--;}
		  }
		  if(evt.getKeyCode() == KeyEvent.VK_DOWN){
			  if(leftSelectedFile>rightSelectedFile){leftSelectedFile++;}
			  else{ rightSelectedFile++;}
		  }
		  if(evt.getKeyCode() == KeyEvent.VK_ENTER){
			if(leftSelectedFile>rightSelectedFile){
				actionOnFiles(leftSelectedFile);
			}
			else{
				actionOnFiles(rightSelectedFile);
			}
		  }
		  if(evt.getKeyCode() == KeyEvent.VK_F7){
			  createNewFolder();
		  }
		
	}
	public void keyReleased(KeyEvent arg0) {
		
		
	}
	public void keyTyped(KeyEvent arg0) {
		
		
	}
	//--
	


}
