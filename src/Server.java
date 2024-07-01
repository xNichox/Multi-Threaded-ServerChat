import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server {
    // ATTRIBUTES:
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers;
    private static Server instance;
    private ServerSocket serverSocket;

    // PRIVATE CONSTRUCTOR FOR SINGLETON:
    private Server() throws IOException { // è singleton perché ho un solo server a cui collego più client
        serverSocket = new ServerSocket(PORT);
        clientHandlers = new HashSet<>();
    }

    public static Server getInstance() throws IOException { // prendo l'istanza dell'unico server
        if (instance == null){
            instance = new Server();
        }
        return instance;
    }

    // PUBLIC METHODS:
    public void startServer(){
        try {
            System.out.println("Server started on port " + PORT);

            while (true) { // il server rimane sempre in ascolto
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), clientHandlers); // serverSocket.accept() = server in attesa delle richieste dei client
                clientHandlers.add(clientHandler);
                System.out.println(clientHandlers);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 /*   public static synchronized void broadcastMessage(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) { // non invia il messaggio al client mittente
                client.sendMessage(message);
            }
        }
    } */

    public static void main(String[] args) throws IOException {
        Server server = getInstance();
        server.startServer();
    }
}