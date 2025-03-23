import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class GenerateTestFilePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private static final String SAVE_TEST_FILE = "Guardar archivo de pruebas",
			LOAD_TEST_FILE = "Cargar archivo de pruebas",
			NEW_TEST_FILE = "Nuevo archivo de pruebas",
			DELETE_TEST = "Eliminar prueba",
			NEW_TEST = "Nueva prueba",
			SEEK_ORDER = "Busca orden",
			INPUTS = "Inputs: ",
			OUTPUTS = "Outputs: ",
			TEST_ID_TEXT = "Id de la prueba a eliminar: ",
			CURRENT_TEST_ID_TEXT = "Id actual: ",
			TEST_DESCRIPTION_TEXT = "Descripción del programa: ",
			WARNING = "Aviso",
    		INVALID_ID_WARNING = "Se ingresó un id no válido.",
    		CH_TEST_FILES = "Archivos de test CodeHunter (*.pch)",
    		TEST_FILES_FORMAT_NAME = "pch",
			TEST_FILES_FORMAT = ".pch";
	
	private static final String EQUAL_SIGN = "=",
			EMPTY_STRING = "",
			EMPTY_SPACE_STRING = " ",
			SEEK_ORDER_SIGN = "=O",
			ELEMENT_DELIMITER = "¥",
			EQUAL_DELIMITER = "\\=",
			TEST_DELIMITER = "¤";
	
	private static final String[] columnNamesInputs = {"Num de test",
            "Entrada"},
			columnNamesOutputs = {"Num de test",
    		"Salida"};
	
	private static final int BORDER = 20,
			ZERO = 0;
			
	private final Color MEDIUM_GRAY = new Color(50, 50, 50), WHITE = new Color(255, 255, 255);
	
	private JButton buttonSaveTestFile,  buttonLoadTestFile, buttonNewTestFile, buttonDeleteTest, buttonNewTest;
	private JCheckBox checkBoxSeekOrder;
	private JTextArea textAreaTestDescription;
	private JTextField textFieldTestId, textFieldInputs, textFieldOutputs;
	private JLabel labelTestId, labelInputs, labelOutputs, labelCurrentTestId, labelTestDescription;
	private JPanel panelControls, panelTables;
	private DefaultTableModel modelTableInputs, modelTableOutputs;
	private JTable tableInputs, tableOutputs;
	private JScrollPane scrollPaneInputs, scrollPaneOutputs,scrollPaneTextAreaTestDescription;
	private GridBagConstraints gridBagConstraint;
	
	private Test currentTest = null;
	
	private List<Test> currentTests = new ArrayList<>();
	
	private int idTest = 0;
	
	public GenerateTestFilePanel() {
		
		textFieldTestId = new JTextField();
		textFieldInputs = new JTextField();
		textFieldOutputs = new JTextField();
		
		checkBoxSeekOrder = new JCheckBox(SEEK_ORDER);
        checkBoxSeekOrder.setBackground(MEDIUM_GRAY);
        checkBoxSeekOrder.setForeground(WHITE);
		
		labelTestId = new JLabel(TEST_ID_TEXT);
		labelTestId.setForeground(WHITE);
		
		labelInputs = new JLabel(INPUTS);
		labelInputs.setForeground(WHITE);
		
		labelOutputs = new JLabel(OUTPUTS);
		labelOutputs.setForeground(WHITE);
		
		labelTestDescription = new JLabel(TEST_DESCRIPTION_TEXT);
		labelTestDescription.setForeground(WHITE);
		
		labelCurrentTestId = new JLabel(CURRENT_TEST_ID_TEXT + ZERO);
		labelCurrentTestId.setForeground(WHITE);
		labelCurrentTestId.setHorizontalAlignment(SwingConstants.CENTER);
		labelCurrentTestId.setVerticalAlignment(SwingConstants.CENTER);
        
		panelControls = new JPanel(new GridLayout(7, 5));
		panelControls.setBackground(MEDIUM_GRAY);
		panelTables = new JPanel(new GridLayout(1, 3));
		panelTables.setBackground(MEDIUM_GRAY);
		
		buttonSaveTestFile = new JButton(SAVE_TEST_FILE);
		buttonLoadTestFile = new JButton(LOAD_TEST_FILE);
		buttonNewTestFile = new JButton(NEW_TEST_FILE);
		buttonDeleteTest = new JButton(DELETE_TEST);
		buttonNewTest = new JButton(NEW_TEST);
		
		buttonNewTest.setEnabled(false);
		
		modelTableInputs = new DefaultTableModel();
		modelTableInputs.addColumn(columnNamesInputs[0]);
		modelTableInputs.addColumn(columnNamesInputs[1]);
		
		modelTableOutputs = new DefaultTableModel();
		modelTableOutputs.addColumn(columnNamesOutputs[0]);
		modelTableOutputs.addColumn(columnNamesOutputs[1]);
        
        tableInputs = new JTable(modelTableInputs);
        tableOutputs = new JTable(modelTableOutputs);
        
        scrollPaneInputs = new JScrollPane(tableInputs);
        scrollPaneOutputs = new JScrollPane(tableOutputs);
        
        textAreaTestDescription = new JTextArea(5, 20); 
        textAreaTestDescription.setLineWrap(true); 
        textAreaTestDescription.setWrapStyleWord(true); 

        scrollPaneTextAreaTestDescription = new JScrollPane(textAreaTestDescription);
        
        tableInputs.setFillsViewportHeight(true);
        tableOutputs.setFillsViewportHeight(true);
        
		setLayout(new GridBagLayout());
		setBackground(MEDIUM_GRAY);
		setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		
		gridBagConstraint = new GridBagConstraints();

		//panelControls
		gridBagConstraint.gridx = 0;
        gridBagConstraint.gridy = 0;
        gridBagConstraint.gridwidth = 2;
        gridBagConstraint.fill = GridBagConstraints.HORIZONTAL;
        panelControls.add(buttonSaveTestFile);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(buttonLoadTestFile);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(buttonNewTestFile);
        
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        
        panelControls.add(labelTestId);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(textFieldTestId);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(buttonDeleteTest);
        
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        
        panelControls.add(labelInputs);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(buttonNewTest);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(labelOutputs);
        
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        
        panelControls.add(textFieldInputs);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(checkBoxSeekOrder);
        panelControls.add(new JLabel(EMPTY_SPACE_STRING));
        panelControls.add(textFieldOutputs);
        add(panelControls, gridBagConstraint);
        
        //emptySpace
        gridBagConstraint.gridy = 1;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //panelTables
        gridBagConstraint.gridy = 2;
        gridBagConstraint.gridwidth = 2;
        gridBagConstraint.fill = GridBagConstraints.BOTH;
        gridBagConstraint.weightx = 1.0;
        gridBagConstraint.weighty = 1.0;
        panelTables.add(scrollPaneInputs);
        panelTables.add(labelCurrentTestId);
        panelTables.add(scrollPaneOutputs);
        add(panelTables, gridBagConstraint);
        
        // emptySpace
        gridBagConstraint.gridy = 3;
        gridBagConstraint.weighty = 0.0;
        gridBagConstraint.fill = GridBagConstraints.NONE;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);
        
        //labelTestDescription
        gridBagConstraint.gridy = 4;
        add(labelTestDescription, gridBagConstraint);
        
        //emptySpace
        gridBagConstraint.gridy = 5;
        add(new JLabel(EMPTY_SPACE_STRING), gridBagConstraint);

        //textAreaTestDescription
        gridBagConstraint.gridy = 6;
        gridBagConstraint.fill = GridBagConstraints.BOTH;
        gridBagConstraint.weightx = 1.0;
        gridBagConstraint.weighty = 0.5;
        add(scrollPaneTextAreaTestDescription, gridBagConstraint);
        
        buttonSaveTestFile.addActionListener(new ButtonsController());
		buttonLoadTestFile.addActionListener(new ButtonsController());
		buttonNewTestFile.addActionListener(new ButtonsController());
		buttonDeleteTest.addActionListener(new ButtonsController());
		buttonNewTest.addActionListener(new ButtonsController());
		
		textFieldInputs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textEnteredInputs = textFieldInputs.getText();

                String[] lines = textEnteredInputs.split(EMPTY_SPACE_STRING);
                
                checkBoxSeekOrder.setEnabled(false);
                
                buttonNewTest.setEnabled(true);
                
                if(currentTest == null)
                {
                	currentTest = new Test(idTest);
                	
                	if(checkBoxSeekOrder.isSelected()) {
                		currentTest.setSeekOrder(true);
                	}
                }
                
                for(String line : lines)
                {
                	if(line.equals(EQUAL_SIGN)) {
                		currentTest.getInputs().add(EQUAL_DELIMITER);
                	}
                	else {
                		currentTest.getInputs().add(line);
                	}
                }
                
                fillTableInputs();
                	
                textFieldInputs.setText(EMPTY_STRING);
            }
        });
		
		textFieldOutputs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String textEnteredOutputs = textFieldOutputs.getText();

                String[] lines = textEnteredOutputs.split(EMPTY_SPACE_STRING);
                
                checkBoxSeekOrder.setEnabled(false);
                
                buttonNewTest.setEnabled(true);
                
                if(currentTest == null)
                {
                	currentTest = new Test(idTest);
                	
                	if(checkBoxSeekOrder.isSelected()) {
                		currentTest.setSeekOrder(true);
                	}
                }
                
                for(String line : lines)
                {
                	if(line.equals(EQUAL_SIGN)) {
                		currentTest.getOutputs().add(EQUAL_DELIMITER);
                	}
                	else {
                		currentTest.getOutputs().add(line);
                	}
                }
                
                fillTableOutputs();
                
                textFieldOutputs.setText(EMPTY_STRING);
            }
        });
	}
	
	private class ButtonsController implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String comando = e.getActionCommand();

			switch(comando) {
			case SAVE_TEST_FILE:
				
				if(currentTest != null) {
					currentTests.add(currentTest);
					currentTest = null;
				}

				checkBoxSeekOrder.setEnabled(true);
				checkBoxSeekOrder.setSelected(false);
				
				String content = currentTestsToString();
				
				saveTestFile(content);
				
                currentTests = new ArrayList<>();

				textFieldInputs.setText(EMPTY_STRING);
				textFieldOutputs.setText(EMPTY_STRING);
				modelTableInputs.setRowCount(ZERO);
				modelTableOutputs.setRowCount(ZERO);
				textAreaTestDescription.setText(EMPTY_STRING);
				break;
				
			case LOAD_TEST_FILE:	
				String testContent = getTestFile();
				String[] lines;
				String[] linesWithoutDescription;
				String testDescription;
				
				if(testContent != null) {
					lines = testContent.split(TEST_DELIMITER);

					testDescription = lines[ZERO];
					
					textAreaTestDescription.setText(testDescription);
		        	
		        	linesWithoutDescription = new String[lines.length - 1];

		            System.arraycopy(lines, 1, linesWithoutDescription, 0, lines.length - 1);
		            
				    currentTests = getTests(linesWithoutDescription);
				    
				    fillTableInputs();
				    fillTableOutputs();
				}
				break;
			
			case NEW_TEST_FILE:	
				currentTests = new ArrayList<>();
				currentTest = null;
				checkBoxSeekOrder.setEnabled(true);
				checkBoxSeekOrder.setSelected(false);

				textFieldInputs.setText(EMPTY_STRING);
				textFieldOutputs.setText(EMPTY_STRING);
				modelTableInputs.setRowCount(ZERO);
				modelTableOutputs.setRowCount(ZERO);
				textAreaTestDescription.setText(EMPTY_STRING);
				break;
				
			case DELETE_TEST:	
				if(textFieldTestId.getText().equals(EMPTY_STRING)) {
					JOptionPane.showMessageDialog(null, INVALID_ID_WARNING, WARNING, JOptionPane.WARNING_MESSAGE);
				}
				else {
					int idEliminar = Integer.parseInt(textFieldTestId.getText());	
					
					if(idEliminar == currentTests.size()) {
						currentTest = null;
						fillTableInputs();
						fillTableOutputs();
					}
					else {
						if(idEliminar <= currentTests.size()-1) {
							currentTests.remove(idEliminar);
							
							textFieldInputs.setText(EMPTY_STRING);
							textFieldOutputs.setText(EMPTY_STRING);
							modelTableInputs.setRowCount(ZERO);
							modelTableOutputs.setRowCount(ZERO);
							
							fillTableInputs();
							fillTableOutputs();
						}
					}
				}
				
				break;
				
			case NEW_TEST:	
				buttonNewTest.setEnabled(false);
				
				currentTests.add(currentTest);
				currentTest = null;
				
				idTest++;
				
				labelCurrentTestId.setText(CURRENT_TEST_ID_TEXT + currentTests.size()); 
				
				checkBoxSeekOrder.setEnabled(true);
				checkBoxSeekOrder.setSelected(false);
				break;				
			}
		}
	}
	
	private void fillTableInputs() {
    	modelTableInputs.setRowCount(ZERO);
    	
    	for(Test test: currentTests) {
    		for(String input: test.getInputs()) {
    			modelTableInputs.addRow(new Object[]{test.getId(), input});
    		}
    	}
    	
    	if(currentTest != null) {
    		for(String input: currentTest.getInputs()) {
    			modelTableInputs.addRow(new Object[]{currentTest.getId(), input});
    		}
    	}
    }
	
	private void fillTableOutputs() {
    	modelTableOutputs.setRowCount(ZERO);
    	
    	for(Test test: currentTests) {
    		for(String output: test.getOutputs()) {
    			modelTableOutputs.addRow(new Object[]{test.getId(), output});
    		}
    	}
    	
    	if(currentTest != null) {
	    	for(String output: currentTest.getOutputs()) {
				modelTableOutputs.addRow(new Object[]{currentTest.getId(), output});
			}
    	}
    }
	
	private static String getTestFile() 
	{
		String fullPath, line;
		
        JFrame frame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        
        BufferedReader bufferedReader;
        StringBuilder content;
        
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
            	fullPath = fileChooser.getSelectedFile().getAbsolutePath();;
                
                bufferedReader = new BufferedReader(new FileReader(fullPath));
                content = new StringBuilder();
                
                while ((line = bufferedReader.readLine()) != null) 
                {
                	content.append(line).append(ELEMENT_DELIMITER);
                }
                bufferedReader.close();
                
                return content.toString();
            } 
            
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } 
        
        else {
            return null;
        }
    }
	
	private List<Test> getTests(String[] lineas)
	{
		boolean isOutput = false;
		
	    int idTest = ZERO;
	    
	    String[]testLines;
	    
	    Test test = new Test(ZERO);
	    
	    List<String> inputs = new ArrayList<String>();
	    List<String> outputs = new ArrayList<String>();
	    List<Test> tests = new ArrayList<Test>();
	    
		for (String linea : lineas) {
	    	testLines = linea.split(ELEMENT_DELIMITER);
	    	test = new Test(idTest);
	    	isOutput = false;
	    	inputs = new ArrayList<String>();
		    outputs = new ArrayList<String>();
	    	
	    	for (String line : testLines) {
	    		if(line.equals(EQUAL_SIGN)) {
	    			isOutput = true;
	    		}
	    		
	    		else if(line.equals(SEEK_ORDER_SIGN)) {
	    			isOutput = true;
	    			test.setSeekOrder(true);
	    		}
	    		
	    		else {
	    			if(!line.equals(EMPTY_STRING)) {
		    			if(isOutput) {
		    				if(line.equals(EQUAL_DELIMITER)) {
		    					outputs.add(EQUAL_SIGN);
		    				}
		    				else {
		    					outputs.add(line);
		    				}
		    			}
		    			else {
		    				if(line.equals(EQUAL_DELIMITER)) {
		    					inputs.add(EQUAL_SIGN);
		    				}
		    				else {
		    					inputs.add(line);
		    				}
		    			}
	    			}
	    		}
	        }
	    	
	    	if(inputs.size()>ZERO || outputs.size()>ZERO) {
		    	test.setInputs(inputs);
				test.setOutputs(outputs);
				tests.add(test);
				
				idTest++;
	    	} 	
		}
		
		return tests;
	}
	
	private String currentTestsToString() {
		String content = textAreaTestDescription.getText()+TEST_DELIMITER;

		for(Test test : currentTests)
		{
			for(String entrada : test.getInputs())
			{
				content = content+entrada+ELEMENT_DELIMITER;
			}
			
			if(test.getSeekOrder() == true) {
				content = content+SEEK_ORDER_SIGN+ELEMENT_DELIMITER;
			}
			else {
				content = content+EQUAL_SIGN+ELEMENT_DELIMITER;
			}

			for(String salida : test.getOutputs())
			{
				content = content+salida+ELEMENT_DELIMITER;
			}
			content = content.substring(ZERO, content.length()-1)+TEST_DELIMITER;
		}
		
		return content;
	}
	
	private static void saveTestFile(String content) {
		String filePath;
		
		JFileChooser fileChooser = new JFileChooser();
		BufferedWriter bufferedWriter;
		FileNameExtensionFilter filter = new FileNameExtensionFilter(CH_TEST_FILES, TEST_FILES_FORMAT_NAME);
		
        fileChooser.setFileFilter(filter);
        
        int seleccion = fileChooser.showSaveDialog(null);
        
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            try {
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                
                if (!filePath.toLowerCase().endsWith(TEST_FILES_FORMAT)) {
                    filePath += TEST_FILES_FORMAT;
                }

                bufferedWriter = new BufferedWriter(new FileWriter(filePath));

                bufferedWriter.write(content);

                bufferedWriter.close();

            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
}