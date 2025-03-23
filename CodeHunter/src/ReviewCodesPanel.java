import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ReviewCodesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

    private static final String HOST_EVALUATION_SERVER = "10.0.0.252",
    		HOST_LEMMATIZATION_SERVER = "10.0.0.253",
    		USER_DIR = "user.dir",
            FOLDER_PATH_TEXT = "Ingrese ruta de la carpeta con los códigos:",
            SELECT_FOLDER = "Seleccionar carpeta",
            START_REVIEW = "Iniciar revisión",
            START_VARIABLES_REVIEW = "Iniciar revisión de variables",
            GENERATE_EXCEL = "Generar excel",
            C = "C",
            CPP = "C++",
            MAINC = "main.c",
            MAINCPP = "main.cpp",
            WARNING = "Aviso",
            UNZIP_CONFIRMATION = " archivos zip detectados, ¿desea continuar con el envío?.",
    		TWO_SELECTED_LANGUAGES_WARNING = "Se seleccionó más de un lenguaje",
            NO_LANGUAGES_SELECTED_WARNING = "No se seleccionó ningún lenguaje.",
            ERROR = "Error",
            NON_EXISTENT_FOLDER_ERROR = "La ruta especificada no es una carpeta válida.",
            NON_EXISTENT_ZIP_FILES_ERROR = "No se encontraron archivos ZIP en la carpeta.",
            TEST_FILE_SELECTION_ERROR = "Ha ocurrido un error al seleccionar el archivo de tests: ",
    		CONFIRMATION = "Confirmación",
            INFORMATION = "Información",
            FINISHED_REVIEW_INFORMATION = "La revisión ha finalizado.",
            FINISHED_VARIABLES_REVIEW_INFORMATION = "La revisión de variables ha finalizado.";

    private static final String EMPTY_STRING = "",
    		EMPTY_SPACE_STRING = " ",
    		COMMA = ",",
    		LINE_BREAK = "\n",
    		WHITE_SPACE_PATTERN = "\\s+",
    		RESOURCES_DELIMITER = "¤",
			COMMAND_WORDS_DELIMITER = "°",
			CREATE_FOLDER_COMMAND = "CREATE_FOLDER",
    		SEND_FILE_COMMAND = "SEND_FILE",
    		START_PRELIMINARY_EVALUATION_COMMAND = "START_PRELIMINARY_EVALUATION",
    		START_EVALUATION_COMMAND = "START_EVALUATION",
    		START_VARIABLES_EVALUATION_COMMAND = "START_VARIABLES_EVALUATION";
    
    private static final int BORDER = 20,
    				ZERO = 0,
    				ONE = 1,
    				PORT_EVALUATION_SERVER = 12345,
    				PORT_LEMMATIZATION_SERVER = 12344;
    
    private static final String[] columnNames = {"NickName",
            "Calificación",
            "Faltas"};

    private static final Color MEDIUM_GRAY = new Color(50, 50, 50), WHITE = new Color(255, 255, 255);

    private JTextField textFieldFolderPath;
    private JLabel labelFolderPath;
    private JPanel panelLanguage;
    private JButton buttonSelectFolder, buttonStartReview, buttonStartVariablesReview, buttonGenerateExcel;
    private JCheckBox checkBoxC, checkBoxCPP;
    private DefaultTableModel modelTableEvaluation;
    private JTable tableEvaluation;
    private JScrollPane scrollPane;
    private GridBagConstraints gridBagConstraint;
    
    List<Student>studentsList = new ArrayList<>();
    List<String>lemmatizedDescription = new ArrayList<>();
    
    String selectedLanguage, mainFileName, projectCurrentDirectory, testDescription, studentsListString, hostFunctionalServer;
    int portFunctionalServer;
    
    public ReviewCodesPanel() {

    	projectCurrentDirectory = System.getProperty(USER_DIR);
    	
        textFieldFolderPath = new JTextField();

        labelFolderPath = new JLabel(FOLDER_PATH_TEXT);
        labelFolderPath.setForeground(WHITE);

        panelLanguage = new JPanel(new GridLayout(ONE, 3));

        buttonSelectFolder = new JButton(SELECT_FOLDER);
        buttonStartReview = new JButton(START_REVIEW);
        buttonStartReview.setEnabled(false);
        buttonStartVariablesReview = new JButton(START_VARIABLES_REVIEW);
        buttonStartVariablesReview.setEnabled(false);
        buttonGenerateExcel = new JButton(GENERATE_EXCEL);
        buttonGenerateExcel.setEnabled(false);

        checkBoxC = new JCheckBox(C);
        checkBoxC.setBackground(MEDIUM_GRAY);
        checkBoxC.setForeground(WHITE);
        
        checkBoxCPP = new JCheckBox(CPP);
        checkBoxCPP.setBackground(MEDIUM_GRAY);
        checkBoxCPP.setForeground(WHITE);

        modelTableEvaluation = new DefaultTableModel();
        modelTableEvaluation.addColumn(columnNames[ZERO]);
        modelTableEvaluation.addColumn(columnNames[ONE]);
        modelTableEvaluation.addColumn(columnNames[2]);
        
        tableEvaluation = new JTable(modelTableEvaluation);

        scrollPane = new JScrollPane(tableEvaluation);
        tableEvaluation.setFillsViewportHeight(true);

        setLayout(new GridBagLayout());
        setBackground(MEDIUM_GRAY);
        setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));

        gridBagConstraint = new GridBagConstraints();

        //labelFolderPath
        gridBagConstraint.gridx = ZERO;
        gridBagConstraint.gridy = ZERO;
        gridBagConstraint.gridwidth = 2;
        gridBagConstraint.fill = GridBagConstraints.HORIZONTAL;
        add(labelFolderPath, gridBagConstraint);

        //emptySpace
        gridBagConstraint.gridy = ONE;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //textFieldFolderPath
        gridBagConstraint.gridy = 2;
        add(textFieldFolderPath, gridBagConstraint);
        
        //emptySpace
        gridBagConstraint.gridy = 3;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //panelLanguage
        gridBagConstraint.gridy = 4;
        panelLanguage.add(checkBoxC);
        panelLanguage.add(new JLabel(EMPTY_SPACE_STRING));
        panelLanguage.add(checkBoxCPP);
        panelLanguage.setBackground(MEDIUM_GRAY);
        add(panelLanguage, gridBagConstraint);

        //emptySpace
        gridBagConstraint.gridy = 5;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //buttonSelectFolder
        gridBagConstraint.gridy = 6;
        gridBagConstraint.gridwidth = 2;
        add(buttonSelectFolder, gridBagConstraint);
        
        //emptySpace
        gridBagConstraint.gridy = 7;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //buttonStartReview
        gridBagConstraint.gridy = 8;
        add(buttonStartReview, gridBagConstraint);

        //emptySpace
        gridBagConstraint.gridy = 9;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //buttonStartReview
        gridBagConstraint.gridy = 10;
        add(buttonStartVariablesReview, gridBagConstraint);

        //emptySpace
        gridBagConstraint.gridy = 11;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //buttonStartReview
        gridBagConstraint.gridy = 12;
        add(buttonGenerateExcel, gridBagConstraint);

        //emptySpace
        gridBagConstraint.gridy = 13;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);

        //scrollPane
        gridBagConstraint.gridy = 14;
        gridBagConstraint.fill = GridBagConstraints.BOTH;
        gridBagConstraint.weightx = ONE;
        gridBagConstraint.weighty = ONE;
        add(scrollPane, gridBagConstraint);
        
        buttonSelectFolder.addActionListener(new ButtonsController());
		buttonStartReview.addActionListener(new ButtonsController());
		buttonStartVariablesReview.addActionListener(new ButtonsController());
		buttonGenerateExcel.addActionListener(new ButtonsController());
    }
    
    private class ButtonsController implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String comando = e.getActionCommand();

			switch(comando) {
			case SELECT_FOLDER:
				if(checkBoxCPP.isSelected() && checkBoxC.isSelected()) {
					JOptionPane.showMessageDialog(null, TWO_SELECTED_LANGUAGES_WARNING, WARNING, JOptionPane.WARNING_MESSAGE);
				}
				else if(!checkBoxCPP.isSelected() && !checkBoxC.isSelected()) {
					JOptionPane.showMessageDialog(null, NO_LANGUAGES_SELECTED_WARNING, WARNING, JOptionPane.WARNING_MESSAGE);
				}
				else if(checkBoxC.isSelected()) {
					selectedLanguage = C;
					mainFileName = MAINC;
					checkBoxC.setEnabled(false);
					checkBoxCPP.setEnabled(false);
					
					try {
						if(getZipFiles(textFieldFolderPath.getText())) {
							studentsListString = sendCommand(START_PRELIMINARY_EVALUATION_COMMAND+COMMAND_WORDS_DELIMITER+mainFileName, hostFunctionalServer, portFunctionalServer);
							
							studentsList = stringToStudents(studentsListString);
							
							fillTable();
						}
						else {
							checkBoxC.setEnabled(true);
							checkBoxCPP.setEnabled(true);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if(checkBoxCPP.isSelected()) {
					selectedLanguage = CPP;
					mainFileName = MAINCPP;
					checkBoxC.setEnabled(false);
					checkBoxCPP.setEnabled(false);
					
					try {
						if(getZipFiles(textFieldFolderPath.getText())) {
							studentsListString = sendCommand(START_PRELIMINARY_EVALUATION_COMMAND+COMMAND_WORDS_DELIMITER+mainFileName, hostFunctionalServer, portFunctionalServer);
							
							studentsList = stringToStudents(studentsListString);
							
							fillTable();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				break;
				
			case START_REVIEW:
				studentsListString = startReview();
				
				studentsList = stringToStudents(studentsListString);
				
				fillTable();
				
		    	checkBoxC.setEnabled(true);
		    	checkBoxCPP.setEnabled(true);
		    	buttonStartReview.setEnabled(false);
		    	buttonStartVariablesReview.setEnabled(true);
		    	
		    	JOptionPane.showMessageDialog(null, FINISHED_REVIEW_INFORMATION, INFORMATION, JOptionPane.INFORMATION_MESSAGE);
				break;
				
			case START_VARIABLES_REVIEW:
				String reply = sendCommand(START_VARIABLES_EVALUATION_COMMAND+COMMAND_WORDS_DELIMITER+testDescription+COMMAND_WORDS_DELIMITER+studentsListString, HOST_LEMMATIZATION_SERVER, PORT_LEMMATIZATION_SERVER);
				if(reply == null) {
					reply = sendCommand(START_VARIABLES_EVALUATION_COMMAND+COMMAND_WORDS_DELIMITER+testDescription+COMMAND_WORDS_DELIMITER+studentsListString, HOST_EVALUATION_SERVER, PORT_EVALUATION_SERVER);
					
					if(reply != null) {
						studentsListString = reply;
						
						studentsList = stringToStudents(studentsListString);
						
						buttonGenerateExcel.setEnabled(true);
						
						fillTable();
						JOptionPane.showMessageDialog(null, FINISHED_VARIABLES_REVIEW_INFORMATION, INFORMATION, JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else {
					studentsListString = reply;
					
					studentsList = stringToStudents(studentsListString);
					
					buttonGenerateExcel.setEnabled(true);
					
					fillTable();
					JOptionPane.showMessageDialog(null, FINISHED_VARIABLES_REVIEW_INFORMATION, INFORMATION, JOptionPane.INFORMATION_MESSAGE);
				}
				break;
			case GENERATE_EXCEL:
				saveCSVFile();
				break;
			}
		}
	}
    
    private Boolean getZipFiles(String folderPath) throws IOException {
    	folderPath = folderPath.replaceAll(WHITE_SPACE_PATTERN, EMPTY_STRING);
    	
    	File folder = new File(folderPath);

    	int answer;
    	
        if (!folder.exists() || !folder.isDirectory()) {
            JOptionPane.showMessageDialog(null, NON_EXISTENT_FOLDER_ERROR, ERROR, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else {
        	File[] zipFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));

            if (zipFiles == null || zipFiles.length == 0) {
            	JOptionPane.showMessageDialog(null, NON_EXISTENT_ZIP_FILES_ERROR, ERROR, JOptionPane.ERROR_MESSAGE);
            	return false;
            } 
            else {
            	String reply = sendCommand(CREATE_FOLDER_COMMAND, HOST_EVALUATION_SERVER, PORT_EVALUATION_SERVER);
            	
            	if(reply == null) {
            		sendCommand(CREATE_FOLDER_COMMAND, HOST_LEMMATIZATION_SERVER, PORT_LEMMATIZATION_SERVER);
            		
            		hostFunctionalServer = HOST_LEMMATIZATION_SERVER;
            		portFunctionalServer = PORT_LEMMATIZATION_SERVER;
            	}
            	else {

            		hostFunctionalServer = HOST_EVALUATION_SERVER;
            		portFunctionalServer = PORT_EVALUATION_SERVER;
            	}
                
                answer = JOptionPane.showConfirmDialog(null, zipFiles.length + UNZIP_CONFIRMATION, CONFIRMATION, JOptionPane.YES_NO_OPTION);

    	        if (answer == JOptionPane.YES_OPTION) {
    	        	for (File file : zipFiles) {
    	                System.out.println(file.getAbsolutePath());
    	                
    	                sendZipFile(file.getAbsolutePath(), file.getName());
    	            }
    	        	return true;
    	        } 
    	        else {
    	        	return false;
    	        }
            }
        }
    }
    
    private void sendZipFile(String filePath, String fileName) throws IOException {
    	String fileInString = zipToString(filePath);
    	
    	System.out.println(sendCommand(SEND_FILE_COMMAND+COMMAND_WORDS_DELIMITER+fileInString+"°"+fileName, hostFunctionalServer, portFunctionalServer));
    }
    
    public static String zipToString(String zipFilePath) throws IOException {
        byte[] zipBytes = Files.readAllBytes(Paths.get(zipFilePath));
        return Base64.getEncoder().encodeToString(zipBytes);
    }
    
    private String sendCommand(String command, String host, int port) {

        Client client = new Client(host, port);
        try {
            client.connect();

            // Ejemplo de interacción
            client.sendCommand(command);
            String reply = client.getReply();
            System.out.println("Respuesta del servidor: " + reply);
            
            client.closeConnection();
            
            return reply;
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
            
            checkBoxC.setEnabled(true);
	    	checkBoxCPP.setEnabled(true);
	    	buttonStartReview.setEnabled(false);
	    	buttonStartVariablesReview.setEnabled(false);
            return null;
        }
    }
    
    private void fillTable() {
    	modelTableEvaluation.setRowCount(ZERO);
    	
    	if(studentsList != null) {
    		for(Student student: studentsList) {
        		modelTableEvaluation.addRow(new Object[]{student.getNickName(), student.getScore(), student.getFaults()});
        	}
    		
    		buttonStartReview.setEnabled(true);
    	}
    	else {
    		checkBoxC.setEnabled(true);
			checkBoxCPP.setEnabled(true);
    	}
    }
    
    private String startReview() {
    	String testContent = getTestFile();
    	
    	if(testContent != null) {
    		testDescription = testContent.split(RESOURCES_DELIMITER, 2)[0];
    		
    		return sendCommand(START_EVALUATION_COMMAND+COMMAND_WORDS_DELIMITER+testContent+COMMAND_WORDS_DELIMITER+selectedLanguage+COMMAND_WORDS_DELIMITER+studentsListString, hostFunctionalServer, portFunctionalServer);
    	}
    	return null;
    }
    
    private String getTestFile() {
    	String fileContent = EMPTY_STRING;
    	JFileChooser fileChooser = new JFileChooser();
    	File selectedFile;

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            
            try {
                fileContent = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                return fileContent.substring(0, fileContent.length()-1);
            } 
            catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, TEST_FILE_SELECTION_ERROR  + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        else {
        	return null;
        }
    }
    
    private void saveCSVFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as...");
        fileChooser.setSelectedFile(new File("file.csv"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            generateCSV(fileToSave.getAbsolutePath());
        }
    }

    private void generateCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
        	writer.append("Nickname del estudiante,Calificación,Faltas\n");
        	
        	if(studentsList != null) {
        		for(Student student: studentsList) {
            		writer.append(student.getNickName()+COMMA+student.getScore()+COMMA+student.getFaults()+LINE_BREAK);
            	}
        	}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String studentsToString(List<Student> students) {
    	Gson gson = new Gson();
        return gson.toJson(students);
    }

    public static List<Student> stringToStudents(String data) {
    	Gson gson = new Gson();
        return gson.fromJson(data, new TypeToken<List<Student>>(){}.getType());
    }
}