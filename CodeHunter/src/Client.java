 import java.io.*; 
 import java.net.*;

public class Client{
	private static final String CONNECTED_TO_SERVER_TEXT = "Conectado al servidor.",
								NOT_CONNECTED_TO_SERVER_TEXT = "No se ha establecido conexión con el servidor.",
								SERVER_CLOSED_CONNECTION_TEXT = "El servidor cerró la conexión.",
								CLOSED_CONNECTION = "Conexión cerrada.",
								CLOSE_CONNECTION_ERROR_TEXT = "Error al cerrar la conexión: ";
	
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    // Constructor
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Método para conectar al servidor
    public void connect() throws IOException {
        socket = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        System.out.println(CONNECTED_TO_SERVER_TEXT);
    }

    // Método para enviar un comando al servidor
    public void sendCommand(String command) throws IOException {
        if (output != null) {
            output.println(command);
        } else {
            throw new IOException(NOT_CONNECTED_TO_SERVER_TEXT);
        }
    }

    // Método para recibir la respuesta del servidor
    public String getReply() throws IOException {
        if (input != null) {
            String reply = input.readLine();
            if (reply == null) {
                throw new IOException(SERVER_CLOSED_CONNECTION_TEXT);
            }
            return reply;
        } else {
            throw new IOException(NOT_CONNECTED_TO_SERVER_TEXT);
        }
    }

    public void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            System.out.println(CLOSED_CONNECTION);
        } catch (IOException e) {
            System.err.println(CLOSE_CONNECTION_ERROR_TEXT+ e.getMessage());
        }
    }
}