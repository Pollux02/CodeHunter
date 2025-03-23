import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.JOptionPane;

public class Server {
    public static void main(String[] args) {
        int puerto = 12345; // Puerto del servidor

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor evaluacion iniciado. Esperando conexiones...");

            while (true) {
                // Aceptar conexión del cliente
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());

                // Crear un hilo para manejar al cliente
                new Thread(new ManejadorCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error iniciando el servidor: " + e.getMessage());
        }
    }
}

class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    
    private static final String ZIP = ".zip",
    		UNZIP_COMMAND = "unzip",
    		MAIN_FUNCTION_NAME = "main",
    		USER_DIR = "user.dir",
    		COMPILE_GCC_COMMAND = "gcc",
    		COMPILE_GPP_COMMAND = "g++",
    		PYTHON_INTERPRETER = "python3",	
    		WALL = "-Wall",
    		WEXTRA = "-Wextra",
    		WSWITCH_DEFAULT = "-Wswitch-default",
    		WFLOAT_EQUAL = "-Wfloat-equal",
    		WUNREACHABLE_CODE = "-Wunreachable-code",
    		WUNUSED_VARIABLE = "-Wunused-variable",
    		WUNINITIALIZED = "-Wuninitialized",
    		EXECUTE_PROGRAM_COMMAND = "./program",
    		PROGRAM_NAME = "program",
            C = "C",
            ERROR = "Error",
            COMPILE_ERROR = "Ha ocurrido un error al compilar: ",
            GET_LEMMATIZATION_FILE = "/getLemmatization.py",
            NON_EXISTENT_MAIN_FAULT = "(-100 pts) No se encontró el archivo ",
            MULTIPLE_VERSION_FAULT = "(-10 pts) Se encontró más de una versión. Versiones extra encontradas: ",
            CODE_NOT_COMPILE_FAULT = "(-100 pts) El código no compila ",
            UNUSED_VARIABLE_FAULT= "Requerimiento de valor agregado B (Variable sin usar) ",
            UNINITIALIZED_FAULT = "Requerimiento de valor agregado K (Variable no inicializada) ",
            FLOAT_EQUAL_FAULT = "Requerimiento de valor agregado K (Trata de comparar flotantes) ",
            SWITCH_DEFAULT_FAULT = "Requerimiento de valor agregado K (Switch sin default) ",
            UNREACHABLE_CODE_FAULT = "Requerimiento de valor agregado K (Código inalcanzable) ",
            INVALID_VARIABLE_FAULT = "La siguiente variable no cumple con el formato: ",
            INVALID_LOGIC_FAULT = "La lógica del programa es incorrecta",
            VARIABLE_NOT_IN_CONTEXT_FAULT = "La siguiente variable no entra en el contexto de la descripción: ";

    private static final String EMPTY_STRING = "",
    				EMPTY_SPACE_STRING = " ",
		    		SERVER_FILES_PATH ="/home/diegoh02/Documents/ServerFiles/",
		    		SEPARATION = ", ",
		    		COMMA = ",",
		    		UNDERSCORE = "_",
		    		FINISHED_TEXT = "finished",
    				NICKNAME_PATTERN = ".*\\d+[ab](.*?)(V\\d+|v\\d+)?$",
    				OUTPUT_PATTERN = "[\\p{L}]+|\\d+\\.\\d+|\\d+|[^\\p{L}\\d\\s]",
    				VARIABLES_NAMES_PATTERN = "\\b(int|float|double|char|long|short|unsigned|bool)\\b\\s+([\\w,\\s]+)",
    				SPECIAL_CHARACTERS_PATTERN = "[,!.?\"]",
    				DIACRITICAL_MARKS_PATTERN = "\\p{InCombiningDiacriticalMarks}+",
    				CAMMEL_CASE_PATTERN = "^[a-z]+([A-Z][a-z]*)*[0-9]*$",
    				SNAKE_CASE_PATTERN = "^[A-Z]+(_[A-Z]+)*$",
    				SPLIT_CAMMEL_CASE_PATTERN =	"(?<!^)(?=[A-Z])|(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)",
    				RESOURCES_DELIMITER = "¤",
    				PROPERTIES_DELIMITER = "¥",
    				COMMAND_WORD_DELIMITER = "°",
 					LINE_DELIMITER = "\n",
 					EQUAL_DELIMITER = "\\=",
 					EMPTY_SPACE_DELIMITER= "\\s+",
 					SEEK_NUMBER = "\\d",
					EQUAL_SIGN = "=",
		    		SEEK_ORDER_SIGN = "=O",
		    		DESTINATION = "-d",
		    		OUTPUT = "-o",
		    		CREATE_FOLDER_COMMAND = "CREATE_FOLDER",
		    		SEND_FILE_COMMAND = "SEND_FILE",
		    		START_PRELIMINARY_EVALUATION_COMMAND = "START_PRELIMINARY_EVALUATION",
		    		START_EVALUATION_COMMAND = "START_EVALUATION",
		    		START_VARIABLES_EVALUATION_COMMAND = "START_VARIABLES_EVALUATION";
    
    private static final int ZERO = 0,
    				ONE = 1,
    				NON_EXISTENT_MAIN_SCORE = 5,
    				CODE_NOT_COMPILE_SCORE = 5,
    				WARNINGS_SCORE = 95,
    				MULTIPLE_VERSION_DEDUCTION = 10;

    List<String>lemmatizedDescription = new ArrayList<>();
    
    String selectedLanguage, mainFileName;

    public ManejadorCliente(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
             PrintWriter output = new PrintWriter(clienteSocket.getOutputStream(), true)) {
        	
        	String clientIP = clienteSocket.getInetAddress().getHostAddress();
            String command;
            
            List<Student>studentsList;
            
            while ((command = input.readLine()) != null) { // Leer comandos mientras el cliente esté conectado
                System.out.println("Comando recibido: " + command);

                // Dividir el comando y el texto
                String[] commandWords = command.split(COMMAND_WORD_DELIMITER); // Divide en dos partes: comando y texto
                String instruction = commandWords[0];

                // Manejar el comando con un switch
                String reply;
                switch (instruction) {
                	case CREATE_FOLDER_COMMAND:
                		recreateFolder(SERVER_FILES_PATH+clientIP);
                		reply = FINISHED_TEXT;
                		break;
                    case SEND_FILE_COMMAND:
                    	String file = commandWords[1];

                        stringToZip(file, SERVER_FILES_PATH+clientIP+"/"+commandWords[2]);
                        
                        reply = FINISHED_TEXT;
                        break;
                    case START_PRELIMINARY_EVALUATION_COMMAND:
                    	String[] zipList;
                    	String mainFileName = commandWords[1];
                    	
                    	zipList = getZipList(SERVER_FILES_PATH+clientIP);
                    	
                    	studentsList = unZip(zipList, SERVER_FILES_PATH+clientIP, mainFileName);
                    	
                    	
                        reply = studentsToString(studentsList);
                        break;
                    case START_EVALUATION_COMMAND:
                    	String testsString = commandWords[1];
                    	
                    	selectedLanguage = commandWords[2];
                    	
                    	studentsList = stringToStudents(commandWords[3]);
                    	
                    	
                    	startReview(testsString, studentsList);
                        reply = studentsToString(studentsList);
                        break;
                    case START_VARIABLES_EVALUATION_COMMAND:
                    	String testDescription = commandWords[1];
                    	
                    	studentsList = stringToStudents(commandWords[2]);
                    	
                    	
                    	startVariablesReview(testDescription, studentsList);
                    	
                        reply = studentsToString(studentsList);
                        break;
                    default:
                        reply = "Error: Comando no reconocido.";
                        break;
                }

                // Enviar reply al cliente
                output.println(reply);
                System.out.println("reply enviado: " + reply);
            }
        } catch (IOException e) {
            System.err.println("Error manejando cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
                System.out.println("Conexión cerrada con el cliente.");
            } catch (IOException e) {
                System.err.println("Error cerrando socket del cliente: " + e.getMessage());
            }
        }
    }
    
    public static void stringToZip(String base64String, String outputZipFilePath) throws IOException {
        byte[] zipBytes = Base64.getDecoder().decode(base64String);
        Files.write(Paths.get(outputZipFilePath), zipBytes);
    }
    
    public static void recreateFolder(String folderPath) {
        File carpeta = new File(folderPath);

        if (carpeta.exists()) {
            // Si la carpeta existe, eliminarla completamente
            deleteFolder(carpeta);
            System.out.println("Se eliminó la carpeta: " + folderPath);
        }

        // Crear la carpeta nuevamente
        if (carpeta.mkdirs()) {
            System.out.println("Se creó la carpeta: " + folderPath);
        } else {
            System.out.println("No se pudo crear la carpeta.");
        }
    }

    public static void deleteFolder(File folder) {
        try {
            Path path = folder.toPath();
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (Exception e) {
            System.out.println("Error al eliminar la carpeta: " + e.getMessage());
        }
    }
    
    private static String[] getZipList(String path) {
        File directory = new File(path);
        
        if (directory.isDirectory()) {
            String[] zipFiles = directory.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(ZIP);
                }
            });

            if (zipFiles != null) {
                Arrays.sort(zipFiles);
            }
            
            return zipFiles;
        } 
        else {
            return null;
        }
    }
    
    private List<Student> unZip(String[] zipList, String path, String mainFileName) {
        String folderName, studentFolderPath, zipPath;
        Student student;
        Process process;
        ProcessBuilder processBuilder;
        File directory;
        
        int exitCode;

        List<Student>studentsList = new ArrayList<>();

        for (String zipName : zipList) {
            folderName = zipName.replaceAll(ZIP, EMPTY_STRING);
            studentFolderPath = path + File.separator + folderName;
            zipPath = path + File.separator + zipName;
            directory = new File(studentFolderPath);
            
            if(!directory.isDirectory()) {
            	processBuilder = new ProcessBuilder(UNZIP_COMMAND, zipPath, DESTINATION, studentFolderPath);

                try {
                    process = processBuilder.start();
                    exitCode = process.waitFor(); 

                    if (exitCode == ZERO) {	
                        student = preliminaryEvaluation(studentsList, studentFolderPath, folderName, mainFileName);
                        studentsList.add(student);
                    } 
                    else {
                        //JOptionPane.showMessageDialog(null, UNZIP_ERROR + exitCode, ERROR, JOptionPane.ERROR_MESSAGE);
                    }
                } 
                catch (IOException | InterruptedException e) {
                    //JOptionPane.showMessageDialog(null, UNZIP_ERROR + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Restablece el estado de interrupción del hilo
                }
            }
            else {
            	student = preliminaryEvaluation(studentsList, studentFolderPath, folderName, mainFileName);
                studentsList.add(student);
            }
        }
        
        return studentsList;
    }
    
    private Student preliminaryEvaluation(List<Student>studentsList, String studentFolderPath, String folderName, String mainFileName){
    	Student student = new Student(), lastStudent = new Student();
    	String codePath;
		
		student.setNickName(folderName.replaceAll(NICKNAME_PATTERN, "$1"));
		
		codePath = getMainFile(studentFolderPath, mainFileName);
		
		if(codePath != null) {
			student.setCodePath(codePath);
		}
		else {
			student.setScore(NON_EXISTENT_MAIN_SCORE);
			addStudentFault(student, NON_EXISTENT_MAIN_FAULT);
		}
		
		if(studentsList.size()>ZERO && studentsList.get(studentsList.size()-1).getNickName().equals(student.getNickName())) {
			lastStudent = studentsList.get(studentsList.size()-1);
			
			student.setNumVersions(lastStudent.getNumVersions()+1);
			
			if(student.getScore()>NON_EXISTENT_MAIN_SCORE) {
				student.setScore(student.getScore()-MULTIPLE_VERSION_DEDUCTION*student.getNumVersions());
				addStudentFault(student, MULTIPLE_VERSION_FAULT + student.getNumVersions());
			}
			else {
				addStudentFault(student, MULTIPLE_VERSION_FAULT + student.getNumVersions());
			}

			studentsList.remove(lastStudent);
		}
		
		return student;
    }
    
    private static String getMainFile(String path, String mainFileName)
    {
    	File directory = new File(path);
        File[] filesList = directory.listFiles();

        if (filesList != null) {
            for (File file : filesList) {
                if (file.isDirectory()) {

                    String rutaArchivoEncontrado = getMainFile(file.getAbsolutePath(), mainFileName);
                    if (rutaArchivoEncontrado != null) {
                        return rutaArchivoEncontrado; 
                    }
                } 
                else {
                    if (file.getName().equals(mainFileName)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }

        return null;
    }
    
    private void addStudentFault(Student student, String Fault) {
    	String studentFaults = student.getFaults();
    	
    	if(studentFaults.equals(EMPTY_STRING)) {
    		student.setFaults(Fault);
    	}
    	else {
    		student.setFaults(studentFaults + SEPARATION + Fault);
    	}
    }
    
    private void startReview(String testsString, List<Student>studentsList) {
    	String testContent = testsString;
    	String[] lines;
    	String[] linesWithoutDescription;
    	List<Test>testList;

		lines = testContent.split(RESOURCES_DELIMITER);

    	linesWithoutDescription = new String[lines.length - ONE];

        System.arraycopy(lines, ONE, linesWithoutDescription, ZERO, lines.length - ONE);
        
    	testList = getTests(linesWithoutDescription);
    	
    	reviewCodes(testList, studentsList);
    }
    
    private List<Test> getTests(String[] lines)
	{
		boolean isOutput = false;
	    int idTest = ZERO;
	    
	    String[]testLines;
	    Test test;
	    
	    List<Test> testList = new ArrayList<Test>();
	    List<String> inputs;
	    List<String> outputs;

		for (String line : lines){
		    	testLines = line.split(PROPERTIES_DELIMITER);
		    	test = new Test(idTest);
		    	isOutput = false;
		    	inputs = new ArrayList<String>();
			    outputs = new ArrayList<String>();

		    	for (String testLine : testLines){
		    		if(testLine.equals(EQUAL_SIGN)){
		    			isOutput = true;
		    		}
		    		else if(testLine.equals(SEEK_ORDER_SIGN)){
		    			isOutput = true;
		    			test.setSeekOrder(true);
		    		}
		    		else{
		    			if(!testLine.equals(EMPTY_STRING)){
			    			if(isOutput){
			    				if(testLine.equals(EQUAL_DELIMITER)){
			    					outputs.add(EQUAL_SIGN);
			    				}
			    				else{
			    					outputs.add(testLine);
			    				}
			    			}
			    			else{
			    				if(testLine.equals(EQUAL_DELIMITER)){			    					
			    					inputs.add(EQUAL_SIGN);
			    				}
			    				else{			    					
			    					inputs.add(testLine);
			    				}
			    			}
		    			}
		    		}
		        }
		    	if(inputs.size()>ZERO || outputs.size()>ZERO){    		
			    	test.setInputs(inputs);
					test.setOutputs(outputs);
					
					testList.add(test);
					
					idTest++;
		    	}
	        }
		return testList;
	}
    
    private void reviewCodes(List<Test>testList, List<Student>studentsList) {
    	List<Variable>studentVariables;
    	String output, errorMessage, studentCodePath;
    	
    	for(Student student : studentsList) {	
    		studentCodePath = student.getCodePath();
    		
    		if(!studentCodePath.equals(EMPTY_STRING)) {
    			errorMessage = compile(studentCodePath);
    	    	
    	    	if(errorMessage != null) {
    		    	if(errorMessage.contains(WUNUSED_VARIABLE)) {
    			    	addStudentFault(student, UNUSED_VARIABLE_FAULT);
    			    	student.setScore(WARNINGS_SCORE);
    			    }
    			    
    			    if(errorMessage.contains(WUNINITIALIZED)) {
    			    	addStudentFault(student, UNINITIALIZED_FAULT);
    			    	
    			    	if(student.getScore() > WARNINGS_SCORE) {
    			    		student.setScore(WARNINGS_SCORE);
    			    	}
    			    }
    			    
    			    if(errorMessage.contains(WFLOAT_EQUAL)) {
    			    	addStudentFault(student, FLOAT_EQUAL_FAULT);
    			    	
    			    	if(student.getScore() > WARNINGS_SCORE) {
    			    		student.setScore(WARNINGS_SCORE);
    			    	}
    			    }
    			    
    			    if(errorMessage.contains(WSWITCH_DEFAULT)) {
    			    	addStudentFault(student, SWITCH_DEFAULT_FAULT);
    			    	
    			    	if(student.getScore() > WARNINGS_SCORE) {
    			    		student.setScore(WARNINGS_SCORE);
    			    	}
    			    }
    			    
    			    if(errorMessage.contains(WUNREACHABLE_CODE)) {
    			    	addStudentFault(student, UNREACHABLE_CODE_FAULT);
    			    	
    			    	if(student.getScore() > WARNINGS_SCORE) {
    			    		student.setScore(WARNINGS_SCORE);
    			    	}
    			    }
    			    
    	    		for(Test test : testList) {
    		    		output = executeProgram(test);
    			        System.out.println(output);
    			        
    			        if(!areAllTheOutputs(output, test)) {
    			        	student.setScore(NON_EXISTENT_MAIN_SCORE);
    			        	
    			        	addStudentFault(student, INVALID_LOGIC_FAULT);
    			        	break;
    			        }
    		    	}
    	    		
    	    		studentVariables = extractVariableNames(student.getCodePath());
    	    		
    	    		student.setVariables(studentVariables);
    	    	}
    	    	else {
    	    		student.setScore(CODE_NOT_COMPILE_SCORE);
    	    		addStudentFault(student, CODE_NOT_COMPILE_FAULT);
    	    	}
    		}
    	}
    }
    
    private String compile(String codePath) {
    	Process process;
        ProcessBuilder processBuilder;
        BufferedReader errorReader;
        StringBuilder errorMessage;
        String line;
        
        int exitCode;
        
        if(selectedLanguage.equals(C)) {
        	processBuilder = new ProcessBuilder(COMPILE_GCC_COMMAND, 
        										WALL, 
        										WEXTRA, 
        										WSWITCH_DEFAULT, 
        										WFLOAT_EQUAL, 
        										WUNREACHABLE_CODE, 
        										OUTPUT, 
        										PROGRAM_NAME, 
        										codePath);
        }
        else {
        	processBuilder = new ProcessBuilder(COMPILE_GPP_COMMAND, 		
							        			WALL, 
												WEXTRA, 
												WSWITCH_DEFAULT, 
												WFLOAT_EQUAL, 
												WUNREACHABLE_CODE, 
												OUTPUT, 
												PROGRAM_NAME, 
												codePath);
        }
    	
        try {
            process = processBuilder.start();
            
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            errorMessage = new StringBuilder(EMPTY_STRING);

            while ((line = errorReader.readLine()) != null) {
                errorMessage.append(line).append(LINE_DELIMITER);
            }
            
            exitCode = process.waitFor(); 

            if (exitCode == ZERO) {
            	return errorMessage.toString();
            } 
            else {
            	return null;
            }
        } 
        catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(null, COMPILE_ERROR + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
            
            e.printStackTrace();
            
            Thread.currentThread().interrupt();
            
            return null;
        }
    }
    
    private static String executeProgram(Test test) {
	    StringBuilder outputBuilder = new StringBuilder();
	    ProcessBuilder processBuilder;
	    Process process;
	    
	    InputStream inputStream;
        OutputStream outputStream;
        
        BufferedReader reader;
        PrintWriter writer;
        
        Thread inputThread, outputThread;
        
        int exitCode;

	    try {
	        processBuilder = new ProcessBuilder(EXECUTE_PROGRAM_COMMAND);
	        processBuilder.redirectErrorStream(true);

	        process = processBuilder.start();
	        inputStream = process.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(inputStream));
	        outputStream = process.getOutputStream();
	        writer = new PrintWriter(outputStream);

	        inputThread = new Thread(() -> {
	            for (String input : test.getInputs()) {
	                writer.write(input + LINE_DELIMITER );
	                writer.flush();
	            }
	            writer.close();
	        });

	        outputThread = new Thread(() -> {
	            try {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    outputBuilder.append(line).append(LINE_DELIMITER);
	                }
	                reader.close();
	            } 
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	        });

	        inputThread.start();
	        outputThread.start();

	        inputThread.join();
	        outputThread.join();

	        exitCode = process.waitFor();
	        System.out.println("El programa en C ha terminado con código de salida: " + exitCode);
	    } 
	    catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	    }

	    return outputBuilder.toString();
    }
    
    private static boolean areAllTheOutputs(String output, Test test) {
    	Pattern pattern = Pattern.compile(OUTPUT_PATTERN);
        Matcher matcher = pattern.matcher(output);

        List<String> wordsList = new ArrayList<>();
        List<String> outputsList = new ArrayList<>(test.getOutputs());
        
        while (matcher.find()) {
            wordsList.add(matcher.group());
        }
        
        if(test.getSeekOrder()) {
        	for(String word : wordsList) {
        		if(outputsList.get(ZERO).equals(SEEK_NUMBER)) {
        			if(isNumber(word)) {
        				outputsList.remove(ZERO);
        			}
        		}
        		else {
        			if(word.equals(outputsList.get(ZERO))) {
            			outputsList.remove(ZERO);
            		}
        		}
        	}
        }
        else {
        	for(String word : wordsList) {
        		if(outputsList.contains(word)) {
        			outputsList.remove(word);
        		}
        		else {
        			if(isNumber(word) && outputsList.contains(SEEK_NUMBER)) {
        				outputsList.remove(SEEK_NUMBER);
        			}
        		}
        	}
        }
        
        return outputsList.size() == ZERO;
    }
    
    private static boolean isNumber(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(word);
            return true;
        } 
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static List<Variable> extractVariableNames(String filePath) {
        List<Variable> variablesList= new ArrayList<Variable>();
        
        Pattern pattern = Pattern.compile(VARIABLES_NAMES_PATTERN);
        String line, variablePart;
        String[] variables;
        Matcher matcher;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                matcher = pattern.matcher(line);
                while (matcher.find()) {
                    variablePart = matcher.group(2);
                    
                    variables = variablePart.split(COMMA);
                    for (String variable : variables) {
                        variablesList.add(new Variable(variable.trim()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return variablesList;
    }
    
    private void startVariablesReview(String testDescription, List<Student>studentsList) {
    	int wordsNumber;
    	String variableWords;
    	String variableName, allVariablesWords = EMPTY_STRING;
    	
    	testDescription = testDescription.replaceAll(SPECIAL_CHARACTERS_PATTERN , EMPTY_STRING);
    	
    	testDescription = testDescription.toLowerCase();
    	
    	testDescription = Normalizer.normalize(testDescription, Normalizer.Form.NFD);
        testDescription = testDescription.replaceAll(DIACRITICAL_MARKS_PATTERN, EMPTY_STRING);
    	
    	lemmatizedDescription = getLemmatization(testDescription);
    	
    	for(Student student : studentsList) {
    		
    		allVariablesWords = EMPTY_STRING;
    		
    		for(Variable variable : student.getVariables()) {
    			variableName = variable.getName();
    			
    			if(isCamelCase(variableName)) {
    				variableWords = splitCamelCaseToString(variableName);
    				
    				allVariablesWords = allVariablesWords + EMPTY_SPACE_STRING + variableWords;
    				
    				wordsNumber = countWords(variableWords);
    				
    				variable.setWords(variableWords);
    				variable.setWordsNumber(wordsNumber);
    			}
    			else if(isSnakeCase(variableName)) {
    				variableWords = splitSnakeCaseToString(variableName);
    				
    				allVariablesWords = allVariablesWords + EMPTY_SPACE_STRING + variableWords;
    				
    				wordsNumber = countWords(variableWords);
    				
    				variable.setWords(variableWords);
    				variable.setWordsNumber(wordsNumber);
    			}
    			else {
    				addStudentFault(student, INVALID_VARIABLE_FAULT + variable.getName());
    				variable.setIsValid(false);
    			}
    		}
    		
    		if(!allVariablesWords.equals(EMPTY_STRING)) {
    			getLemmatizedVariables(allVariablesWords, student.getVariables());
    			
    			evaluateVariables(student);
    		}
    	}
    }
    
    private List<String> getLemmatization(String text) {
    	String projectCurrentDirectory = System.getProperty(USER_DIR);
    	String pythonScriptPath = projectCurrentDirectory+GET_LEMMATIZATION_FILE;
        String line;
        
    	ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_INTERPRETER, pythonScriptPath, text);
        Process process;
        BufferedReader reader, errorReader;

        List<String>lemmatizedWords = new ArrayList<>();

        int exitCode;

        try {
            process = processBuilder.start();

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            while ((line = reader.readLine()) != null) {
        		lemmatizedWords.add(line);
            }

            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            exitCode = process.waitFor();
            System.out.println("El proceso terminó con código: " + exitCode);
            
            return lemmatizedWords;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean isCamelCase(String variable) {
        return variable.matches(CAMMEL_CASE_PATTERN);
    }
    
    public static boolean isSnakeCase(String variable) {
        return variable.matches(SNAKE_CASE_PATTERN);
    }
    
    public static String splitCamelCaseToString(String variable) {
        String[] wordsArray = variable.split(SPLIT_CAMMEL_CASE_PATTERN);

        StringBuilder result = new StringBuilder();
        for (String word : wordsArray) {
            result.append(word).append(EMPTY_SPACE_STRING);
        }

        return result.toString().trim();
    }
    
    public static String splitSnakeCaseToString(String variable) {
    	variable = variable.toLowerCase();
    	
        return variable.replace(UNDERSCORE, EMPTY_SPACE_STRING);
    }
    
    public static int countWords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return ZERO;
        }

        String[] words = input.trim().split(EMPTY_SPACE_DELIMITER);
        
        if(words.length == ZERO) {
        	return ONE;
        }
        else {
        	return words.length;
        }
    }
    
    private void getLemmatizedVariables(String allVariablesWords, List<Variable>studentVariables){
    	int i;
    	
    	List<String>lemmatizedVariables = getLemmatization(allVariablesWords);
    	
    	for(Variable variable: studentVariables) {
    		if(variable.getIsValid()) {
    			for(i = ZERO; i< variable.getWordsNumber() ; i++) {
        			variable.getLemmatizedWords().add(lemmatizedVariables.remove(ZERO));
        		}
    		}
    	}
    }
    
    private void evaluateVariables(Student student) {
    	for(Variable variable : student.getVariables()) {
    		if(variable.getIsValid()) {
    			for(String word : variable.getLemmatizedWords()) {
    				if(!variable.getIsInContext()) {
    					if(word.equals(MAIN_FUNCTION_NAME)) {
    						variable.setIsInContext(true);
    						break;
    					}
    					else if(word.length() >= 3) {
            				for(String descriptionWord : lemmatizedDescription) {
            					if(descriptionWord.contains(word)) {
            						variable.setIsInContext(true);
            						break;
            					}
            				}
            			}
    					else {
    						for(String descriptionWord : lemmatizedDescription) {
            					if(descriptionWord.equals(word)) {
            						variable.setIsInContext(true);
            						break;
            					}
            				}
    					}
    				}
        		}
    			
    			if(!variable.getIsInContext()) {
    				addStudentFault(student, VARIABLE_NOT_IN_CONTEXT_FAULT  + variable.getName());
    			}
    		}
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